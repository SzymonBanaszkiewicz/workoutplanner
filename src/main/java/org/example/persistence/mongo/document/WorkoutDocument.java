package org.example.persistence.mongo.document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WorkoutDocument {
    private UUID id;
    private Long version;
    private UUID userId;
    private String name;
    private LocalDateTime performedAt;
    private List<WorkoutEntryDocument> entries = new ArrayList<>();

    public WorkoutDocument() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }

    public List<WorkoutEntryDocument> getEntries() {
        return entries;
    }

    public void setEntries(List<WorkoutEntryDocument> entries) {
        this.entries = Objects.requireNonNullElseGet(entries, ArrayList::new);
    }
}

