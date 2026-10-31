package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    Button btnSignUp;
    TextView txtLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtLogin = findViewById(R.id.txtLogin);

        btnSignUp.setOnClickListener(v -> {
            String name = inputName.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            String confirmPassword = inputConfirmPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();

                                // Prepare user profile data
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("bio", "");
                                user.put("teaches", Arrays.asList());
                                user.put("wantsToLearn", Arrays.asList());
                                user.put("rating", 0.0);
                                user.put("profileImage", "");

                                // Save to Firestore
                                db.collection("users").document(uid)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, MainActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            } else {
                                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}
