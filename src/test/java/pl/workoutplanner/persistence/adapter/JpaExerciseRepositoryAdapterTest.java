
package pl.workoutplanner.persistence.adapter;

import org.example.persistence.adapter.JpaExerciseRepositoryAdapter;
import org.example.persistence.config.JpaConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JpaExerciseRepositoryAdapterTest {

    private final JpaExerciseRepositoryAdapter repository =
            new JpaExerciseRepositoryAdapter();

    @AfterAll
    static void shutdown() {
        JpaConfig.shutdown();
    }

    @Test
    void shouldSaveAndFindExerciseById() {

    }
}
