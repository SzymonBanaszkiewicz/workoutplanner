package org.example.persistence.postgres.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("CARDIO")
public class CardioExerciseEnt extends ExerciseEnt {

    @Column(length = 80)
    private String activityType;

    private Integer targetHeartRate;

    protected CardioExerciseEnt() {
    }

    public CardioExerciseEnt(
            UUID id,
            String name,
            String activityType,
            Integer targetHeartRate
    ) {
        super(id, name);
        this.activityType = activityType;
        this.targetHeartRate = targetHeartRate;
    }

    public String getActivityType() {
        return activityType;
    }

    public Integer getTargetHeartRate() {
        return targetHeartRate;
    }
}
