package org.example.persistence.mongo.adapter;

import org.example.domain.model.Exercise;
import org.example.domain.model.Workout;
import org.example.persistence.mongo.document.ExerciseDocument;
import org.example.persistence.mongo.document.WorkoutDocument;
import org.example.persistence.mongo.mapper.ExerciseMapper;
import org.example.persistence.mongo.mapper.WorkoutMapper;
import org.example.persistence.mongo.repository.MongoExerciseRepository;
import org.example.persistence.mongo.repository.MongoWorkoutRepository;
import org.example.ports.repository.WorkoutRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MongoWorkoutRepositoryAdapter implements WorkoutRepository {

    private final MongoWorkoutRepository workoutRepository;
    private final MongoExerciseRepository exerciseRepository;

    public MongoWorkoutRepositoryAdapter(
            MongoWorkoutRepository workoutRepository,
            MongoExerciseRepository exerciseRepository
    ) {
        this.workoutRepository = workoutRepository;
        this.exerciseRepository = exerciseRepository;
    }

    @Override
    public Workout save(Workout workout) {
        WorkoutDocument document = WorkoutMapper.toDocument(workout);

        WorkoutDocument savedDocument = workoutRepository.save(document);

        return toDomain(savedDocument);
    }

    @Override
    public Optional<Workout> findById(UUID id) {
        return workoutRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<Workout> findAll() {
        return workoutRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Workout> findByUserId(UUID userId) {
        return workoutRepository.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        workoutRepository.deleteById(id);
    }

    private Workout toDomain(WorkoutDocument document) {
        return WorkoutMapper.toDomain(
                document,
                exerciseId -> {
                    Optional<ExerciseDocument> exerciseDocument =
                            exerciseRepository.findById(exerciseId);

                    Exercise exercise = exerciseDocument
                            .map(ExerciseMapper::toDomain)
                            .orElse(null);

                    return exercise;
                }
        );
    }
}
