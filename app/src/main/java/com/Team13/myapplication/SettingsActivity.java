package com.Team13.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "ROOM_ACTIVITY";
    private Button backButton;
    private FirebaseDatabase database;

    private DatabaseReference roundsRef;
    private DatabaseReference movesRef;
    private DatabaseReference settingsRef;
    EditText editText1;
    EditText editText2;
    private NumberPicker numberPickerRounds;
    private NumberPicker numberPickerMoves;
    private   String[] answersForRounds;
    private static final String ROUND = "rounds per game";
    private static final String MOVE = "moves per round";
    private int numRounds;
    private int numMovesPerRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        initDatabase();

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);

        numberPickerRounds = (NumberPicker) findViewById(R.id.numberPicker1);
        numberPickerRounds.setMaxValue(1);
        numberPickerRounds.setMinValue(0);
        numberPickerRounds.setDisplayedValues(readRoundsFromSharedPreferences());

        numberPickerMoves = (NumberPicker) findViewById(R.id.numberPicker2);
        numberPickerMoves.setMaxValue(20);
        numberPickerMoves.setMinValue(1);
        numberPickerMoves.setValue(readMovesFromSharedPreferences());

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

        backButton = findViewById(R.id.btnBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, WelcomeActivity.class);
                startActivity(intent);
            }
        });

    }

    // Private Helper Methods
    private void initDatabase() {
        Log.i(TAG, "connecting to settings db node");
        database = FirebaseDatabase.getInstance();
        roundsRef = database.getReference("settings/" + ROUND);
        roundsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "retrieved number of rounds value");
                String value = snapshot.getValue(String.class);
                numRounds = (value != null ) ? Integer.getInteger(value) : 3;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        movesRef = database.getReference("settings/" + MOVE);
        movesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "retrieved number of moves/round value");
                String value = snapshot.getValue(String.class);
                numMovesPerRound = (value != null ) ? Integer.getInteger(value): 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void writeRoundsToSharedPreferences(int round) {
        Log.i(TAG, "updating rounds in db " + round);
        roundsRef = database.getReference("settings/" + ROUND);
        roundsRef.setValue(""+round);
    }

    private String[] readRoundsFromSharedPreferences() {
        String[] temp1 = {"3", "5"};
        String[] temp2 = {"5", "3"};
        if(numRounds == 3){
            answersForRounds = temp1;
            return  temp1;

        }
        else if(numRounds == 5){
            answersForRounds = temp2;
            return temp2;
        }
        else {
            answersForRounds = temp1;
            return temp1;
        }
    }


    private void writeMovesToSharedPreferences(int move) {
        Log.i(TAG, "updating moves in db " + move);
        movesRef = database.getReference("settings/" + MOVE);
        movesRef.setValue(""+move);
    }

    private int readMovesFromSharedPreferences() {
        return numMovesPerRound;
    }


}