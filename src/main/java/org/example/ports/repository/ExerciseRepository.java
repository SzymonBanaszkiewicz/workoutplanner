package org.example.ports.repository;

import org.example.domain.model.Exercise;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExerciseRepository {

    Exercise save(Exercise exercise);

    Optional<Exercise> findById(UUID id);

    List<Exercise> findAll();

    void deleteById(UUID id);
}