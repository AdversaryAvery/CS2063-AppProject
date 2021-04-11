package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Guideline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {
    TextView timer;
    private Button backButton;
    private Button saveButton;
    EditText editText1; // Number of Rounds
    EditText editText2; // Number of moves per Round
    EditText editText3;
    //private static String pastText1;
    //private static String pastText2;
    //private static String pastText3;

    private SharedPreferences sharedPreferences;
    private final String pref = "sharedPreferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences(pref, Context.MODE_PRIVATE);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);

        editText1.setText(String.valueOf(sharedPreferences.getInt("Rounds",3)));
        editText2.setText(String.valueOf(sharedPreferences.getInt("MovesPerRound",1)));

        backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

        saveButton = findViewById(R.id.savebtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strValue3 = editText3.getText().toString();
                if(!(strValue3.equalsIgnoreCase("YES") || strValue3.equalsIgnoreCase("NO"))){
                    Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Saved setting", Toast.LENGTH_LONG).show();
                }

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Rounds", Integer.parseInt(editText1.getText().toString()));
                editor.putInt("MovesPerRound", Integer.parseInt(editText2.getText().toString()));
                editor.apply();
            }
        });
        /*
        if(pastText1 != null){
            editText1.setText(pastText1); //setting the saved value to the TextView
        }
        if(pastText2 != null){
            editText2.setText(pastText2); //setting the saved value to the TextView
        }
        if(pastText3 != null){
            editText3.setText(pastText3); //setting the saved value to the TextView
        }*/
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String strValue1 = editText1.getText().toString();
        String strValue2 = editText2.getText().toString();
        String strValue3 = editText3.getText().toString();
//        outState.putString("savedText1", strValue1);
//        outState.putString("savedText2", strValue2);
//        outState.putString("savedText3", strValue3);
        //pastText1 = strValue1;
        //pastText2 = strValue2;
       // pastText3 = strValue3;
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedState) {
//        super.onRestoreInstanceState(savedState);
//        String editText3Value = editText3.getText().toString();
//        editText3.setText(editText3Value);
//    }
}
