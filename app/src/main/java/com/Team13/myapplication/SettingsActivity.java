package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    TextView timer;
    private Button backButton;
//    private Button saveButton;
    EditText editText1;
    EditText editText2;
    EditText editText3;
    private static String pastText1;
    private static String pastText2;
    private static String pastText3;
    private NumberPicker numberPickerRounds;
    private NumberPicker numberPickerMoves;
    private NumberPicker numberPickerCards;
    private   String[] andwersForCard;
    private   String[] answersForRounds;
    private   String[] answersForRounds2 = {"3", "5"};
    SharedPreferences settings;
    //    String prefs = "MyPrefs";
    private static final String PREFS_FILE_NAME = "AppPrefs";
    private SharedPreferences prefs;
    private static final String ROUND = "rounds per game";
    private static final String MOVE = "moves per round";
    private static final String CARD = "start with cards or not";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        initSharedPreferences();

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);

        numberPickerRounds = (NumberPicker) findViewById(R.id.numberPicker1);
        numberPickerRounds.setMaxValue(1);
        numberPickerRounds.setMinValue(0);
//        numberPickerRounds.setDisplayedValues(answersForRounds2);
        numberPickerRounds.setDisplayedValues(readRoundsFromSharedPreferences());

        numberPickerMoves = (NumberPicker) findViewById(R.id.numberPicker2);
        numberPickerMoves.setMaxValue(20);
        numberPickerMoves.setMinValue(1);
        numberPickerMoves.setValue(readMovesFromSharedPreferences());

        numberPickerCards = (NumberPicker) findViewById(R.id.numberPicker3);
        numberPickerCards.setMaxValue(1);
        numberPickerCards.setMinValue(0);
        numberPickerCards.setDisplayedValues(readCardsFromSharedPreferences());


        numberPickerRounds.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                writeRoundsToSharedPreferences(Integer.parseInt(answersForRounds[newVal]));
            }
        });

        numberPickerMoves.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                writeMovesToSharedPreferences(newVal);
            }
        });

        numberPickerCards.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                System.out.println("new val is: "+newVal);
//                System.out.println("answerforcard for new new val is: "+andwersForCard[newVal]);
                writeCardsToSharedPreferences(andwersForCard[newVal]);
            }
        });

        backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

//        saveButton = findViewById(R.id.savebtn);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String strValue3 = editText3.getText().toString();
//                if(!(strValue3.equalsIgnoreCase("YES") || strValue3.equalsIgnoreCase("NO"))){
//                    Toast.makeText(getApplicationContext(), "Invalid input", Toast.LENGTH_LONG).show();
//                }
//                else{
//                    Toast.makeText(getApplicationContext(), "Saved setting", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

    }

    // Private Helper Methods
    private void initSharedPreferences() {
        prefs = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

//    private void writeRoundsToSharedPreferences(String round) {
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(ROUND, round);
//        editor.commit();
//    }
//
//    private String[] readRoundsFromSharedPreferences() {
//        String[] temp1 = {"3", "5"};
//        String[] temp2 = {"5", "3"};
//        if(prefs.getString(ROUND, "3").equalsIgnoreCase("3")){
//            answersForRounds = temp1;
//            return  temp1;
//        }
//        else if(prefs.getString(ROUND, "3").equalsIgnoreCase("5")){
//            answersForRounds = temp2;
//            return temp2;
//        }
//        else {
//            answersForRounds = temp1;
//            return temp1;
//        }
//    }
    private void writeRoundsToSharedPreferences(int round) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(ROUND, round);
        editor.commit();
    }

    private String[] readRoundsFromSharedPreferences() {
        String[] temp1 = {"3", "5"};
        String[] temp2 = {"5", "3"};
        if(prefs.getInt(ROUND, 3) == 3){
            answersForRounds = temp1;
            return  temp1;
        }
        else if(prefs.getInt(ROUND, 3) == 5){
            answersForRounds = temp2;
            return temp2;
        }
        else {
            answersForRounds = temp1;
            return temp1;
        }
    }










    private void writeMovesToSharedPreferences(int move) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(MOVE, move);
        editor.commit();
    }

    private int readMovesFromSharedPreferences() {
        return prefs.getInt(MOVE, 1);
    }

    private void writeCardsToSharedPreferences(String card) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(CARD, card);
        editor.commit();
    }

    private String[] readCardsFromSharedPreferences() {
        String[] temp1 = {"no", "yes"};
        String[] temp2 = {"yes", "no"};
        System.out.println("here is preference: "+ prefs.getString(CARD, "no"));
        if(prefs.getString(CARD, "no").equalsIgnoreCase("no")){
            andwersForCard = temp1;
            return  temp1;
        }
        else if(prefs.getString(CARD, "no").equalsIgnoreCase("yes")){
            andwersForCard = temp2;
            return temp2;
        }
        else {
            andwersForCard = temp1;
            return temp1;
        }
    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        String strValue1 = editText1.getText().toString();
//        String strValue2 = editText2.getText().toString();
//        String strValue3 = editText3.getText().toString();
////        outState.putString("savedText1", strValue1);
////        outState.putString("savedText2", strValue2);
////        outState.putString("savedText3", strValue3);
//        pastText1 = strValue1;
//        pastText2 = strValue2;
//        pastText3 = strValue3;
//    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedState) {
//        super.onRestoreInstanceState(savedState);
//        String editText3Value = editText3.getText().toString();
//        editText3.setText(editText3Value);
//    }

}