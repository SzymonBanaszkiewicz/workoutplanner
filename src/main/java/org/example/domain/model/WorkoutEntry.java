package org.example.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class WorkoutEntry {

    private final UUID id;
    private final Exercise exercise;

    private Integer sets;
    private Integer repetitions;
    private BigDecimal weightKg;
    private Integer durationMinutes;

    public WorkoutEntry(
            UUID id,
            Exercise exercise,
            Integer sets,
            Integer repetitions,
            BigDecimal weightKg,
            Integer durationMinutes
    ) {
        this.id = Objects.requireNonNull(id, "Entry ID cannot be null");
        this.exercise = Objects.requireNonNull(
                exercise, "Exercise cannot be null"
        );

        setSets(sets);
        setRepetitions(repetitions);
        setWeightKg(weightKg);
        setDurationMinutes(durationMinutes);
    }

    public WorkoutEntry(
            Exercise exercise,
            Integer sets,
            Integer repetitions,
            BigDecimal weightKg,
            Integer durationMinutes
    ) {
        this(
                UUID.randomUUID(),
                exercise,
                sets,
                repetitions,
                weightKg,
                durationMinutes
        );
    }

    public UUID getId() {
        return id;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public Integer getSets() {
        return sets;
    }

    public void setSets(Integer sets) {
        if (sets != null && sets <= 0) {
            throw new IllegalArgumentException(
                    "Sets must be positive"
            );
        }
        this.sets = sets;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(Integer repetitions) {
        if (repetitions != null && repetitions <= 0) {
            throw new IllegalArgumentException(
                    "Repetitions must be positive"
            );
        }
        this.repetitions = repetitions;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        if (weightKg != null
                && weightKg.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Weight cannot be negative"
            );
        }
        this.weightKg = weightKg;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        if (durationMinutes != null && durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be positive"
            );
        }
        this.durationMinutes = durationMinutes;
    }
}