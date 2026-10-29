package org.example.persistence.mongo.adapter;

import org.example.domain.model.Exercise;
import org.example.persistence.mongo.document.ExerciseDocument;
import org.example.persistence.mongo.mapper.ExerciseMapper;
import org.example.persistence.mongo.repository.MongoExerciseRepository;
import org.example.ports.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MongoExerciseRepositoryAdapter implements ExerciseRepository {

    private final MongoExerciseRepository mongoRepository;

    public MongoExerciseRepositoryAdapter(MongoExerciseRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public Exercise save(Exercise exercise) {
        ExerciseDocument document = ExerciseMapper.toDocument(exercise);
        ExerciseDocument savedDocument = mongoRepository.save(document);
        return ExerciseMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Exercise> findById(UUID id) {
        return mongoRepository.findById(id)
                .map(ExerciseMapper::toDomain);
    }

    @Override
    public List<Exercise> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(ExerciseMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        mongoRepository.deleteById(id);
    }
}