package com.ideotic.edioticideas.aaya;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mukul on 16-05-2016.
 */
public class ComposeMail extends AppCompatActivity implements TextToSpeech.OnInitListener,Runnable{

    TextToSpeech tts;
    EditText et_to,et_subject,et_body;
    String result=Commands.MAIL_TO;
    Bundle bundle;

    Thread t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compose_mails);
        init();
    }


    private void init(){
        et_body = (EditText) findViewById(R.id.et_body);
        et_subject = (EditText) findViewById(R.id.et_subject);
        et_to = (EditText) findViewById(R.id.et_to);
        tts = new TextToSpeech(this,this);
        t = new Thread(this);
        bundle = new Bundle();
        Button b = (Button) findViewById(R.id.b_send);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakOut(Commands.MAIL_SEND);
                Intent i = new Intent();
                i.putExtras(bundle);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

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

    /*
     * Receiving speech input
     */

    private final int REQ_CODE = 100;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    if(this.result != Commands.MAIL_CONFIRM)
                        fill(result.get(0));
                    else
                        confirm(result.get(0));
                }
                else if(resultCode == RESULT_CANCELED)
                {
                    finish();
                }
                break;
            }

        }
    }

    boolean fill = false;
    private void fill(String result) {
        switch (this.result){
            case Commands.MAIL_TO:
                result = Util.mail(result);
                et_to.setText(result);
                bundle.putString(Commands.MAIL_TO, result +
                        "");
                if(!fill)
                    this.result = Commands.MAIL_SUBJECT;
                else
                    this.result = Commands.MAIL_CONFIRM;
                ask_fill();
                break;
            case Commands.MAIL_SUBJECT:
                et_subject.setText(result);
                bundle.putString(Commands.MAIL_SUBJECT,result);
                if(!fill)
                    this.result = Commands.MAIL_BODY;
                else
                    this.result = Commands.MAIL_CONFIRM;
                ask_fill();
                break;
            case Commands.MAIL_BODY:

                et_body.setText(result);
                bundle.putString(Commands.MAIL_BODY,result);
                this.result = Commands.MAIL_CONFIRM;
                fill=true;
                ask_fill();

        }
    }

    private void confirm(String result){

        switch (result)
        {
            case Commands.MAIL_TO:
                this.result = Commands.MAIL_TO;
                ask_fill();
                break;
            case Commands.MAIL_SUBJECT:
                this.result = Commands.MAIL_SUBJECT;
                ask_fill();
                break;
            case Commands.MAIL_BODY:
                this.result = Commands.MAIL_BODY;
                ask_fill();
                break;
            case Commands.MAIL_SEND:
                this.result = Commands.MAIL_SEND;
                ask_fill();
                break;
            default:
                this.result = Commands.MAIL_CONFIRM;
                ask_fill();
        }
    }

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void ask_fill()  {
        switch (result){
            case Commands.MAIL_TO:
                speakOut(Commands.MAIL_TO);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Commands.MAIL_SUBJECT:
                speakOut(Commands.MAIL_SUBJECT);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Commands.MAIL_BODY:
                speakOut(Commands.MAIL_BODY);
                while(tts.isSpeaking());
                promptSpeechInput();
                break;
            case Commands.MAIL_SEND:

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                speakOut(Commands.MAIL_SEND);
                Intent i = new Intent();
                i.putExtras(bundle);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setResult(RESULT_OK, i);
                finish();
                break;

            case Commands.MAIL_CONFIRM:
                speakOut(Commands.MAIL_CONFIRM);
                while (tts.isSpeaking());
                promptSpeechInput();
                break;
        }
    }

    boolean a =false;
    @Override
    protected void onPostResume() {
        super.onPostResume();
        a=true;
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                //bl.setEnabled(true);
                speakOut("");
                while (!a);
                ask_fill();
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

    @Override
    public void run() {
        // fill();
    }
}
