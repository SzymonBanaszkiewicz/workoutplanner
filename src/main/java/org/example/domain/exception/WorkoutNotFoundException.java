package org.example.domain.exception;

import java.util.UUID;

public class WorkoutNotFoundException extends DomainException {

    public WorkoutNotFoundException(UUID workoutId) {
        super("Workout not found: " + workoutId);
    }
}