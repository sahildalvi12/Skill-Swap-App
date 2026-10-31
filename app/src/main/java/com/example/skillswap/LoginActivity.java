package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText inputEmail, inputPassword;
    Button btnLogin;
    TextView txtRegister;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();

                                // Fetch Firestore data
                                db.collection("users").document(uid)
                                        .get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                String name = documentSnapshot.getString("name");
                                                Toast.makeText(this, "Welcome, " + name, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(this, "Profile not found!", Toast.LENGTH_SHORT).show();
                                            }
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        txtRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }
}
