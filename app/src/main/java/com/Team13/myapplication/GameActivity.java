package com.Team13.myapplication;

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

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private GestureDetectorCompat swipeDetector;
    private static final String TAG = "GAME_ACTIVITY";
    public static final String CHANNEL_ID = ".gameActivity";
    public static final int NOTIFICATION_ID = 2;
    protected int finalDecision = 0;
    private static final int maxSwipes = 3;
    private ImageView card;
    private int numPlayers = 4;
    private GameController controller;
    private Button endTurnButton;
    private CountDownTimer roundTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Log.i(TAG, "after called");
//        Create/Instantiate game controller
        controller = new GameController(getResources());
//        Set num of rounds 3
        controller.setRoundNum(3);
//        Create Notification Channel
        createNotificationChannel(this);
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
//        Real game code
//        for (int i=0 ; i < numPlayers; i++) {
//            players.add(new Player());
//        }
        players.add(new Player());
        players.add(new AIPlayer());
        players.add(new AIPlayer());
        players.add(new AIPlayer());
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
//              Build notification
                NotificationCompat.Builder builder = new NotificationCompat
                        .Builder(GameActivity.this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("No More Moves")
                        .setContentText("You have used all of your moves for this round")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
//              Show notification
                NotificationManagerCompat nManager = NotificationManagerCompat.from(GameActivity.this);
                nManager.notify(NOTIFICATION_ID, builder.build());
            }

            return true;
        }
    }

    private void swipeLeft() {
//      Update player decision
        finalDecision++;
//      Build notification
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(GameActivity.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Moved Wheel Clockwise")
                .setContentText("You have moved the wheel clockwise by one")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
//              Show notification
        NotificationManagerCompat nManager = NotificationManagerCompat.from(GameActivity.this);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }


    private void swipeRight() {
//      Update player decision
        finalDecision--;
//      Build notification
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(GameActivity.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Moved Wheel Anti-Clockwise")
                .setContentText("You have moved the wheel anti-clockwise by one")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
//              Show notification
        NotificationManagerCompat nManager = NotificationManagerCompat.from(GameActivity.this);
        nManager.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
//      Build notification
        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(GameActivity.this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Ended Round")
                .setContentText("The round has ended")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
//              Show notification
        NotificationManagerCompat nManager = NotificationManagerCompat.from(GameActivity.this);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }
}
