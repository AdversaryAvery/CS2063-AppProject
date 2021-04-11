package com.Team13.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Guideline;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GestureDetectorCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private GestureDetectorCompat swipeDetector;
    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private static final String TAG = "GAME_ACTIVITY";
    protected int finalDecision = 0;
    private int maxSwipes = 3;
    private int numPlayers;
    private GameController controller;
    private Button endTurnButton;
    private CountDownTimer roundTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
//        Create/Instantiate game controller
        controller = new GameController(getResources());
//        Set num of rounds 3
        controller.setRoundNum(3);
//        Create Notification Channel
//        Set up gestureDetector to use swipe gestureListener
        swipeDetector = new GestureDetectorCompat(this, new MyGestureListener());

        controller.assignCards2Wheel();
        ArrayList<Card> wheelHand = controller.getWheel();
        ImageView cardUpLeft = (ImageView) findViewById(R.id.UL);
        ImageView cardUpRight = (ImageView) findViewById(R.id.UR);
        ImageView cardDownLeft = (ImageView) findViewById(R.id.DL);
        ImageView cardDownRight = (ImageView) findViewById(R.id.DR);

        cardUpLeft.setImageDrawable(wheelHand.get(0).getFaceUpCard());
        cardUpRight.setImageDrawable(wheelHand.get(1).getFaceUpCard());
        cardDownLeft.setImageDrawable(wheelHand.get(2).getFaceUpCard());
        cardDownRight.setImageDrawable(wheelHand.get(3).getFaceUpCard());

        ArrayList<Player> players = new ArrayList<Player>();

        dbRef = database.getReference("settings");
        dbRef.child("numPlayers").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Log.i(TAG, "retrieved numPlayer value");
                        numPlayers = (task.getResult().getValue() != null) ? ((int) task.getResult().getValue()): 2;
                    }
                });
        dbRef.child("maxSwipes").get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Log.i(TAG, "retrieved maxSwipes value");
                        maxSwipes = (task.getResult().getValue() != null) ? ((int) task.getResult().getValue()): 3;
                    }
                });


        String gameMode = getIntent().getStringExtra("game mode");
        if (gameMode.matches("multi")) {
            for (int i=0 ; i < numPlayers; i++) {
                players.add(new Player());
            }
        } else {
            players.add(new Player());
            players.add(new AIPlayer());
            players.add(new AIPlayer());
            players.add(new AIPlayer());
        }

        controller.setAllPlayers(players);

        endTurnButton = findViewById(R.id.turnButton);
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "endTurnButton onClick() called");
                endPlayerTurn();
            }
        });

        TextView timer = findViewById(R.id.timer);

        long duration = TimeUnit.MINUTES.toMillis(1);
        roundTimer = new  CountDownTimer(duration, 1000){
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
                endPlayerTurn();
                Toast.makeText(getApplicationContext(), "Timer has ended", Toast.LENGTH_LONG).show();
            }
        };
        roundTimer.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.i(TAG, "onTouchEvent called");
        this.swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "SWIPE DETECTED";
        private int swipeCount= 0;
        private int sensitivity = 10;
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {

            if (swipeCount < maxSwipes) {
                if ((event1.getX() - event2.getX()) > sensitivity) {
                    Log.i(DEBUG_TAG, "Left Swipe: " + event1.getX() + ", " + event2.getX());
                    swipeLeft();
                } else if ((event2.getX() - event1.getX()) > sensitivity) {
                    Log.i(DEBUG_TAG, "Right Swipe: " + event1.getX() + ", " + event2.getX());
                    swipeRight();
                }
            } else {
                Toast.makeText(GameActivity.this, "No More Moves", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }

    private void swipeLeft() {
//      Update player decision
        finalDecision++;
        Toast.makeText(GameActivity.this, "Moved Wheel Clockwise", Toast.LENGTH_SHORT).show();
    }


    private void swipeRight() {
//      Update player decision
        finalDecision--;
        Toast.makeText(GameActivity.this, "Moved Wheel Anti-Clockwise", Toast.LENGTH_SHORT).show();
    }


    private void endPlayerTurn(){
//        End Round for all players
//        Stop and Remove Timer
        TextView timer = findViewById(R.id.timer);
        timer.setVisibility(View.INVISIBLE);
        roundTimer.cancel();
//        Remove button
        Button bttn = findViewById(R.id.turnButton);
        bttn.setVisibility(View.INVISIBLE);
//        This is only done for prototype
        ArrayList<Player> players = controller.getAllPlayers();
        for (Player p : players){
            p.endTurn();
        }
        Toast.makeText(GameActivity.this, "Ended Round", Toast.LENGTH_SHORT).show();
    }
}
