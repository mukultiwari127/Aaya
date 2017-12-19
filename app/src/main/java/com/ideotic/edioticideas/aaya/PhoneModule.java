package com.ideotic.edioticideas.aaya;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Mukul on 16-05-2016.
 */
public class PhoneModule extends Activity implements TextToSpeech.OnInitListener {

    ProgressDialog mProgress;
    String extras = "";
    String check;
    TextView m, dateView;
    TextToSpeech tts;
    public static String result;
    String COMMAND = "";
    ImageButton b;
    Button hlp;
    String number, date;
    final int SMS_CODE = 10001;
    final int REQ_CODE = 100;
    Intent callIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_module);
        mProgress = new ProgressDialog(this);
        m = (TextView) findViewById(R.id.textViewShowPhone);
        mProgress.setMessage("Fetching ....");
        hlp = (Button) findViewById(R.id.buttonHelpPhone);
        tts = new TextToSpeech(this, this);
        b = (ImageButton) findViewById(R.id.imageButtonSpeakPhone);
        dateView = (TextView) findViewById(R.id.textView7datephone);
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        date = df.format(Calendar.getInstance().getTime());
        dateView.setText(date);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
        hlp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.module = "phone";
                HelpFrag frag = new HelpFrag();
                frag.show(getFragmentManager(), null);
            }
        });
    }


    //Which Functionality
    private void launchModule() {
        if (COMMAND.equals(Commands.CALL))
            call();
        else if (COMMAND.equals(Commands.SMS_SEND)) {
            Intent intent = new Intent(PhoneModule.this, ComposeMail.class);
            startActivityForResult(intent, SMS_CODE);
        } else if (COMMAND.equals(Commands.PHONE_HELP)) {
            MainActivity.module = "phone";
            HelpFrag frag = new HelpFrag();
            frag.show(getFragmentManager(), null);
        } else
            new myTask().execute(COMMAND);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String[] a = Commands.filterCommands(result.get(0));
                    COMMAND = a[0];
                    extras = a[1];
                    check = a[2];
                    m.setText(COMMAND + " " + extras);
                    launchModule();
                }
                break;
            }
            case SMS_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    String body = "Subject" + data.getStringExtra(Commands.MAIL_SUBJECT) + "\n" + data.getStringExtra(Commands.MAIL_BODY);
                    String to = data.getStringExtra(Commands.MAIL_TO);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(to, null, body, null, null);
                }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String call() {

        if (check == null) {
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + extras));
            startActivity(callIntent);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                        10);
                return null;
            }
        } else {
            Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};

            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + extras + "%'";
            Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);
            int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            people.moveToFirst();
            try {
                do {
                    number = people.getString(indexNumber);
                    if (number.contains("+91")) {
                        number = number.replace("+91", "");
                    }

                } while (people.moveToNext() && people.getPosition() != 1);
            }catch (Exception ex){
                Toast.makeText(this,"Sorry no such contact",Toast.LENGTH_SHORT);
            }
            callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
        }
        if(number != null) {
            startActivity(callIntent);
        }
        return null;
    }

    public static final int SMS = 111111111;

/*
    private void get(Cursor cursor , String... name)
    {
        List<Integer> idx = new ArrayList<>();
        for(String n : name)
        {
            for (int i = 0; i < cursor.getCount();i++)
            {
                if(n.equals(cursor.get))
            }
        }
    }
*/

    class myTask extends AsyncTask<String, Void, String> {
        String n;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {

            switch (params[0]) {
                case Commands.CALL_LOG:
                    n = callLogs();
                    break;
                case Commands.SMS:
                    n = sms();
                    break;
                case Commands.CALL:
                    n = call();
                    break;
                case Commands.CONTACTS:
                    n = contacts();
                    break;
                case Commands.SMS_SEND:
                    sendMessage();
                    break;
                default:
                    n = "Invalid Command Please say help for the commands";
            }
            return n;
        }


        //Send Message

        public void sendMessage() {
            Intent i = new Intent(PhoneModule.this, ComposeMail.class);
            startActivityForResult(i, SMS);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        private String contacts() {
            String nameall = "";
            Uri uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};

            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + extras + "%'";
            Cursor people = getContentResolver().query(uri, projection, selection, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);


            int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            people.moveToFirst();
            try {
                do {
                    String name = people.getString(indexName);
                    String numberr = people.getString(indexNumber);
                    if (numberr.contains("+91")) {
                        numberr = numberr.replace("+91", "0");
                    }
                    nameall = nameall + name + "\n" + numberr + "\n\n\n\n";
                    // Do work...
                } while (people.moveToNext() && people.getPosition() != 5);
            }catch (Exception ex){
                nameall = "Sorry no such contact found";
            }
            return nameall;
        }


        private String callLogs() {
            Cursor c = getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, CallLog.Calls.DATE + " DESC");
            SimpleDateFormat d = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            String details = "";
            if (c.moveToFirst()) {
                do {

                    String num = c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
                    if (num.contains("+91")) {
                        num = num.replace("+91", "0");
                    }
                    java.util.Date date = new Date(c.getLong(c.getColumnIndex("date")));
                    String name = c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
                    if (name == null)
                        name = "unknown";
                    long t = Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DURATION)));
                    String duration = "";
                    duration += String.valueOf(t / 60) + " minutes ";
                    duration += String.valueOf(t % 60) + " seconds ";
                    int type = Integer.parseInt(c.getString(c.getColumnIndex(CallLog.Calls.TYPE)));

                    details = details + num + "\n" + name + "\n" + duration + "\n" + d.format(date) + "\n\n\n";
                } while (c.moveToNext() && c.getPosition() != 10);
            }

            return details;
        }

        private String sms() {

            SimpleDateFormat d = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
            String msgData = "";
            int i = 0;
            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    msgData += " " + "From:   " + cursor.getString(cursor.getColumnIndex("address")) + "\n";
                    java.util.Date date = new Date(cursor.getLong(cursor.getColumnIndex("date")));
                    msgData += " " + "Date:   " + d.format(date) + "\n";
                    msgData += " " + "Body:   " + cursor.getString(cursor.getColumnIndex("body")) + "\n";
                    msgData = msgData + "\n\n\n\n";
                    i++;
                } while (cursor.moveToNext() && i < 5);
            } else {
                // empty box, no SMS
            }
            return msgData;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mProgress.hide();
            result = n;
            PhoneFragment n = new PhoneFragment();
            n.show(getFragmentManager(), "ss");
        }
    }

    /**
     * Showing google speech input dialog
     */
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


    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
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
}


