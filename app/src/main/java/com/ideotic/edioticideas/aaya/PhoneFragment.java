package com.ideotic.edioticideas.aaya;

import android.app.DialogFragment;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Mukul on 16-05-2016.
 */
public class PhoneFragment extends DialogFragment implements TextToSpeech.OnInitListener, Runnable {

    private TextToSpeech textToSpeech;
    Thread i;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.diplay_msgs, container);
        TextView msg = (TextView) view.findViewById(R.id.tv_display);
        msg.setText(PhoneModule.result);
        textToSpeech = new TextToSpeech(getActivity().getBaseContext(), this);
        i = new Thread(this);
        return view;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                i.start();


                //while (textToSpeech.isSpeaking());
                //dismiss();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null);
        while (!textToSpeech.isSpeaking()) ;
    }

    @Override
    public void run() {
        speakOut(PhoneModule.result);
        while (textToSpeech.isSpeaking()) ;
        dismiss();
    }

    @Override
    public void onDestroy() {
        // Shuts Down TTS
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
