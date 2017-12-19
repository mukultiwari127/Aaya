package com.ideotic.edioticideas.aaya;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Shubham on 21-04-2016.
 */
public class myClass extends AppCompatActivity {

    static int i = 0;
    TextView tvshow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shubham);
        int a = getIntent().getIntExtra("add",0);
        tvshow = (TextView)findViewById(R.id.textview_s);
        tvshow.setText(""+a);
    }


}
