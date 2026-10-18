package org.example.domain.model;

import java.util.Objects;
import java.util.UUID;

public abstract class Exercise {

    private final UUID id;
    private String name;

    protected Exercise(UUID id, String name) {
        this.id = Objects.requireNonNull(id, "Exercise ID cannot be null");
        setName(name);
    }

    protected Exercise(String name) {
        this(UUID.randomUUID(), name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException(
                    "Exercise name cannot be empty"
            );
        }

        this.name = name.trim();
    }
}