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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GAME_ACTIVITY";
    private GestureDetectorCompat swipeDetector;
    private FirebaseDatabase database;
    private DatabaseReference settingsRef;
    private DatabaseReference gameRef;
    private DatabaseReference roomRef;
    private static final String ROUND = "rounds per game";
    private static final String MOVE = "moves per round";
    private String gameMode;
    protected int finalDecision = 0;
    private GameController controller;
    private Button endTurnButton;
    private Button quitButton;
    private CountDownTimer roundTimer;

    //Configurable Values
    private int numberOfRounds;
    private Boolean showCards;
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
    private String playerName;
    private int playerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        //Load Values from preferences
        Log.i(TAG, "getting game settings");
        initSettings();

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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);

        String gameMode = getIntent().getStringExtra("game mode");
        if (gameMode.matches("multi")) {
            setupMultiPlayer(players);
        } else {
            players.add(new Player());
            players.add(new AIPlayer());
            players.add(new AIPlayer());
            players.add(new AIPlayer());
        }

        for(int i = 0; i<4; i++){
            players.get(i).setPlayerNumber(i+1);
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


        quitButton = findViewById(R.id.quitButton);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "quit onClick() called");
                roundTimer.cancel();
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

            if (swipeCount < movesPerRound && controller.getRoundNum() > 0) {
                if ((event1.getX() - event2.getX()) > sensitivity) {
                    Log.i(DEBUG_TAG, "Left Swipe: " + event1.getX() + ", " + event2.getX());
                    swipeLeft();
                } else if ((event2.getX() - event1.getX()) > sensitivity) {
                    Log.i(DEBUG_TAG, "Right Swipe: " + event1.getX() + ", " + event2.getX());
                    swipeRight();
                }
            } else {
                if (controller.getRoundNum() <= 0) {
                    Toast.makeText(GameActivity.this, "End of Game", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, "No More Moves", Toast.LENGTH_SHORT).show();
                }
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
        if (gameMode != "multi") {
            player1name.setText("Player 1");
        }
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
        controller.assignCards2Wheel();

        cardUpLeft.setImageDrawable(wheelHand.get(0).getFaceUpCard()); // In front of Player 1
        cardUpRight.setImageDrawable(wheelHand.get(1).getFaceUpCard()); // In front of Player 2
        cardDownRight.setImageDrawable(wheelHand.get(2).getFaceUpCard()); // In front of Player 3
        cardDownLeft.setImageDrawable(wheelHand.get(3).getFaceUpCard()); // In front of Player 4

        controller.startRound(movesPerRound);
        decisionView.setText("Your Decision: " + String.valueOf(controller.getAllPlayers().get(0).getTurnDecision()));
    }

    public void endRound(){
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


    void postRound() {
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

        player1cards.setAdapter(null);
        player2cards.setAdapter(null);
        player3cards.setAdapter(null);
        player4cards.setAdapter(null);

        player1cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(0).getHand(), true));
        player2cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(1).getHand(), showCards));
        player3cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(2).getHand(), showCards));
        player4cards.setAdapter(new MyAdapter(controller.getAllPlayers().get(3).getHand(), showCards));

        if (controller.getRoundNum() <= 0) {
            Log.i("Post Game", "Start Ranking");
            ArrayList<Player> winnerList = controller.rankPlayers(numberOfCards, numberOfRounds);

            String tempString;
            Player winner = winnerList.get(0);
            Rank tempRank = winner.getRank();

            if (numberOfCards == 3) {
                switch (tempRank.getHandRank()) {
                    case 6:
                        tempString = "Straight Flush";
                        break;
                    case 5:
                        tempString = "Three of a Kind";
                        break;
                    case 4:
                        tempString = "Straight";
                        break;
                    case 3:
                        tempString = "Flush";
                        break;
                    case 2:
                        tempString = "Pair";
                        break;
                    default:
                        tempString = "High Card";
                        break;
                }
            } else {
                switch (tempRank.getHandRank()) {
                    case 10:
                        tempString = "Royal Flush";
                        break;
                    case 9:
                        tempString = "Straight Flush";
                        break;
                    case 8:
                        tempString = "Four of a Kind";
                        break;
                    case 7:
                        tempString = "Full House";
                        break;
                    case 6:
                        tempString = "Flush";
                        break;
                    case 5:
                        tempString = "Straight";
                        break;
                    case 4:
                        tempString = "Three of a Kind";
                        break;
                    case 3:
                        tempString = "Two Pair";
                        break;
                    case 2:
                        tempString = "Pair";
                        break;
                    default:
                        tempString = "High Card";
                        break;
                }
            }

            decisionView.setText("Winner: Player " + String.valueOf(winner.getPlayerNumber()) + "\nBest Hand:" + tempString);
            endTurnButton.setText("Play Again");

            endTurnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                    Log.i(TAG, "Restarting Single Player Game Activity");
                    intent.putExtra("game mode", "single");
                    startActivity(intent);
                }
            });
        }
    }

    private void initSettings() {
        Log.i(TAG, "connecting to settings db node");
        settingsRef = database.getReference("settings");
        settingsRef.child(ROUND).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Log.i(TAG, "retrieved number of rounds value");
                        numberOfRounds = (task.getResult().getValue() != null) ? ((int) task.getResult().getValue()) : 3;
                    }
                });
        settingsRef.child(MOVE).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        Log.i(TAG, "retrieved number of moves/round value");
                        movesPerRound = (task.getResult().getValue() != null) ? ((int) task.getResult().getValue()) : 1;
                    }
                });
        numberOfCards = numberOfRounds;
        showCards = true;
        SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);
        playerName = gamePrefs.getString("playerName", "");
    }

    private void setupMultiPlayer(ArrayList<Player> players) {
//        This code is not a true implementation of mutiplayer.
//        This implementation is due to the limitation of the Android Studio Emulators
        gameRef = database.getReference("game");
        gameRef.setValue("");
        gameRef.child("wheel").setValue("");
        gameRef.child("deck").setValue("");

        roomRef = database.getReference("room");
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "room onDataChange called");
//                Iterable<DataSnapshot> room = snapshot.getChildren();
//                int count = 0;
//                for (DataSnapshot d : room) {
//                    if ((d.getKey()) == playerName) {
//                        playerIndex = count;
//                    }
//                    if (count == 0) {
//                        player1name.setText(d.getKey());
//                        players.add(new Player());
//                    }
//                    count++;
//                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "error with room node");
            }
        });
        player1name.setText(playerName);
        players.add(new Player());
        players.add(new AIPlayer());
        players.add(new AIPlayer());
        players.add(new AIPlayer());
    }
}


