package org.example.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Workout {

    private final UUID id;
    private final UUID userId;

    private String name;
    private LocalDateTime performedAt;

    private final List<WorkoutEntry> entries = new ArrayList<>();

    public Workout(
            UUID id,
            UUID userId,
            String name,
            LocalDateTime performedAt
    ) {
        this.id = Objects.requireNonNull(id, "Workout ID cannot be null");
        this.userId = Objects.requireNonNull(
                userId, "User ID cannot be null"
        );

        setName(name);
        setPerformedAt(performedAt);
    }

    public Workout(
            UUID userId,
            String name,
            LocalDateTime performedAt
    ) {
        this(UUID.randomUUID(), userId, name, performedAt);
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Workout name cannot be empty"
            );
        }
        this.name = name.trim();
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = Objects.requireNonNull(
                performedAt, "Workout date cannot be null"
        );
    }

    public List<WorkoutEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public void addEntry(WorkoutEntry entry) {
        Objects.requireNonNull(entry, "Workout entry cannot be null");
        entries.add(entry);
    }

    public boolean removeEntry(UUID entryId) {
        return entries.removeIf(entry -> entry.getId().equals(entryId));
    }
}