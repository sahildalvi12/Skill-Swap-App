package com.example.skillswap.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.skillswap.EditProfileActivity;
import com.example.skillswap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final int EDIT_PROFILE_REQUEST = 1001;

    TextView txtName, txtEmail, txtBio, txtTeaches, txtWantsToLearn;
    ImageView imgProfile;
    ProgressBar progressBar;
    Button btnEditProfile;

    FirebaseAuth auth;
    FirebaseFirestore db;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtBio = view.findViewById(R.id.txtBio);
        txtTeaches = view.findViewById(R.id.txtTeaches);
        txtWantsToLearn = view.findViewById(R.id.txtWantsToLearn);
        imgProfile = view.findViewById(R.id.imgProfile);
        progressBar = view.findViewById(R.id.progressBar);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);

        String uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE);

                    if (documentSnapshot.exists()) {
                        txtName.setText(documentSnapshot.getString("name"));
                        txtEmail.setText(documentSnapshot.getString("email"));
                        txtBio.setText(documentSnapshot.getString("bio"));
                        txtTeaches.setText(documentSnapshot.get("teaches") != null ?
                                documentSnapshot.get("teaches").toString() : "");
                        txtWantsToLearn.setText(documentSnapshot.get("wantsToLearn") != null ?
                                documentSnapshot.get("wantsToLearn").toString() : "");

                        String profileImage = documentSnapshot.getString("profileImage");
                        if (profileImage != null && !profileImage.isEmpty()) {
                            Glide.with(this).load(profileImage).into(imgProfile);
                        }
                    } else {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            loadUserProfile(); // Refresh when coming back from EditProfileActivity
        }
    }
}
