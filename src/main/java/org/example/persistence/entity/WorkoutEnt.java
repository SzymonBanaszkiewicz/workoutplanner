package org.example.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workouts")
public class WorkoutEnt {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private LocalDateTime performedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEnt user;

    @OneToMany(
            mappedBy = "workout",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<WorkoutEntryEnt> entries = new ArrayList<>();

    protected WorkoutEnt() {
    }

    public WorkoutEnt(UUID id, String name, LocalDateTime performedAt) {
        this.id = id;
        this.name = name;
        this.performedAt = performedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public UserEnt getUser() {
        return user;
    }

    public void setUser(UserEnt user) {
        this.user = user;
    }

    public List<WorkoutEntryEnt> getEntries() {
        return entries;
    }

    public void addEntry(WorkoutEntryEnt entry) {
        entries.add(entry);
        entry.setWorkout(this);
    }

    public void removeEntry(WorkoutEntryEnt entry) {
        entries.remove(entry);
        entry.setWorkout(null);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}