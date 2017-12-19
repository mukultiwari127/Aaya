package com.ideotic.edioticideas.aaya;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class NoteModule extends AppCompatActivity implements TextToSpeech.OnInitListener {

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_module);
        ListView l = (ListView) findViewById(R.id.lv_note);
        BaseAdapter l1 = new MyAdapter(this);
        l.setAdapter(l1);
        tts = new TextToSpeech(this, this);
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
                promptSpeechInput();

            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private final int REQ_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    command = result.get(0);
                    execute();
                }
                break;
            }

        }
    }

    String command = Commands.MAKE;

    private void execute() {
        switch (command) {
            case Commands.READ:
                String result = "";
                for (int i = 0; i < MyAdapter.list.size(); i++) {
                    SingleRow sr = MyAdapter.list.get(i);
                    result = result + sr.title + "\n" + sr.body + "\n" + sr.priority + "\n\n\n\n";
                }

                speakOut(result);

                break;
            case Commands.MAKE:
                Intent i = new Intent(this, NoteModule2.class);
                startActivity(i);
                finish();
                break;
            default:
                if (command !=null) {
                    speakOut("sorry no such Command as " + command + ".....try again");
                    while (tts.isSpeaking()) ;
                    promptSpeechInput();
                    command = null;
                }
        }
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

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}