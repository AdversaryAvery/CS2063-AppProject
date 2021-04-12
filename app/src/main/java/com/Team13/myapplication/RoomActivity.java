package com.Team13.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RoomActivity extends AppCompatActivity {
    private static final String TAG = "ROOM_ACTIVITY";
    private FirebaseDatabase database;

    private DatabaseReference roomPlayerRef;
    private DatabaseReference roomRef;
    private Button roomButton;
    private ListView roomListView;
    private ArrayList<String> roomList;
    private TextView playerCount;
    private TextView joinText;
    private String playerName;
    private int numPlayers = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //    Check if player already exists in database and get reference to them
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");
        setContentView(R.layout.activity_room);
        database = FirebaseDatabase.getInstance();
        SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);

        roomButton = findViewById(R.id.roomButton);
        roomListView = findViewById(R.id.roomList);
        playerCount = findViewById(R.id.playerCount);
        joinText = findViewById(R.id.joinText);
        roomList = new ArrayList<String>();

        playerName = gamePrefs.getString("playerName", "");

        setupRoomEventListener();

        playerCount.setText("0");

        roomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "roomCreate onClick called");
                roomButton.setText("Creating Room");
                roomButton.setEnabled(false);
                roomPlayerRef = database.getReference("room/"+ playerName);
                addRoomEventListener();
                roomPlayerRef.setValue(playerName);
            }
        });

        roomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                roomPlayerRef = database.getReference("room/"+ playerName);
                Log.i(TAG, "joining existing room");
                addRoomEventListener();
                roomPlayerRef.setValue(playerName);
            }
        });

        getRoomDisplay();
    }

    private void addRoomEventListener() {
        roomPlayerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//               player created/joined the room start game
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (roomList.size() < 2) {
                    Log.i(TAG, "Room onDataCancelled called when creating room ");
                    roomButton.setText("Create Room");
                    roomButton.setEnabled(true);
                    Toast.makeText(RoomActivity.this, "Error creating room", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Room onDataCancelled called when adding player ");
                    Toast.makeText(RoomActivity.this, "Error joining room", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupRoomEventListener() {
        roomRef = database.getReference("room");
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i(TAG, "room onDataChange called");
                roomList.clear();
                Iterable<DataSnapshot> room =snapshot.getChildren();
                for (DataSnapshot d : room) {
                    roomList.add(d.getKey());
                    ArrayAdapter<String> adapter =
                            new ArrayAdapter<>(RoomActivity.this, android.R.layout.simple_list_item_1, roomList);
                    roomListView.setAdapter(adapter);
                }
                getRoomDisplay();
                Log.i(TAG, "roomsListView updated" + roomList.size());
                if (roomList.size() == numPlayers) {
                    Log.i(TAG, "All players are in room ");
                    Intent intent = new Intent(RoomActivity.this, GameActivity.class);
                    intent.putExtra("game mode", "multi");
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "error with room node");
            }
        });
    }

    private void getRoomDisplay() {
        playerCount.setText(""+ roomList.size());
        if (roomList.size() < 1) {
            roomButton.setVisibility(View.VISIBLE);
            roomListView.setVisibility(View.GONE);
            joinText.setVisibility(View.GONE);
            playerCount.setVisibility(View.GONE);
        } else {
            roomButton.setVisibility(View.GONE);
            roomListView.setVisibility(View.VISIBLE);
            joinText.setVisibility(View.VISIBLE);
            playerCount.setVisibility(View.VISIBLE);
        }
    }
}
