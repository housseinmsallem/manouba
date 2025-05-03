package com.example.manouba;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView nameText, emailText, genderText, phoneText;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views

        nameText = view.findViewById(R.id.nameText);
        emailText = view.findViewById(R.id.emailText);
        genderText = view.findViewById(R.id.genderText);
        phoneText = view.findViewById(R.id.phoneText);

        // Set click listeners
        view.findViewById(R.id.editName).setOnClickListener(v -> editName());
        view.findViewById(R.id.editEmail).setOnClickListener(v -> editEmail());
        view.findViewById(R.id.editGender).setOnClickListener(v -> editGender());
        view.findViewById(R.id.editPhone).setOnClickListener(v -> editPhone());


        // Check authentication state
        updateUI();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in

            loadUserData(currentUser);
        }
    }

    private void loadUserData(FirebaseUser currentUser) {
        // Set email from Firebase Auth
        emailText.setText(currentUser.getEmail());

        // Load additional user data from Firestore
        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    nameText.setText(document.getString("name"));
                    genderText.setText(document.getString("gender"));
                    phoneText.setText(document.getString("phone"));
                } else {
                    Toast.makeText(getContext(), "No user data found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    private void editName() {
        Toast.makeText(getContext(), "Edit name clicked", Toast.LENGTH_SHORT).show();
    }

    private void editEmail() {
        Toast.makeText(getContext(), "Edit email clicked", Toast.LENGTH_SHORT).show();
    }

    private void editGender() {
        Toast.makeText(getContext(), "Edit gender clicked", Toast.LENGTH_SHORT).show();
    }

    private void editPhone() {
        Toast.makeText(getContext(), "Edit phone clicked", Toast.LENGTH_SHORT).show();
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        updateUI();
    }
}