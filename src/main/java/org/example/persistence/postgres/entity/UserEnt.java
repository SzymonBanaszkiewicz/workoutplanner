package org.example.persistence.postgres.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class UserEnt {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<WorkoutEnt> workouts = new ArrayList<>();

    protected UserEnt() {
    }

    public UserEnt(UUID id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<WorkoutEnt> getWorkouts() {
        return workouts;
    }

    public void addWorkout(WorkoutEnt workout) {
        workouts.add(workout);
        workout.setUser(this);
    }

    public void removeWorkout(WorkoutEnt workout) {
        workouts.remove(workout);
        workout.setUser(null);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}