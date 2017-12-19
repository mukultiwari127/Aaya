package com.ideotic.edioticideas.aaya;

import android.app.DialogFragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Mukul on 16-05-2016.
 */
public class display_frag extends DialogFragment implements TextToSpeech.OnInitListener{


    TextToSpeech textToSpeech;
    TextView tv_dispay;
    IsSpeaking i;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dis_frag, null);
        tv_dispay = (TextView) view.findViewById(R.id.tv_dis);
        tv_dispay.setText(getArguments().getString(Commands.DATE));
        textToSpeech = new TextToSpeech(getActivity().getBaseContext(), this);
        i= new IsSpeaking(textToSpeech,this);
        return view;

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tv_dispay.setText("TTS  This Language is not supported");
            } else {
                //bl.setEnabled(true);
                speakOut(getArguments().getString(Commands.DATE));
                i.start();
                // dismiss();
            }

        } else {
            tv_dispay.setText("TTS Initilization Failed!");
        }
    }
    private void speakOut(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
