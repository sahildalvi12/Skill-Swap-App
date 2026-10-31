package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText editName, editBio, editTeaches, editWantsToLearn, editProfileImage;
    Button btnSave;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        editName = findViewById(R.id.editName);
        editBio = findViewById(R.id.editBio);
        editTeaches = findViewById(R.id.editTeaches);
        editWantsToLearn = findViewById(R.id.editWantsToLearn);
        editProfileImage = findViewById(R.id.editProfileImage);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        // Load existing profile
        loadProfileData();

        // Save button action
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadProfileData() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(document -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    if (document.exists()) {
                        editName.setText(document.getString("name"));
                        editBio.setText(document.getString("bio"));
                        editProfileImage.setText(document.getString("profileImage"));

                        // Convert list to comma separated
                        if (document.get("teaches") != null) {
                            editTeaches.setText(document.get("teaches").toString().replaceAll("[\\[\\]]", ""));
                        }
                        if (document.get("wantsToLearn") != null) {
                            editWantsToLearn.setText(document.get("wantsToLearn").toString().replaceAll("[\\[\\]]", ""));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProfile() {
        String uid = auth.getCurrentUser().getUid();

        String name = editName.getText().toString().trim();
        String bio = editBio.getText().toString().trim();
        String teaches = editTeaches.getText().toString().trim();
        String wantsToLearn = editWantsToLearn.getText().toString().trim();
        String profileImage = editProfileImage.getText().toString().trim();

        if (name.isEmpty()) {
            editName.setError("Name required");
            return;
        }

        progressBar.setVisibility(ProgressBar.VISIBLE);

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("bio", bio);
        userUpdates.put("profileImage", profileImage);
        userUpdates.put("teaches", Arrays.asList(teaches.split("\\s*,\\s*")));
        userUpdates.put("wantsToLearn", Arrays.asList(wantsToLearn.split("\\s*,\\s*")));

        db.collection("users").document(uid).update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();

                    // Return RESULT_OK so ProfileFragment refreshes
                    setResult(RESULT_OK, new Intent());
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
