package org.example.domain.model;

import java.util.*;

public class User {

    private final UUID id;

    private String username;
    private String email;

    private final List<Workout> workouts = new ArrayList<>();

    public User(UUID id, String username, String email) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        setUsername(username);
        setEmail(email);
    }

    public User(String username, String email) {
        this(UUID.randomUUID(), username, email);
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty"
            );
        }

        this.username = username.trim();
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "Invalid email address"
            );
        }
        this.email = email.trim();
    }

    public List<Workout> getWorkouts() {
        return Collections.unmodifiableList(workouts);
    }

    public void addWorkout(Workout workout) {
        Objects.requireNonNull(workout, "Workout cannot be null");

        if (!workout.getUserId().equals(id)) {
            throw new IllegalArgumentException(
                    "Workout belongs to another user"
            );
        }

        boolean alreadyAdded = workouts.stream()
                .anyMatch(existing -> existing.getId().equals(workout.getId()));

        if (!alreadyAdded) {
            workouts.add(workout);
        }
    }

    public boolean removeWorkout(UUID workoutId) {
        return workouts.removeIf(
                workout -> workout.getId().equals(workoutId)
        );
    }
}