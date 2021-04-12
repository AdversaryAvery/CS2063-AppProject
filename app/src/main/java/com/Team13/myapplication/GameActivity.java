package com.Team13.myapplication;

import androidx.annotation.NonNull;
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
import android.content.SharedPreferences;
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
    private Button quitButton;
    private CountDownTimer roundTimer;

    //Configurable Values
    private int numberOfRounds;
    private Boolean showCards;
    private Boolean startWithCards;
    private int numberOfCards;
    private int movesPerRound;
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

    private SharedPreferences sharedPreferences;
    private static final String PREFS_FILE_NAME = "AppPrefs";
    private static final String ROUND = "rounds per game";
    private static final String MOVE = "moves per round";
    private static final String CARD = "start with cards or not";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        //Load Values from preferences
        Log.i(TAG, "getting preferences");
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        numberOfRounds = sharedPreferences.getInt("Rounds",3);
        numberOfCards = sharedPreferences.getInt("Rounds",3);
        movesPerRound = sharedPreferences.getInt("MovesPerRound",1);
        showCards = true;
        startWithCards = false;

//        numberOfRounds = Integer.parseInt(sharedPreferences.getString(ROUND,"3"));
//        movesPerRound = sharedPreferences.getInt(MOVE,1);
//        numberOfCards = numberOfRounds;
//        showCards = true;
//        if(sharedPreferences.getString(CARD,"no").equalsIgnoreCase("no")){
//            startWithCards = false;
//        }
//        else if(sharedPreferences.getString(CARD,"no").equalsIgnoreCase("yes")){
//            startWithCards = true;
//        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);
//        Create/Instantiate game controller
        controller = new GameController(getResources());
        controller.setRoundNum(numberOfRounds);
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

        database = FirebaseDatabase.getInstance();
        SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);
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


        quitButton = findViewById(R.id.quitButton);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "quit onClick() called");
                Intent intent = new Intent(GameActivity.this, WelcomeActivity.class);
                startActivity(intent);
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
                Toast.makeText(GameActivity.this, "No More Moves", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
    }

    private void swipeLeft() {
//      Update player decision
        finalDecision++;
        Toast.makeText(GameActivity.this, "Moved Wheel Clockwise", Toast.LENGTH_SHORT).show();
        controller.getAllPlayers().get(0).decrementDecision(movesPerRound);
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
    }


    private void swipeRight() {
//      Update player decision
        finalDecision--;
        Toast.makeText(GameActivity.this, "Moved Wheel Anti-Clockwise", Toast.LENGTH_SHORT).show();
        controller.getAllPlayers().get(0).incrementDecision(movesPerRound);
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
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
        Toast.makeText(GameActivity.this, "Ended Round", Toast.LENGTH_SHORT).show();
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

        controller.startRound(movesPerRound);
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


