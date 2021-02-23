package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Guideline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WELCOME_ACTIVITY";

    private Button playGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        playGameButton = findViewById(R.id.btnLaunchGame);
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, GameActivity.class);
                Log.i(TAG, "starting Game Activity");
                startActivity(intent);
            }
        });
    }
}
