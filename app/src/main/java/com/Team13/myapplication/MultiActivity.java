package com.Team13.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MultiActivity extends AppCompatActivity {
    private static final String TAG = "MULTI_ACTIVITY";

    private DatabaseReference playerRef;
    private EditText nameInput;
    private Button enterName;
    private String playerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //    Check if player already exists in database and get reference to them
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called");

        setContentView(R.layout.activity_multi);

        nameInput = findViewById(R.id.nameInput);
        enterName = findViewById(R.id.multiple);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);
        playerName = gamePrefs.getString("playerName", "");
        if (!playerName.equals("")) {
            Log.i(TAG, "found player in db");
            playerRef = database.getReference("players/" + playerName);
            addEventListener();
            addEventListener();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("isDone", false);
            childUpdates.put("hand", "");
            childUpdates.put("rank", "");
            childUpdates.put("turn decision", 0);
            playerRef.updateChildren(childUpdates);
        }

        enterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "enterName onClick called");
                playerName = nameInput.getText().toString();
                nameInput.setText("");
                if (!playerName.equals("")) {
                    Log.i(TAG, "creating new player");
                    enterName.setText("CREATING PLAYER");
                    enterName.setEnabled(false);
                    playerRef = database.getReference("players/" + playerName);
                    addEventListener();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("isDone", false);
                    childUpdates.put("hand", "");
                    childUpdates.put("rank", "");
                    childUpdates.put("turn decision", 0);
                    playerRef.updateChildren(childUpdates);
                }
            }
        });

    }

    private void addEventListener() {
        playerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!playerName.equals("")) {
                    SharedPreferences gamePrefs = getSharedPreferences("GAME-PREFS", 0);
                    SharedPreferences.Editor editor = gamePrefs.edit();
                    editor.putString("playerName", playerName);
                    editor.apply();
                    Log.i(TAG, "player added to db");
                    Intent intent = new Intent(MultiActivity.this, RoomActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i(TAG, "error adding player to db");
                enterName.setText("Enter");
                enterName.setEnabled(true);
                Toast.makeText(MultiActivity.this, "Error creating player", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
