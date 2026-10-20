
package org.example.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateWorkoutCommand {

    private final UUID userId;
    private final String name;
    private final LocalDateTime performedAt;

    public CreateWorkoutCommand(UUID userId, String name, LocalDateTime performedAt) {
        this.userId = userId;
        this.name = name;
        this.performedAt = performedAt;
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
}
