
package org.example.persistence.adapter;

import org.example.domain.model.Exercise;
import org.example.domain.model.StrengthExercise;
import org.example.persistence.config.JpaConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class JpaExerciseRepositoryAdapterTest {

    private final JpaExerciseRepositoryAdapter repository =
            new JpaExerciseRepositoryAdapter();

    @AfterAll
    static void shutdown() {
        JpaConfig.shutdown();
    }

    @Test
    void shouldSaveAndFindExerciseById() {
        Exercise exercise = new StrengthExercise(
                UUID.randomUUID(),
                "Bench Press",
                "Chest",
                "Barbell"
        );

        Exercise saved = repository.save(exercise);
        Exercise found = repository.findById(saved.getId()).orElseThrow();

        assertEquals(exercise.getId(), saved.getId());
        assertEquals("Bench Press", found.getName());
        assertInstanceOf(StrengthExercise.class, found);

        StrengthExercise strength = (StrengthExercise) found;
        assertEquals("Chest", strength.getMuscleGroup());
        assertEquals("Barbell", strength.getEquipment());

        repository.deleteById(saved.getId());
    }
}
