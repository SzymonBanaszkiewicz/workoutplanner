package org.example.persistence.mapper;

import org.example.domain.model.CardioExercise;
import org.example.domain.model.Exercise;
import org.example.domain.model.StrengthExercise;
import org.example.persistence.entity.CardioExerciseEnt;
import org.example.persistence.entity.ExerciseEnt;
import org.example.persistence.entity.StrengthExerciseEnt;
import org.hibernate.Hibernate;

public final class ExerciseMapper {

    private ExerciseMapper() {
    }

    public static ExerciseEnt toEntity(Exercise exercise) {
        if (exercise instanceof StrengthExercise strength) {
            return new StrengthExerciseEnt(
                    strength.getId(),
                    strength.getName(),
                    strength.getMuscleGroup(),
                    strength.getEquipment()
            );
        }

        if (exercise instanceof CardioExercise cardio) {
            return new CardioExerciseEnt(
                    cardio.getId(),
                    cardio.getName(),
                    cardio.getActivityType(),
                    cardio.getTargetHeartRate()
            );
        }

        throw new IllegalArgumentException(
                "Unsupported exercise type: " + exercise.getClass().getName()
        );
    }


    public static Exercise toDomain(ExerciseEnt entity) {
        if (entity == null) {
            return null;
        }

        ExerciseEnt actualEntity =
                (ExerciseEnt) Hibernate.unproxy(entity);

        if (actualEntity instanceof StrengthExerciseEnt strength) {
            return new StrengthExercise(
                    strength.getId(),
                    strength.getName(),
                    strength.getMuscleGroup(),
                    strength.getEquipment()
            );
        }

        if (actualEntity instanceof CardioExerciseEnt cardio) {
            return new CardioExercise(
                    cardio.getId(),
                    cardio.getName(),
                    cardio.getActivityType(),
                    cardio.getTargetHeartRate()
            );
        }

        throw new IllegalArgumentException(
                "Unsupported exercise entity type: "
                        + actualEntity.getClass().getName()
        );
    }

}