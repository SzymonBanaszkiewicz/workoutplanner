
package org.example.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkoutSummary {

    private final UUID id;
    private final UUID userId;
    private final String name;
    private final LocalDateTime performedAt;
    private final int exerciseCount;

    public WorkoutSummary(
            UUID id,
            UUID userId,
            String name,
            LocalDateTime performedAt,
            int exerciseCount
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.performedAt = performedAt;
        this.exerciseCount = exerciseCount;
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

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }
}
