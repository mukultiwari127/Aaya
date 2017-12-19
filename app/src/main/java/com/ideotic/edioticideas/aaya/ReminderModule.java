package com.ideotic.edioticideas.aaya;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Mukul on 16-05-2016.
 */
public class ReminderModule extends Activity implements TextToSpeech.OnInitListener {


    TextView tv;
    EditText et_date, et_month, et_year, et_hour, et_minutes, et_title;

    long time = 3000;

    TextToSpeech textToSpeech;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_reminder);
        init();
    }

    private void init() {
        textToSpeech = new TextToSpeech(this, this);
        et_date = (EditText) findViewById(R.id.et_date);
        et_year = (EditText) findViewById(R.id.et_year);
        et_month = (EditText) findViewById(R.id.et_month);
        et_hour = (EditText) findViewById(R.id.et_hour);
        et_minutes = (EditText) findViewById(R.id.et_minutes);
        et_title = (EditText) findViewById(R.id.et_title);
    }

    public void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReciver.class);

        DataBase db = new DataBase(this);
        try {
            db.Open();
            Random r = new Random(9999);
            String id = String.valueOf(r.nextInt());
            intent.putExtra("id", id);
            db.createEntry(id, title, desc);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Date date = new Date();
        long time = date.getTime();
        Date date1 = new Date((year % 2000) + 100, month - 1, this.date, hour, minutes, 0);
        long time2 = date1.getTime();
        this.time = time2 - time;
        alarmManager.set(AlarmManager.RTC_WAKEUP, new GregorianCalendar().getTimeInMillis() + this.time, pendingIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    this.result = result.get(0);
                    fill();
                }
                break;
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

    private void askFill() {
        switch (command) {
            case Commands.DATE:
                speakout(Commands.DATE);
                promptSpeechInput();
                break;
            case Commands.YEAR:
                speakout(Commands.YEAR);
                promptSpeechInput();
                break;
            case Commands.MONTH:
                speakout(Commands.MONTH);
                promptSpeechInput();
                break;
            case Commands.HOUR:
                speakout(Commands.HOUR);
                promptSpeechInput();
                break;
            case Commands.MINUTES:
                speakout(Commands.MINUTES);
                promptSpeechInput();
                break;
            case Commands.TITLE:
                speakout(Commands.TITLE);
                promptSpeechInput();
                break;
            case Commands.SET:
                scheduleAlarm();
        }
    }

    int date, year, month, hour, minutes;
    String title, desc = "Hey u wanted me to remind you !!";
    String command = Commands.YEAR;

    private void fill() {
        switch (command) {
            case Commands.DATE:
                //while (textToSpeech.isSpeaking());
                et_date.setText(result);
                date = Integer.parseInt(result);
                command = Commands.HOUR;
                askFill();

                break;
            case Commands.MONTH:
                //while (textToSpeech.isSpeaking());
                et_month.setText(result);
                month = Integer.parseInt(result);

                command = Commands.DATE;
                askFill();
                break;
            case Commands.YEAR:
                //while (textToSpeech.isSpeaking());
                et_year.setText(result);
                year = Integer.parseInt(result);
                command = Commands.MONTH;
                askFill();
                break;
            case Commands.HOUR:
                //while (textToSpeech.isSpeaking());
                et_hour.setText(result);
                hour = Integer.parseInt(result.replace("one", "1"));
                command = Commands.MINUTES;
                askFill();
                break;
            case Commands.MINUTES:
                //while (textToSpeech.isSpeaking());
                et_minutes.setText(result);
                minutes = Integer.parseInt(result);
                command = Commands.TITLE;
                askFill();
                break;
            case Commands.TITLE:
                //while (textToSpeech.isSpeaking());
                et_title.setText(result);
                title = result;
                command = Commands.SET;
                askFill();
                break;
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            askFill();

        }
    }

    private void speakout(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    public void onDestroy() {
        // Shuts Down TTS
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
        finish();

    }


}
