package org.example.persistence.mongo.mapper;

import org.example.domain.model.Exercise;
import org.example.domain.model.StrengthExercise;
import org.example.domain.model.CardioExercise;
import org.example.persistence.mongo.document.ExerciseDocument;

import java.util.Objects;

public class ExerciseMapper {

    private ExerciseMapper() {}

    public static ExerciseDocument toDocument(Exercise exercise) {
        Objects.requireNonNull(exercise, "Exercise cannot be null");

        ExerciseDocument document = new ExerciseDocument();

        document.setId(exercise.getId());
        document.setName(exercise.getName());

        if (exercise instanceof StrengthExercise strength) {
            document.setType("STRENGTH");
            document.setMuscleGroup(strength.getMuscleGroup());
            document.setEquipment(strength.getEquipment());

        } else if (exercise instanceof CardioExercise cardio) {
            document.setType("CARDIO");
            document.setActivityType(cardio.getActivityType());
            document.setTargetHeartRate(cardio.getTargetHeartRate());

        } else {
            throw new IllegalArgumentException(
                    "Unsupported exercise type: " + exercise.getClass().getName()
            );
        }

        return document;
    }

    public static Exercise toDomain(ExerciseDocument document) {
        Objects.requireNonNull(document, "ExerciseDocument cannot be null");

        return switch (document.getType()) {
            case "STRENGTH" -> new StrengthExercise(
                    document.getId(),
                    document.getName(),
                    document.getMuscleGroup(),
                    document.getEquipment()
            );

            case "CARDIO" -> new CardioExercise(
                    document.getId(),
                    document.getName(),
                    document.getActivityType(),
                    document.getTargetHeartRate()
            );

            default -> throw new IllegalArgumentException(
                    "Unknown exercise type: " + document.getType()
            );
        };
    }
}