
package org.example.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.domain.model.StrengthExercise;
import org.example.domain.model.Workout;
import org.example.domain.model.WorkoutEntry;
import org.example.persistence.config.JpaConfig;
import org.example.persistence.entity.ExerciseEnt;
import org.example.persistence.entity.UserEnt;
import org.example.persistence.entity.WorkoutEntryEnt;
import org.example.persistence.mapper.ExerciseMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JpaWorkoutRepositoryAdapterTest {

    private final JpaWorkoutRepositoryAdapter workouts =
            new JpaWorkoutRepositoryAdapter();

    private final EntityManager em =
            JpaConfig.getEntityManagerFactory().createEntityManager();

    @AfterAll
    static void shutdown() {
        JpaConfig.shutdown();
    }

    @AfterEach
    void closeEntityManager() {
        if (em.isOpen()) {
            em.close();
        }
    }

    @Test
    void shouldSaveAndFindWorkoutWithEntry() {
        UUID userId = UUID.randomUUID();
        UUID exerciseId = UUID.randomUUID();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            UserEnt user = new UserEnt(
                    userId,
                    "user_" + userId.toString().substring(0, 8),
                    userId + "@example.com"
            );
            em.persist(user);

            StrengthExercise exercise = new StrengthExercise(
                    exerciseId,
                    "Bench Press",
                    "Chest",
                    "Barbell"
            );

            ExerciseEnt exerciseEntity =
                    ExerciseMapper.toEntity(exercise);

            em.persist(exerciseEntity);

            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }

        StrengthExercise exercise = new StrengthExercise(
                exerciseId,
                "Bench Press",
                "Chest",
                "Barbell"
        );

        Workout workout = new Workout(
                userId,
                "Push Day",
                LocalDateTime.now()
        );

        workout.addEntry(new WorkoutEntry(
                exercise,
                3,
                10,
                new BigDecimal("60.00"),
                null
        ));

        Workout saved = workouts.save(workout);

        assertNotNull(saved.getId());

        var found = workouts.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Push Day", found.get().getName());
        assertEquals(userId, found.get().getUserId());
        assertEquals(1, found.get().getEntries().size());

        WorkoutEntry entry = found.get().getEntries().get(0);
        assertEquals("Bench Press", entry.getExercise().getName());
        assertEquals(3, entry.getSets());
        assertEquals(10, entry.getRepetitions());
        assertEquals(0, entry.getWeightKg().compareTo(
                new BigDecimal("60.00")
        ));

        assertEquals(
                1,
                workouts.findByUserId(userId).size()
        );

        saved.setName("Push Day Updated");

        Workout updated = workouts.save(saved);

        assertEquals(saved.getId(), updated.getId());
        assertEquals("Push Day Updated", updated.getName());

        var updatedFromDb = workouts.findById(saved.getId());

        assertTrue(updatedFromDb.isPresent());
        assertEquals("Push Day Updated", updatedFromDb.get().getName());
        assertEquals(1, updatedFromDb.get().getEntries().size());

        UUID entryId = updated.getEntries().get(0).getId();

        workouts.deleteById(updated.getId());

        assertTrue(workouts.findById(updated.getId()).isEmpty());

        assertNull(em.find(WorkoutEntryEnt.class, entryId));

        EntityTransaction cleanupTx = em.getTransaction();
        try {
            cleanupTx.begin();
            em.remove(em.find(ExerciseEnt.class, exerciseId));
            em.remove(em.find(UserEnt.class, userId));
            cleanupTx.commit();
        } catch (RuntimeException e) {
            if (cleanupTx.isActive()) {
                cleanupTx.rollback();
            }
            throw e;
        }
    }

    @Test
    void shouldRejectUpdateWhenWorkoutVersionIsStale() {
        UUID userId = UUID.randomUUID();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            UserEnt user = new UserEnt(
                    userId,
                    "user_" + userId.toString().substring(0, 8),
                    userId + "@example.com"
            );

            em.persist(user);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }

        Workout workout = new Workout(
                userId,
                "Push Day",
                LocalDateTime.now()
        );

        Workout saved = workouts.save(workout);

        Workout copy1 = workouts.findById(saved.getId()).orElseThrow();
        Workout copy2 = workouts.findById(saved.getId()).orElseThrow();

        assertEquals(copy1.getVersion(), copy2.getVersion());

        copy1.setName("Pull Day");
        Workout updated = workouts.save(copy1);

        assertEquals("Pull Day", updated.getName());
        assertNotEquals(copy2.getVersion(), updated.getVersion());

        copy2.setName("Leg Day");

        assertThrows(
                jakarta.persistence.OptimisticLockException.class,
                () -> workouts.save(copy2)
        );

        Workout current = workouts.findById(saved.getId()).orElseThrow();
        assertEquals("Pull Day", current.getName());

        workouts.deleteById(saved.getId());

        tx.begin();
        em.remove(em.find(UserEnt.class, userId));
        tx.commit();
    }
}
