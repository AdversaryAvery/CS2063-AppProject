package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Guideline;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    TextView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        timer = findViewById(R.id.timer);

        long duration = TimeUnit.MINUTES.toMillis(1);
        new CountDownTimer(duration, 1000){
            @Override
            public void onTick(long l){
                String sDuration = String.format(Locale.ENGLISH, "%02d : %02d"
                        , TimeUnit.MILLISECONDS.toMinutes(l)
                        , TimeUnit.MILLISECONDS.toSeconds(l) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));
                timer.setText(sDuration);
            }

            @Override
            public void onFinish(){
                timer.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Timer has ended", Toast.LENGTH_LONG).show();
            }
        }.start();

    }
}
