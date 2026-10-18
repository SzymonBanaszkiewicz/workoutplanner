package org.example.ports.repository;

import org.example.domain.model.Workout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

    Workout save(Workout workout);

    Optional<Workout> findById(UUID id);

    List<Workout> findAll();

    List<Workout> findByUserId(UUID userId);

    void deleteById(UUID id);
}