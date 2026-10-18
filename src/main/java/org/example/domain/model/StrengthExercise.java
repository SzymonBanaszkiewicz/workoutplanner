package org.example.domain.model;

import java.util.UUID;

public class StrengthExercise extends Exercise {

    private String muscleGroup;
    private String equipment;

    public StrengthExercise(
            UUID id,
            String name,
            String muscleGroup,
            String equipment
    ) {
        super(id, name);
        setMuscleGroup(muscleGroup);
        setEquipment(equipment);
    }

    public StrengthExercise(
            String name,
            String muscleGroup,
            String equipment
    ) {
        this(UUID.randomUUID(), name, muscleGroup, equipment);
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        if (muscleGroup == null || muscleGroup.isEmpty()) {
            throw new IllegalArgumentException(
                    "Muscle group cannot be empty"
            );
        }

        this.muscleGroup = muscleGroup.trim();
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        if (equipment == null || equipment.isEmpty()) {
            throw new IllegalArgumentException(
                    "Equipment cannot be empty"
            );
        }

        this.equipment = equipment.trim();
    }
}