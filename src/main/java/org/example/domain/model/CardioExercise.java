package org.example.domain.model;

import java.util.UUID;

public class CardioExercise extends Exercise {

    private String activityType;
    private Integer targetHeartRate;

    public CardioExercise(
            UUID id,
            String name,
            String activityType,
            Integer targetHeartRate
    ) {
        super(id, name);
        setActivityType(activityType);
        setTargetHeartRate(targetHeartRate);
    }

    public CardioExercise(
            String name,
            String activityType,
            Integer targetHeartRate
    ) {
        this(UUID.randomUUID(), name, activityType, targetHeartRate);
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        if (activityType == null || activityType.isEmpty()) {
            throw new IllegalArgumentException(
                    "Activity type cannot be empty"
            );
        }

        this.activityType = activityType.trim();
    }

    public Integer getTargetHeartRate() {
        return targetHeartRate;
    }

    public void setTargetHeartRate(Integer targetHeartRate) {
        if (targetHeartRate != null
                && (targetHeartRate < 30 || targetHeartRate > 250)) {
            throw new IllegalArgumentException(
                    "Target heart rate must be between 30 and 250"
            );
        }

        this.targetHeartRate = targetHeartRate;
    }
}
