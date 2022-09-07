package com.example.firebaseexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

enum ProviderType {
    BASIC,
    GOOGLE
}

public class HomeActivity extends AppCompatActivity {

    // Declaring variables
    TextView txtViewEmail, txtViewMethod;
    Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initializing variables
        txtViewEmail = findViewById(R.id.txtViewEmail);
        txtViewMethod = findViewById(R.id.txtViewMethod);
        btnLogOut = findViewById(R.id.btnLogOut);

        // Setup
        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String provider = intent.getStringExtra("provider");
        setup(email, provider);

        // Saving user data in SharedPreferences
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", email);
        editor.putString("provider", provider);
        editor.apply();
    }

    private void setup(String email, String method) {
        txtViewEmail.setText(email);
        txtViewMethod.setText(method);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete user data in SharedPreferences
                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                onBackPressed();
            }
        });
    }
}