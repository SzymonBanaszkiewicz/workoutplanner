package org.example.persistence.mongo;

import org.example.domain.model.CardioExercise;
import org.example.domain.model.Exercise;
import org.example.domain.model.StrengthExercise;
import org.example.domain.model.User;
import org.example.domain.model.Workout;
import org.example.domain.model.WorkoutEntry;

import org.example.persistence.mongo.adapter.MongoExerciseRepositoryAdapter;
import org.example.persistence.mongo.adapter.MongoUserRepositoryAdapter;
import org.example.persistence.mongo.adapter.MongoWorkoutRepositoryAdapter;

import org.example.persistence.mongo.config.MongoConfig;
import org.example.persistence.mongo.config.MongoIndexInitializer;

import org.example.persistence.mongo.repository.MongoExerciseRepository;
import org.example.persistence.mongo.repository.MongoUserRepository;
import org.example.persistence.mongo.repository.MongoWorkoutRepository;

import org.bson.Document;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MongoIntegrationTest {

    private static MongoConfig mongoConfig;

    private static MongoUserRepository mongoUserRepository;
    private static MongoExerciseRepository mongoExerciseRepository;
    private static MongoWorkoutRepository mongoWorkoutRepository;

    private static MongoUserRepositoryAdapter userAdapter;
    private static MongoExerciseRepositoryAdapter exerciseAdapter;
    private static MongoWorkoutRepositoryAdapter workoutAdapter;

    @BeforeAll
    static void setUp() {
        mongoConfig = new MongoConfig();

        MongoIndexInitializer indexInitializer =
                new MongoIndexInitializer(mongoConfig.getDatabase());

        indexInitializer.initialize();

        mongoUserRepository =
                new MongoUserRepository(mongoConfig.getDatabase());

        mongoExerciseRepository =
                new MongoExerciseRepository(mongoConfig.getDatabase());

        mongoWorkoutRepository =
                new MongoWorkoutRepository(mongoConfig.getDatabase());

        userAdapter =
                new MongoUserRepositoryAdapter(mongoUserRepository);

        exerciseAdapter =
                new MongoExerciseRepositoryAdapter(mongoExerciseRepository);

        workoutAdapter =
                new MongoWorkoutRepositoryAdapter(
                        mongoWorkoutRepository,
                        mongoExerciseRepository
                );
    }

    @BeforeEach
    void cleanDatabase() {
        mongoConfig.getDatabase()
                .getCollection("users")
                .deleteMany(new Document());

        mongoConfig.getDatabase()
                .getCollection("exercises")
                .deleteMany(new Document());

        mongoConfig.getDatabase()
                .getCollection("workouts")
                .deleteMany(new Document());
    }

    @AfterAll
    static void tearDown() {
        if (mongoConfig != null) {
            mongoConfig.close();
        }
    }

    // =====================================================
    // USER REPOSITORY AND ADAPTER
    // =====================================================

    @Test
    void shouldSaveAndFindUserById() {
        User user = new User(
                UUID.randomUUID(),
                "test_user",
                "test@example.com"
        );

        userAdapter.save(user);

        User result = userAdapter.findById(user.getId())
                .orElseThrow();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User(
                UUID.randomUUID(),
                "email_user",
                "email@example.com"
        );

        userAdapter.save(user);

        User result = userAdapter.findByEmail(user.getEmail())
                .orElseThrow();

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {
        assertTrue(userAdapter.findById(UUID.randomUUID()).isEmpty());
        assertTrue(userAdapter.findByEmail("missing@example.com").isEmpty());
    }

    @Test
    void shouldReturnAllUsers() {
        User first = new User(
                UUID.randomUUID(),
                "first_user",
                "first@example.com"
        );

        User second = new User(
                UUID.randomUUID(),
                "second_user",
                "second@example.com"
        );

        userAdapter.save(first);
        userAdapter.save(second);

        List<User> users = userAdapter.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream()
                .anyMatch(user -> user.getId().equals(first.getId())));
        assertTrue(users.stream()
                .anyMatch(user -> user.getId().equals(second.getId())));
    }

    @Test
    void shouldDeleteUserById() {
        User user = new User(
                UUID.randomUUID(),
                "delete_user",
                "delete@example.com"
        );

        userAdapter.save(user);
        userAdapter.deleteById(user.getId());

        assertTrue(userAdapter.findById(user.getId()).isEmpty());
    }

    @Test
    void shouldUpdateUserWhenSavingWithExistingId() {
        UUID id = UUID.randomUUID();

        User original = new User(
                id,
                "original_user",
                "original@example.com"
        );

        userAdapter.save(original);

        User updated = new User(
                id,
                "updated_user",
                "updated@example.com"
        );

        userAdapter.save(updated);

        assertEquals(1, userAdapter.findAll().size());

        User result = userAdapter.findById(id).orElseThrow();

        assertEquals("updated_user", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
    }

    // =====================================================
    // EXERCISE REPOSITORY AND ADAPTER
    // =====================================================

    @Test
    void shouldSaveAndFindStrengthExercise() {
        StrengthExercise exercise = new StrengthExercise(
                UUID.randomUUID(),
                "Bench Press",
                "Chest",
                "Barbell"
        );

        exerciseAdapter.save(exercise);

        Exercise result = exerciseAdapter.findById(exercise.getId())
                .orElseThrow();

        assertInstanceOf(StrengthExercise.class, result);
        assertEquals(exercise.getId(), result.getId());
        assertEquals(exercise.getName(), result.getName());
    }

    @Test
    void shouldSaveAndFindCardioExercise() {
        CardioExercise exercise = new CardioExercise(
                UUID.randomUUID(),
                "Running",
                "Running",
                150
        );

        exerciseAdapter.save(exercise);

        Exercise result = exerciseAdapter.findById(exercise.getId())
                .orElseThrow();

        assertInstanceOf(CardioExercise.class, result);
        assertEquals(exercise.getId(), result.getId());
        assertEquals(exercise.getName(), result.getName());
    }

    @Test
    void shouldReturnEmptyWhenExerciseDoesNotExist() {
        assertTrue(
                exerciseAdapter.findById(UUID.randomUUID()).isEmpty()
        );
    }

    @Test
    void shouldReturnAllExercises() {
        StrengthExercise first = new StrengthExercise(
                UUID.randomUUID(),
                "Squat",
                "Legs",
                "Barbell"
        );

        CardioExercise second = new CardioExercise(
                UUID.randomUUID(),
                "Cycling",
                "Cycling",
                140
        );

        exerciseAdapter.save(first);
        exerciseAdapter.save(second);

        List<Exercise> exercises = exerciseAdapter.findAll();

        assertEquals(2, exercises.size());
        assertTrue(exercises.stream()
                .anyMatch(exercise ->
                        exercise.getId().equals(first.getId())));
        assertTrue(exercises.stream()
                .anyMatch(exercise ->
                        exercise.getId().equals(second.getId())));
    }

    @Test
    void shouldDeleteExerciseById() {
        StrengthExercise exercise = new StrengthExercise(
                UUID.randomUUID(),
                "Deadlift",
                "Back",
                "Barbell"
        );

        exerciseAdapter.save(exercise);
        exerciseAdapter.deleteById(exercise.getId());

        assertTrue(
                exerciseAdapter.findById(exercise.getId()).isEmpty()
        );
    }

    // =====================================================
    // WORKOUT REPOSITORY AND ADAPTER
    // =====================================================

    @Test
    void shouldSaveAndFindWorkout() {
        User user = new User(
                UUID.randomUUID(),
                "workout_user",
                "workout@example.com"
        );

        userAdapter.save(user);

        StrengthExercise exercise = new StrengthExercise(
                UUID.randomUUID(),
                "Bench Press",
                "Chest",
                "Barbell"
        );

        exerciseAdapter.save(exercise);

        LocalDateTime performedAt =
                LocalDateTime.of(2026, 10, 20, 17, 30);

        Workout workout = new Workout(
                UUID.randomUUID(),
                user.getId(),
                "Push Day",
                performedAt
        );

        workout.addEntry(new WorkoutEntry(
                UUID.randomUUID(),
                exercise,
                3,
                10,
                new BigDecimal("60.00"),
                null
        ));

        workoutAdapter.save(workout);

        Workout result = workoutAdapter.findById(workout.getId())
                .orElseThrow();

        assertEquals(workout.getId(), result.getId());
        assertEquals(workout.getUserId(), result.getUserId());
        assertEquals(workout.getName(), result.getName());
        assertEquals(workout.getPerformedAt(), result.getPerformedAt());

        assertEquals(1, result.getEntries().size());

        WorkoutEntry resultEntry = result.getEntries().get(0);

        assertEquals(exercise.getId(),
                resultEntry.getExercise().getId());

        assertEquals(3, resultEntry.getSets());
        assertEquals(10, resultEntry.getRepetitions());

        assertEquals(
                0,
                new BigDecimal("60.00")
                        .compareTo(resultEntry.getWeightKg())
        );
    }

    @Test
    void shouldReturnEmptyWhenWorkoutDoesNotExist() {
        assertTrue(
                workoutAdapter.findById(UUID.randomUUID()).isEmpty()
        );
    }

    @Test
    void shouldReturnAllWorkouts() {
        User user = new User(
                UUID.randomUUID(),
                "all_workouts_user",
                "allworkouts@example.com"
        );

        userAdapter.save(user);

        Workout first = new Workout(
                UUID.randomUUID(),
                user.getId(),
                "Push Day",
                LocalDateTime.of(2026, 10, 20, 10, 0)
        );

        Workout second = new Workout(
                UUID.randomUUID(),
                user.getId(),
                "Pull Day",
                LocalDateTime.of(2026, 10, 21, 10, 0)
        );

        workoutAdapter.save(first);
        workoutAdapter.save(second);

        List<Workout> workouts = workoutAdapter.findAll();

        assertEquals(2, workouts.size());
        assertTrue(workouts.stream()
                .anyMatch(workout ->
                        workout.getId().equals(first.getId())));
        assertTrue(workouts.stream()
                .anyMatch(workout ->
                        workout.getId().equals(second.getId())));
    }

    @Test
    void shouldFindWorkoutsByUserId() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        workoutAdapter.save(new Workout(
                UUID.randomUUID(),
                firstUserId,
                "First User Workout",
                LocalDateTime.of(2026, 10, 20, 10, 0)
        ));

        workoutAdapter.save(new Workout(
                UUID.randomUUID(),
                firstUserId,
                "Second Workout",
                LocalDateTime.of(2026, 10, 21, 10, 0)
        ));

        workoutAdapter.save(new Workout(
                UUID.randomUUID(),
                secondUserId,
                "Other User Workout",
                LocalDateTime.of(2026, 10, 22, 10, 0)
        ));

        List<Workout> results =
                workoutAdapter.findByUserId(firstUserId);

        assertEquals(2, results.size());
        assertTrue(results.stream()
                .allMatch(workout ->
                        workout.getUserId().equals(firstUserId)));
    }

    @Test
    void shouldDeleteWorkoutById() {
        Workout workout = new Workout(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Workout to delete",
                LocalDateTime.of(2026, 10, 20, 10, 0)
        );

        workoutAdapter.save(workout);
        workoutAdapter.deleteById(workout.getId());

        assertTrue(
                workoutAdapter.findById(workout.getId()).isEmpty()
        );
    }

    @Test
    void shouldPreserveWorkoutEntryExerciseReference() {
        StrengthExercise exercise = new StrengthExercise(
                UUID.randomUUID(),
                "Squat",
                "Legs",
                "Barbell"
        );

        exerciseAdapter.save(exercise);

        Workout workout = new Workout(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Leg Day",
                LocalDateTime.of(2026, 10, 20, 10, 0)
        );

        workout.addEntry(new WorkoutEntry(
                UUID.randomUUID(),
                exercise,
                4,
                8,
                new BigDecimal("80.00"),
                null
        ));

        workoutAdapter.save(workout);

        Workout result = workoutAdapter.findById(workout.getId())
                .orElseThrow();

        assertEquals(1, result.getEntries().size());
        assertEquals(
                exercise.getId(),
                result.getEntries().get(0).getExercise().getId()
        );
    }
}