
package org.example.persistence.adapter;

import org.example.domain.model.*;
import org.example.persistence.postgres.adapter.JpaExerciseRepositoryAdapter;
import org.example.persistence.postgres.adapter.JpaUserRepositoryAdapter;
import org.example.persistence.postgres.adapter.JpaWorkoutRepositoryAdapter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JpaExerciseDeletionTest {

    private final JpaExerciseRepositoryAdapter exerciseRepository =
            new JpaExerciseRepositoryAdapter();

    private final JpaUserRepositoryAdapter userRepository =
            new JpaUserRepositoryAdapter();

    private final JpaWorkoutRepositoryAdapter workoutRepository =
            new JpaWorkoutRepositoryAdapter();

    @Test
    void shouldDeleteExerciseWhenItIsNotUsedInAnyWorkout() {
        Exercise exercise = exerciseRepository.save(
                new StrengthExercise(
                        "Test squat " + UUID.randomUUID(),
                        "Legs",
                        "Barbell"
                )
        );

        exerciseRepository.deleteById(exercise.getId());

        assertTrue(
                exerciseRepository.findById(exercise.getId()).isEmpty(),
                "Exercise should no longer exist in the database"
        );
    }

    @Test
    void shouldRejectDeletingExerciseUsedInWorkout() {
        User user = userRepository.save(
                new User(
                        "testuser_" + UUID.randomUUID().toString().substring(0, 8),
                        "test_" + UUID.randomUUID() + "@example.com"
                )
        );

        Exercise exercise = exerciseRepository.save(
                new StrengthExercise(
                        "Test bench press " + UUID.randomUUID(),
                        "Chest",
                        "Barbell"
                )
        );

        Workout workout = new Workout(
                user.getId(),
                "Test workout",
                LocalDateTime.now()
        );

        workout.addEntry(
                new WorkoutEntry(
                        exercise,
                        3,
                        10,
                        new BigDecimal("40.0"),
                        null
                )
        );

        Workout savedWorkout = workoutRepository.save(workout);

        try {
            assertThrows(
                    IllegalStateException.class,
                    () -> exerciseRepository.deleteById(exercise.getId()),
                    "Deleting an exercise used in a workout should fail"
            );

            assertTrue(
                    exerciseRepository.findById(exercise.getId()).isPresent(),
                    "Exercise should remain in the database"
            );

            Workout reloadedWorkout = workoutRepository
                    .findById(savedWorkout.getId())
                    .orElseThrow();

            assertEquals(
                    1,
                    reloadedWorkout.getEntries().size(),
                    "Workout entry should remain after rejected deletion"
            );

        } finally {
            workoutRepository.deleteById(savedWorkout.getId());
            userRepository.deleteById(user.getId());
            exerciseRepository.deleteById(exercise.getId());
        }
    }
}
