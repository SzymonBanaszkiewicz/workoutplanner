package org.example.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("STRENGTH")
public class StrengthExerciseEnt extends ExerciseEnt {

    @Column(length = 80)
    private String muscleGroup;

    @Column(length = 80)
    private String equipment;

    protected StrengthExerciseEnt() {
    }

    public StrengthExerciseEnt(
            UUID id,
            String name,
            String muscleGroup,
            String equipment
    ) {
        super(id, name);
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }
}