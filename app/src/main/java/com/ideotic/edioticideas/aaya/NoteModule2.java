package com.ideotic.edioticideas.aaya;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class NoteModule2 extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextToSpeech tts;
    EditText et_title, et_body, et_priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note2_module);
        init();
    }

    private void init() {
        et_body = (EditText) findViewById(R.id.et_body);
        et_priority = (EditText) findViewById(R.id.et_pri);
        et_title = (EditText) findViewById(R.id.et_title);
        tts = new TextToSpeech(this, this);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "");
        try {
            startActivityForResult(intent, REQ_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "",
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
                    fill(result.get(0));
                }
                else if (resultCode==RESULT_CANCELED){
                    finish();
                }

                break;
            }

        }
    }


    String command = Commands.TITLEn, body, title, priority;

    private void fill(String result) {
        switch (this.command) {
            case Commands.BODY:
                body = result;
                et_body.setText(result);
                this.command = Commands.PRIORITY;
                ask_fill();
                break;
            case Commands.TITLE:
                title = result;
                et_title.setText(result);
                this.command = Commands.BODY;
                ask_fill();
                break;
            case Commands.PRIORITY:
                try {
                    priority = String.valueOf(Integer.parseInt(result));
                }
                catch (Exception w)
                {
                    speakOut(Commands.PRIORITY);
                    while (tts.isSpeaking()) ;
                    promptSpeechInput();
                }
                if(priority!=null) {
                    et_priority.setText(result);
                    this.command = Commands.SAVE;
                    ask_fill();
                }
        }
    }

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void ask_fill() {
        switch (command) {
            case Commands.BODY:
                speakOut(Commands.BODY);
                while (tts.isSpeaking()) ;
                promptSpeechInput();
                break;
            case Commands.TITLE:
                speakOut(Commands.TITLE);
                while (tts.isSpeaking()) ;
                promptSpeechInput();
                break;
            case Commands.PRIORITY:
                speakOut(Commands.PRIORITY);
                while (tts.isSpeaking()) ;
                promptSpeechInput();
                break;
            case Commands.SAVE:
                speakOut(Commands.SAVE);
                DataBaseNotes db = new DataBaseNotes(this);
                try {
                    db.createEntry(title, body, priority);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            default:
                break;
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
                //bl.setEnabled(true);
                speakOut("");
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
}
