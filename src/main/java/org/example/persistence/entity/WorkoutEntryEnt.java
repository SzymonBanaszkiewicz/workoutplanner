package org.example.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "workout_entries")
public class WorkoutEntryEnt {

    @Id
    private UUID id;

    private Integer sets;

    private Integer repetitions;

    @Column(precision = 7, scale = 2)
    private BigDecimal weightKg;

    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private WorkoutEnt workout;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private ExerciseEnt exercise;

    protected WorkoutEntryEnt() {
    }

    public WorkoutEntryEnt(
            UUID id,
            Integer sets,
            Integer repetitions,
            BigDecimal weightKg,
            Integer durationMinutes,
            ExerciseEnt exercise
    ) {
        this.id = id;
        this.sets = sets;
        this.repetitions = repetitions;
        this.weightKg = weightKg;
        this.durationMinutes = durationMinutes;
        this.exercise = exercise;
    }

    public UUID getId() {
        return id;
    }

    public Integer getSets() {
        return sets;
    }

    public Integer getRepetitions() {
        return repetitions;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public WorkoutEnt getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutEnt workout) {
        this.workout = workout;
    }

    public ExerciseEnt getExercise() {
        return exercise;
    }

    public void setExercise(ExerciseEnt exercise) {
        this.exercise = exercise;
    }
}