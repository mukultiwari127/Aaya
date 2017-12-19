package com.ideotic.edioticideas.aaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Needs extends AppCompatActivity {
    Button submit;
    EditText etName, etContact, etAdd, etDob,etEmer;
    CheckBox vImpaired;
    String name, address, phone, data, birth, vImp = "no",emer;
    public final static String NAME = "name";
    public final static String ADD = "add";
    public final static String PHno = "phno";
    public static final String DATA = "data";
    public static final String BIRTH = "birth";
    public static final String VIMP = "vimp";
    public static final String EMER = "emer";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_needs);


        //Grabbing References
        submit = (Button) findViewById(R.id.buttonSubmit);
        etName = (EditText) findViewById(R.id.editTextName);
        etAdd = (EditText) findViewById(R.id.editTextAdd);
        etContact = (EditText) findViewById(R.id.editTextContact);
        etDob = (EditText) findViewById(R.id.editTextdob);
        vImpaired = (CheckBox) findViewById(R.id.checkBoxYes);
        etEmer = (EditText) findViewById(R.id.et_emergency);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Initializing variables
                name = etName.getText().toString();
                address = etAdd.getText().toString();
                phone = etContact.getText().toString();
                birth = etDob.getText().toString();
                if (vImpaired.isChecked()) {
                    vImp = "yes";
                }
                emer = etEmer.getText().toString();
                //Saves to database
                try {
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString(NAME,name);
                    editor.putString(ADD,address);
                    editor.putString(PHno,phone);
                    editor.putString(BIRTH,birth);
                    editor.putString(VIMP,vImp);
                    editor.putString(EMER,emer);
                    editor.commit();
                    /*
                    AayaDatabase enterDatabase = new AayaDatabase(Needs.this);
                    enterDatabase.open();
                    enterDatabase.createEntry(name, address, phone, birth, vImp);
                    data = enterDatabase.getData();
                    enterDatabase.close();
                    */
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    //Toast.makeText(Needs.this, data, Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent(Needs.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
