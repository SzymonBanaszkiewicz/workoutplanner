
package org.example.application.service;

import org.example.domain.model.CardioExercise;
import org.example.domain.model.Exercise;
import org.example.domain.model.StrengthExercise;
import org.example.ports.repository.ExerciseRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    public StrengthExercise createStrengthExercise(
            String name,
            String muscleGroup,
            String equipment
    ) {
        StrengthExercise exercise = new StrengthExercise(
                name,
                muscleGroup,
                equipment
        );

        return (StrengthExercise) exerciseRepository.save(exercise);
    }

    public CardioExercise createCardioExercise(
            String name,
            String activityType,
            Integer targetHeartRate
    ) {
        CardioExercise exercise = new CardioExercise(
                name,
                activityType,
                targetHeartRate
        );

        return (CardioExercise) exerciseRepository.save(exercise);
    }

    public Exercise getExerciseById(UUID id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise not found: " + id
                ));
    }

    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public void deleteExercise(UUID id) {
        getExerciseById(id);
        exerciseRepository.deleteById(id);
    }
}
