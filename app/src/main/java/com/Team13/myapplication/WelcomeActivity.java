package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WELCOME_ACTIVITY";
    private Button instructionsButton;
    private Button settingsButton;
    private Button aiGameButton;
    private Button multiGameButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        instructionsButton = findViewById(R.id.btnInstructions);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, InstructionsActivity.class);
                Log.i(TAG, "starting Instructions Activity");
                startActivity(intent);
            }
        });

        settingsButton = findViewById(R.id.btnSettings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        multiGameButton = findViewById(R.id.multiple);
        multiGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, MultiActivity.class);
                Log.i(TAG, "starting Multi Player Game Activity");
                intent.putExtra("game mode", "multi");
                startActivity(intent);
            }
        });

        aiGameButton = findViewById(R.id.single);
        aiGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, GameActivity.class);
                Log.i(TAG, "starting Single Player Game Activity");
                intent.putExtra("game mode", "single");
                startActivity(intent);
            }
        });
    }
}
