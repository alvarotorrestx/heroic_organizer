package com.example.heroicorganizer.model;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class User {
    // User fields
    private String uid;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String email;
    private String username;
    private String password;
    private String role;

    private static final List<String> VALID_ROLES = Arrays.asList("user", "admin", "owner");

    public User() {}

    public User(String firstName, String lastName, String dateOfBirth, String email, String username, String password, String role) {

        if (firstName == null || lastName == null || dateOfBirth == null || email == null || username == null || password == null) throw new IllegalArgumentException("All fields must be filled.");

        // Default user created if not specified
        if (!VALID_ROLES.contains(role)) role = "user";

        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Convert user object for Firestore presenter
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("dateOfBirth", dateOfBirth);
        map.put("email", email);
        map.put("username", username);
        map.put("role", role);
        return map;
    }

    // Getters
    public String getUid() {
        return uid;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters
    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = VALID_ROLES.contains(role) ? role : "user";
    }
}
