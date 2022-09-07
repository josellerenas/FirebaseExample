package com.example.firebaseexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//Troubleshooting
//
//1.- The error I was stuck for was simply because the user needs to enter a password
//    with a minimum of 6 characters, and I had been putting a 4 character password.

public class AuthActivity extends AppCompatActivity {

    // Constants
    private int GOOGLE_SIGN_IN = 100;

    // Declaring variables
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    Button btnSignUp, btnLogIn, btnLogInGoogle;
    EditText editTxtEmail, editTxtPassword;
    ConstraintLayout layoutAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initializing variables
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogIn = findViewById(R.id.btnLogIn);
        editTxtEmail = findViewById(R.id.editTxtEmail);
        editTxtPassword = findViewById(R.id.editTxtPassword);
        layoutAuth = findViewById(R.id.layoutAuth);
        btnLogInGoogle = findViewById(R.id.btnLogInGoogle);

        // Analytics Event
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("message", "Integración de Firebase completa");
        mFirebaseAnalytics.logEvent("InitScreen", bundle);

        // Auth
        mAuth = FirebaseAuth.getInstance();

        // Setup
        setup();
        session();
    }

    // I set my Auth Layout visible when starting the page again.
    @Override
    protected void onStart() {
        super.onStart();

        layoutAuth.setVisibility(View.VISIBLE);
    }

    private void session() {
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        String email =  prefs.getString("email", null);
        String provider = prefs.getString("provider", null);

        if (email != null && provider != null) {
            // TODO put layout invisible
            layoutAuth.setVisibility(View.GONE);
            showHome(email, ProviderType.valueOf(provider));
        }
    }

    private void setup() {
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTxtEmail.getText().toString().equals("") && !editTxtPassword.getText().toString().equals("")) {
                    mAuth.createUserWithEmailAndPassword(editTxtEmail.getText().toString(),
                                    editTxtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        showHome(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                                    } else {
                                        showAlert();
                                    }
                                }
                            });
                }
            }
        });

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTxtEmail.getText().toString().equals("") && !editTxtPassword.getText().toString().equals("")) {
                    mAuth.signInWithEmailAndPassword(editTxtEmail.getText().toString(),
                                    editTxtPassword.getText().toString()).addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                showHome(task.getResult().getUser().getEmail(), ProviderType.BASIC);
                            } else {
                                showAlert();
                            }
                        }
                    });
                }
            }
        });

        btnLogInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Setup
                GoogleSignInOptions googleConf = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                // TODO Keep setting up the Google LogIn
                GoogleSignInClient googleClient = GoogleSignIn.getClient(AuthActivity.this, googleConf);
//                startActivity(googleClient.getSignInIntent(), GOOGLE_SIGN_IN);
            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage("Se ha producido un error autenticando al usuario");
        builder.setPositiveButton("Aceptar", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showHome(String email, ProviderType provider) {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("email", email);
        homeIntent.putExtra("provider", provider.name());
        startActivity(homeIntent);
    }
}