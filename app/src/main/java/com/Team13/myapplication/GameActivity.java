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

    private ArrayList<Card> postWheel;


    ImageView cardUpLeft;
    ImageView cardUpRight;
    ImageView cardDownLeft;
    ImageView cardDownRight;

    ImageView player1card1;
    ImageView player1card2;
    ImageView player1card3;

    ImageView player2card1;
    ImageView player2card2;
    ImageView player2card3;

    ImageView player3card1;
    ImageView player3card2;
    ImageView player3card3;

    ImageView player4card1;
    ImageView player4card2;
    ImageView player4card3;

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



        cardUpLeft = (ImageView) findViewById(R.id.UL);
        cardUpRight = (ImageView) findViewById(R.id.UR);
        cardDownLeft = (ImageView) findViewById(R.id.DL);
        cardDownRight = (ImageView) findViewById(R.id.DR);

        player1card1 = findViewById(R.id.player1card1);
        player1card2 = findViewById(R.id.player1card2);
        player1card3 = findViewById(R.id.player1card3);

        player2card1 = findViewById(R.id.player2card1);
        player2card2 = findViewById(R.id.player2card2);
        player2card3 = findViewById(R.id.player2card3);

        player3card1 = findViewById(R.id.player3card1);
        player3card2 = findViewById(R.id.player3card2);
        player3card3 = findViewById(R.id.player3card3);

        player4card1 = findViewById(R.id.player4card1);
        player4card2 = findViewById(R.id.player4card2);
        player4card3 = findViewById(R.id.player4card3);



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

        // Round Logic Starts Here
        startRound();

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
        private int sensitivity = 2;
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
        controller.getAllPlayers().get(0).setTurnDecision(controller.getAllPlayers().get(0).getTurnDecision() -1);
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

        controller.getAllPlayers().get(0).setTurnDecision(controller.getAllPlayers().get(0).getTurnDecision() + 1);

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
        //timer.setVisibility(View.INVISIBLE);
        roundTimer.cancel();
//        Remove button
        Button bttn = findViewById(R.id.turnButton);
       // bttn.setVisibility(View.INVISIBLE);
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
        endRound();
    }

    public void startRound(){
        TextView timer = findViewById(R.id.timer);

        endTurnButton.setText("End Turn");
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "endTurnButton onClick() called");
                endPlayerTurn();
            }
        });

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
                //timer.setVisibility(View.GONE);
                endPlayerTurn();
                Toast.makeText(getApplicationContext(), "Timer has ended", Toast.LENGTH_LONG).show();
            }
        };
        roundTimer.start();

        controller.assignCards2Wheel();
        ArrayList<Card> wheelHand = controller.getWheel();

        cardUpLeft.setImageDrawable(wheelHand.get(0).getFaceUpCard()); // In front of Player 1
        cardUpRight.setImageDrawable(wheelHand.get(1).getFaceUpCard()); // In front of Player 2
        cardDownRight.setImageDrawable(wheelHand.get(2).getFaceUpCard()); // In front of Player 3
        cardDownLeft.setImageDrawable(wheelHand.get(3).getFaceUpCard()); // In front of Player 4

        controller.startRound();
    }

    public void endRound(){

        controller.sumAllDecisions();
        controller.shiftWheel();







        controller.setRoundNum(controller.getRoundNum() - 1);
        if(controller.getRoundNum() >= 0){
            postRound();
        }
        else{
            //Game Over Code Goes Here
        }


    }


    void postRound(){
        endTurnButton.setText("Start Round");
        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "endTurnButton onClick() called");
                startRound();
            }
        });
        ArrayList<Card> wheelHand = controller.getWheel();



        cardUpLeft.setImageDrawable(wheelHand.get(0).getFaceUpCard()); // In front of Player 1
        cardUpRight.setImageDrawable(wheelHand.get(1).getFaceUpCard()); // In front of Player 2
        cardDownRight.setImageDrawable(wheelHand.get(2).getFaceUpCard()); // In front of Player 3
        cardDownLeft.setImageDrawable(wheelHand.get(3).getFaceUpCard()); // In front of Player 4

        controller.assignCards2Players();

        int cardsInHand = 3 - controller.getRoundNum();
        ArrayList<Player> tempPlayerList = controller.getAllPlayers();

        if(cardsInHand >= 1 ){
            player1card1.setImageDrawable(tempPlayerList.get(0).getHand().get(0).getFaceUpCard()); // In front of Player 1
            player2card1.setImageDrawable(tempPlayerList.get(1).getHand().get(0).getFaceUpCard()); // In front of Player 2
            player3card1.setImageDrawable(tempPlayerList.get(2).getHand().get(0).getFaceUpCard()); // In front of Player 3
            player4card1.setImageDrawable(tempPlayerList.get(3).getHand().get(0).getFaceUpCard()); // In front of Player 4
        }
        if(cardsInHand >= 2 ){
            player1card2.setImageDrawable(tempPlayerList.get(0).getHand().get(1).getFaceUpCard()); // In front of Player 1
            player2card2.setImageDrawable(tempPlayerList.get(1).getHand().get(1).getFaceUpCard()); // In front of Player 2
            player3card2.setImageDrawable(tempPlayerList.get(2).getHand().get(1).getFaceUpCard()); // In front of Player 3
            player4card2.setImageDrawable(tempPlayerList.get(3).getHand().get(1).getFaceUpCard()); // In front of Player 4
        }
        if(cardsInHand >= 3){
            player1card3.setImageDrawable(tempPlayerList.get(0).getHand().get(2).getFaceUpCard()); // In front of Player 1
            player2card3.setImageDrawable(tempPlayerList.get(1).getHand().get(2).getFaceUpCard()); // In front of Player 2
            player3card3.setImageDrawable(tempPlayerList.get(2).getHand().get(2).getFaceUpCard()); // In front of Player 3
            player4card3.setImageDrawable(tempPlayerList.get(3).getHand().get(2).getFaceUpCard()); // In front of Player 4
        }

    }
}
