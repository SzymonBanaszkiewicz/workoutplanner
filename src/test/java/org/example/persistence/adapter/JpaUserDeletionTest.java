package org.example.persistence.adapter;

import org.example.domain.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaUserDeletionTest {

    private final JpaUserRepositoryAdapter userRepository =
            new JpaUserRepositoryAdapter();

    private final JpaExerciseRepositoryAdapter exerciseRepository =
            new JpaExerciseRepositoryAdapter();

    private final JpaWorkoutRepositoryAdapter workoutRepository =
            new JpaWorkoutRepositoryAdapter();

    @Test
    void shouldDeleteUsersWorkoutsButKeepSharedExercise() {
        String unique = UUID.randomUUID().toString();

        User user = userRepository.save(
                new User("user_" + unique.substring(0, 8),
                        unique + "@example.com")
        );

        Exercise exercise = exerciseRepository.save(
                new StrengthExercise(
                        "Squat " + unique,
                        "Legs",
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

        userRepository.deleteById(user.getId());

        assertTrue(
                userRepository.findById(user.getId()).isEmpty(),
                "User should be deleted"
        );

        assertTrue(
                workoutRepository.findById(savedWorkout.getId()).isEmpty(),
                "Users workout should be deleted"
        );

        assertTrue(
                exerciseRepository.findById(exercise.getId()).isPresent(),
                "Shared exercise should remain"
        );

        exerciseRepository.deleteById(exercise.getId());
    }
}
