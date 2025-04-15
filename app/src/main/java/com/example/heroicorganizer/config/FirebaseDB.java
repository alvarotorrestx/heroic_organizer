package com.example.heroicorganizer.config;

// Imports
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDB {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseAuth getAuth() {
        return mAuth;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }

    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
