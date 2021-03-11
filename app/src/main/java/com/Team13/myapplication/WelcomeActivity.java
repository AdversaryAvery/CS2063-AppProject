package com.Team13.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WELCOME_ACTIVITY";

    private Button playGameButton;
    private Button instructionsButton;
    private Button signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        instructionsButton = findViewById(R.id.btnInstructions);
        instructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        playGameButton = findViewById(R.id.btnBack);
        playGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, GameActivity.class);
                Log.i(TAG, "starting Game Activity");
                startActivity(intent);
            }
        });

        signInButton = findViewById(R.id.gameLink);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setAndroidPackageName(getPackageName(),
                                true, /* install if not available? */
                                null   /* minimum app version */)
                        .setHandleCodeInApp(true)
                        .setUrl("https://fire.example.com/emailSignInLink")
                        .build();
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn().
                                setActionCodeSettings(actionCodeSettings).build())).build(), 1234);
            }
        });

        if (AuthUI.canHandleIntent(getIntent())) {
            String link = getIntent().getData().toString();

            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build());

            Log.d(TAG, "got an email link: " + link);

            if (link != null) {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setEmailLink(link)
                                .setAvailableProviders(providers)
                                .build(),
                        12345);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(), "Successfully signed in!", Toast.LENGTH_SHORT).show();

            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(getApplicationContext(), "Sign in FAILED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
