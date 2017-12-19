package com.ideotic.edioticideas.aaya;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.internal.Command;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Mukul on 16-05-2016.
 */
public class GmailModule extends Activity
        implements EasyPermissions.PermissionCallbacks, View.OnClickListener, TextToSpeech.OnInitListener {


    GoogleAccountCredential mCredential;
    public static String mOutputText;
    ProgressDialog mProgress;
    private TextView tv_show, dateView;
    private ImageButton b_listen;
    private Button b_help;

    // Gmial Related Var
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String PREF_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_LABELS, GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_MODIFY};

    //voice related Var
    private TextToSpeech tts;

    String date;
    String commands;
    String extras = "";


    String extras_subject;
    String extras_to;
    String extras_body;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    /**
     * Create the main activity.
     *
     * @param savedInstanceState previously saved instance data.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmail_module);
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Gmail API ...");

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
        java.text.DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        date = df.format(Calendar.getInstance().getTime());
        init();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void init() {
        tv_show = (TextView) findViewById(R.id.textViewShowGmail);
        b_listen = (ImageButton) findViewById(R.id.imageButtonSpeakGmail);
        b_help = (Button) findViewById(R.id.buttonHelpGmail);
        b_listen.setOnClickListener(this);
        b_help.setOnClickListener(this);
        tts = new TextToSpeech(this, this);
        dateView = (TextView) findViewById(R.id.textView7DateGmail);
        dateView.setText(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonSpeakGmail:
                promptSpeechInput();
                break;
            case R.id.buttonHelpGmail:
                MainActivity.module = "gmail";
                Toast.makeText(getBaseContext(), "Help Module", Toast.LENGTH_SHORT).show();
                HelpFrag frag = new HelpFrag();
                frag.show(getFragmentManager(), null);
                break;
        }
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    private final int MAIL = 10101;

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (!isDeviceOnline()) {
            mOutputText = "No network connection available.";
        } else {
            chooseAccount();

            switch (commands) {
                case Commands.MAIL_COMPOSE_MAIL:
                    Intent i = new Intent(this, ComposeMail.class);
                    startActivityForResult(i, MAIL);
                    break;
                case Commands.MAIL_SEND:
                    new SendTask(mCredential).execute();
                    break;
                case Commands.helpModule:
                    MainActivity.module = "gmail";
                    Toast.makeText(getBaseContext(), "Help Module", Toast.LENGTH_SHORT).show();
                    HelpFrag frag = new HelpFrag();
                    frag.show(getFragmentManager(), null);
                default:
                    new MakeRequestTask(mCredential).execute(commands, extras);
                    break;
            }

        }
    }

    /**
     * Attempts to set the account used with the API credentials. If an account
     * name was previously saved it will use that one; otherwise an account
     * picker dialog will be shown to the user. Note that the setting the
     * account to use with the credentials object requires the app to have the
     * GET_ACCOUNTS permission, which is requested here if it is not already
     * present. The AfterPermissionGranted annotation indicates that this
     * function will be rerun automatically whenever the GET_ACCOUNTS permission
     * is granted.
     */
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);


            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode code indicating the result of the incoming
     * activity result.
     * @param data Intent (containing result data) returned by incoming
     * activity result.
     */
    String name;

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText =
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.";
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    // name = data.getStringExtra(AccountManager.KEY_USERDATA);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();

                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        //editor.putString(PREF_NAME,name);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;

            case REQ_CODE:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text_voice = result.get(0);
                    tv_show.setText(text_voice);
                    try {
                        Thread.sleep(1000);
                        speakOut(tv_show.getText().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String[] a = Commands.filterCommands(text_voice);
                    commands = a[0];
                    extras = a[1];
                    getResultsFromApi();
                }
                break;


            case MAIL:
                if (resultCode == RESULT_OK) {

                    extras_to = data.getStringExtra(Commands.MAIL_TO);
                    extras_body = data.getStringExtra(Commands.MAIL_BODY);
                    extras_subject = data.getStringExtra(Commands.MAIL_SUBJECT);
                    commands = Commands.MAIL_SEND;
                    getResultsFromApi();
                }
        }


    }

    /**
     * Respond to requests for permissions at runtime for API 23 and above.
     *
     * @param requestCode  The request code passed in
     *                     requestPermissions(android.app.Activity, String, int, String[])
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    // voice related Code (BEGINING)
    private final int REQ_CODE = 100;

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }


    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tv_show.setText("TTS  This Language is not supported");
            } else {
                //bl.setEnabled(true);
                speakOut(tv_show.getText().toString());
            }

        } else {
            tv_show.setText("TTS Initilization Failed!");
        }

    }

    @Override
    public void onDestroy() {
        // Shuts Down TTS
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();

    }

    // Voice Realted commands (Ended)

    /**
     * An asynchronous task that handles the Gmail API call.
     * Placing the API calls in their own task ensures the UI stays responsive.
     */
    String next = null;


    //  ASYNC_TASK CLASS
    private class MakeRequestTask extends AsyncTask<String, Void, List<String>> {
        private Gmail mService = null;
        private Exception mLastError = null;
        private long number_of_msgs = 3;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         *
         * @param params no parameters needed for this task.
         */

        @Override
        protected List<String> doInBackground(String... params) {
            try {
                switch (params[0]) {
                    case Commands.MAIL_FETCH_MAILS:
                        return getMessage(Commands.MAIL_FETCH_MAILS);
                    case Commands.MAIL_FEtCH_LABELS:

                        List<String> n = new ArrayList<>();
                        n.add("INBOX");
                        n.add("UNREAD");
                        n.add("CATEGORY_PERSONAL");
                        return n;
                    case Commands.MAIL_SEARCH_SUBJECT:
                        return getMessage(Commands.MAIL_SEARCH_SUBJECT);
                    case Commands.MAIL_SEARCH_LABELS:
                        extras = "INBOX";
                        if (extras == "INBOX")
                            extras = "INBOX";

                        else if (extras == "CATEGORY_SOCIAL")
                            extras = "CATEGORY_SOCIAL";

                        else if (extras == "UNREAD")
                            extras = "UNREAD";
                        else {
                            extras = "ALL";
                        }
                        return getMessage(Commands.MAIL_FETCH_MAILS);
                    default:
                        return null;
                }
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        private int[] getheader(Message list, String... p) {

            int[] index = new int[p.length];

            for (int i = 0; i < list.getPayload().getHeaders().size(); i++) {

                if (list.getPayload().getHeaders().get(i).getName().toString().equals(p[0])) {
                    System.out.print(i + list.getPayload().getHeaders().get(i).toString());
                    index[0] = i;
                }
                if (list.getPayload().getHeaders().get(i).getName().toString().equals(p[1])) {
                    System.out.print(i + list.getPayload().getHeaders().get(i).toString());
                    index[1] = i;
                }
                if (list.getPayload().getHeaders().get(i).getName().toString().equals(p[2])) {
                    System.out.print(i + list.getPayload().getHeaders().get(i).toString());
                    index[2] = i;
                }
            }
            return index;
        }

        //get messages, subjects, body using message id
        public List<String> getMessage(String type)
                throws IOException {
            List<String> messages = new ArrayList<>();
            List<Message> list = listMessagesWithLabels(mService, "me", type);
            for (Message msg : list) {

                //   messages.add(getPreferences(Context.MODE_PRIVATE).getString(PREF_NAME,null));
                Message message = mService.users().messages().get("me", msg.getId()).setFormat("full").setMetadataHeaders(messages).execute();
                int[] n = getheader(message, "Subject", "Date", "From");
                messages.add(message.getPayload().getHeaders().get(n[0]).getName() + "    " + message.getPayload().getHeaders().get(n[0]).getValue());
                messages.add(message.getPayload().getHeaders().get(n[1]).getName() + "    " + message.getPayload().getHeaders().get(n[1]).getValue());
                messages.add(message.getPayload().getHeaders().get(n[2]).getName() + "    " + message.getPayload().getHeaders().get(n[2]).getValue());
                messages.add("Snippet     " + message.getSnippet().toString() + "\n\n\n");
            }
            return messages;

        }


        // returns message id(s)
        public List<Message> listMessagesWithLabels(Gmail service, String userId, String type)
                throws IOException {

            ListMessagesResponse response;
            List<Message> messages;
            List<String> labelIds;
            switch (type) {
                case Commands.MAIL_SEARCH_SUBJECT:
                    labelIds = new ArrayList<>();
                    //labelIds.add("CATEGORY_PERSONAL");
                    response = mService.users().messages().list("me").setLabelIds(labelIds).setQ("subject:" + extras).execute();
                    messages = new ArrayList<>();
                    next = response.getNextPageToken();
                    messages.addAll(response.getMessages());
                    return messages;
                case Commands.MAIL_FETCH_MAILS:
                    labelIds = new ArrayList<String>();
                    if (extras == "INBOX")
                        labelIds.add("INBOX");
                    else if (extras == "CATEGORY_SOCIAL")
                        labelIds.add("CATEGORY_SOCIAL");
                    else if (extras == "UNREAD")
                        labelIds.add("UNREAD");
                    else {
                        // labelIds.add("INBOX");
                        //labelIds.add("CATEGORY_SOCIAL");
                        // labelIds.add("UNREAD");
                    }
                    response = mService.users().messages().list("me").setLabelIds(labelIds).setMaxResults(number_of_msgs).execute();
                    next = response.getNextPageToken();

                    messages = new ArrayList<Message>();
                    messages.addAll(response.getMessages());
                    return messages;

                case Commands.MAIL_NEXT:
                    response = mService.users().messages().list("me").setQ("subject:" + "Hello").setPageToken(next).execute();
                    messages = new ArrayList<Message>();
                    messages.addAll(response.getMessages());
                    return messages;
                default:
                    return null;
            }
        }


        /**
         * Fetch a list of Gmail labels attached to the specified account.
         *
         * @return List of Strings labels.
         * @throws IOException
         */
        private List<String> getDataFromApi()
                throws IOException {

            // Get the labels in the user's account.
            String user = "me";
            List<String> labels = new ArrayList<String>();
            ListLabelsResponse listResponse =
                    mService.users().labels().list(user).execute();
            for (Label label : listResponse.getLabels()) {
                labels.add(label.getId());
            }
            return labels;
        }

        @Override
        protected void onPreExecute() {
            mOutputText = "";
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                mOutputText = "No results returned.";
            } else {
                output.add(0, "Mails From Gmail\n");
                mOutputText = TextUtils.join("\n", output);
                Display_msgs m = new Display_msgs();

                m.show(getFragmentManager(), null);
            }
        }


        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GmailModule.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText = "The following error occurred:\n"
                            + mLastError.getMessage();
                }
            } else {
                mOutputText = "Request cancelled.";
            }
        }
    }


    private class SendTask extends AsyncTask<String, Void, Void> {
        private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public SendTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android Quickstart")
                    .build();
        }

        /**
         * Background task to call Gmail API.
         *
         * @param params no parameters needed for this task.
         */
        @Override
        protected Void doInBackground(String... params) {
            try {
                sendMail(extras_to, "me", extras_subject, extras_body);
                return null;
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                cancel(true);
                return null;
            }
        }


        public void sendMail(String sender, String receiver, String subject, String content) throws MessagingException, IOException {

            MimeMessage msg = createEmail(sender, receiver, subject, content);
            sendMessage(mService, "me", msg);

        }

        /**
         * Create a MimeMessage using the parameters provided.
         *
         * @param to       Email address of the receiver.
         * @param from     Email address of the sender, the mailbox account.
         * @param subject  Subject of the email.
         * @param bodyText Body text of the email.
         * @return MimeMessage to be used to send email.
         * @throws MessagingException
         */
        public MimeMessage createEmail(String to, String from, String subject,
                                       String bodyText) throws MessagingException {
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);
            InternetAddress tAddress = new InternetAddress(to);
            InternetAddress fAddress = new InternetAddress(from);

            email.setFrom(new InternetAddress(from));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(to));
            email.setSubject(subject);
            email.setText(bodyText);
            return email;
        }


        /**
         * Create a Message from an email
         *
         * @param email Email to be set to raw of message
         * @return Message containing base64url encoded email.
         * @throws IOException
         * @throws MessagingException
         */
        public Message createMessageWithEmail(MimeMessage email)
                throws MessagingException, IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            email.writeTo(bytes);
            String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
            Message message = new Message();
            message.setRaw(encodedEmail);
            return message;
        }


        /**
         * Send an email from the user's mailbox to its recipient.
         *
         * @param service Authorized Gmail API instance.
         * @param userId  User's email address. The special value "me"
         *                can be used to indicate the authenticated user.
         * @param email   Email to be sent.
         * @throws MessagingException
         * @throws IOException
         */
        public void sendMessage(Gmail service, String userId, MimeMessage email)
                throws MessagingException, IOException {
            Message message = createMessageWithEmail(email);
            message = service.users().messages().send(userId, message).execute();

            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());


        }


        /**
         * Fetch a list of Gmail labels attached to the specified account.
         *
         * @return List of Strings labels.
         * @throws IOException
         */


        @Override
        protected void onPreExecute() {
            mOutputText = ("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mProgress.hide();
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            GmailModule.this.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText = "The following error occurred:\n"
                            + mLastError.getMessage();
                }
            } else {
                mOutputText = ("Request cancelled.");
            }
        }
    }

}
