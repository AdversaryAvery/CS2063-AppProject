package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.Guideline;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    //Configurable Values
    private int numberOfRounds;
    private Boolean showCards;
    private Boolean startWithCards;
    private int numberOfCards;
    //

    TextView decisionView;
    ImageView cardUpLeft;
    ImageView cardUpRight;
    ImageView cardDownLeft;
    ImageView cardDownRight;

    RecyclerView player1cards;
    RecyclerView player2cards;
    RecyclerView player3cards;
    RecyclerView player4cards;

    TextView player1name;
    TextView player2name;
    TextView player3name;
    TextView player4name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Load Values from preferences

        numberOfRounds = 3;
        numberOfCards = 3;
        showCards = true;
        startWithCards = false;


        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
        Log.i(TAG, "after called");
//        Create/Instantiate game controller
        controller = new GameController(getResources());
//        Set num of rounds 3

        //Load Round Number from User Preferences
        // int temp = pref.
        controller.setRoundNum(numberOfRounds);
//        Create Notification Channel
        createNotificationChannel(this);
//        Set up gestureDetector to use swipe gestureListener
        swipeDetector = new GestureDetectorCompat(this, new MyGestureListener());



        cardUpLeft = (ImageView) findViewById(R.id.UL);
        cardUpRight = (ImageView) findViewById(R.id.UR);
        cardDownLeft = (ImageView) findViewById(R.id.DL);
        cardDownRight = (ImageView) findViewById(R.id.DR);

        player1cards = findViewById(R.id.player1cards);
        player2cards = findViewById(R.id.player2cards);
        player3cards = findViewById(R.id.player3cards);
        player4cards = findViewById(R.id.player4cards);


        decisionView = findViewById(R.id.decisionView);

        player1name = findViewById(R.id.player1name);
        player2name = findViewById(R.id.player2name);
        player3name = findViewById(R.id.player3name);
        player4name = findViewById(R.id.player4name);

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

        if(startWithCards){
            controller.startingCards(numberOfCards);
        }

        endTurnButton = findViewById(R.id.turnButton);

        endTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "endTurnButton onClick() called");
                endPlayerTurn();
            }
        });


        player1cards.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        player2cards.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        player3cards.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));
        player4cards.setLayoutManager(new LinearLayoutManager(getApplicationContext(),RecyclerView.HORIZONTAL,false));



        player1cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(0).getHand(),true));
        player2cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(1).getHand(), showCards));
        player3cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(2).getHand(), showCards));
        player4cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(3).getHand(), showCards));
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
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
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
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
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

        player1name.setText("Player 1");
        player2name.setText("Player 2");
        player3name.setText("Player 3");
        player4name.setText("Player 4");

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

        ArrayList<Card> wheelHand = controller.getWheel();
        if(startWithCards){
            controller.donations2Wheel();
        }

        else{
        controller.assignCards2Wheel();
        }

        cardUpLeft.setImageDrawable(wheelHand.get(0).getFaceUpCard()); // In front of Player 1
        cardUpRight.setImageDrawable(wheelHand.get(1).getFaceUpCard()); // In front of Player 2
        cardDownRight.setImageDrawable(wheelHand.get(2).getFaceUpCard()); // In front of Player 3
        cardDownLeft.setImageDrawable(wheelHand.get(3).getFaceUpCard()); // In front of Player 4

        controller.startRound();
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
    }

    public void endRound(){
        if(controller.getRoundNum() > 0){
        controller.sumAllDecisions();
        decisionView.setText("Sum of Decisions:" + String.valueOf(controller.getSumOfTurns()));
        controller.shiftWheel();

        ArrayList<Player> tempPlayerList = controller.getAllPlayers();

        player1name.append(": " + String.valueOf(tempPlayerList.get(0).getTurnDecision()));
        player2name.append(": " + String.valueOf(tempPlayerList.get(1).getTurnDecision()));
        player3name.append(": " + String.valueOf(tempPlayerList.get(2).getTurnDecision()));
        player4name.append(": " + String.valueOf(tempPlayerList.get(3).getTurnDecision()));





        controller.setRoundNum(controller.getRoundNum() - 1);
            Log.i("Post Round","Before Round" + String.valueOf(controller.getRoundNum()));
            postRound();
        }
        else{
            Log.i("Post Game","Start Ranking");
            ArrayList<Player> winnerList = controller.rankPlayers(numberOfCards,numberOfRounds);

            String tempString;
            Player winner = winnerList.get(0);
            Rank tempRank = winner.getRank();

            if(numberOfCards == 3){
            switch(tempRank.getHandRank()){
                case 6: tempString = "Straight Flush"; break;
                case 5: tempString = "Three of a Kind"; break;
                case 4: tempString = "Straight"; break;
                case 3: tempString = "Flush"; break;
                case 2: tempString = "Pair"; break;
                default: tempString = "High Card"; break;
            }}

            else{switch(tempRank.getHandRank()){
                case 10: tempString = "Royal Flush"; break;
                case 9: tempString = "Straight Flush"; break;
                case 8: tempString = "Four of a Kind"; break;
                case 7: tempString = "Full House"; break;
                case 6: tempString = "Flush"; break;
                case 5: tempString = "Straight"; break;
                case 4: tempString = "Three of a Kind"; break;
                case 3: tempString = "Two Pair"; break;
                case 2: tempString = "Pair"; break;
                default: tempString = "High Card"; break;}}

            decisionView.append("\nBest Hand:" + tempString);


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

        player1cards.setAdapter(null);
        player2cards.setAdapter(null);
        player3cards.setAdapter(null);
        player4cards.setAdapter(null);

        player1cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(0).getHand(),true));
        player2cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(1).getHand(), showCards));
        player3cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(2).getHand(), showCards));
        player4cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(3).getHand(), showCards));


            }

    }


