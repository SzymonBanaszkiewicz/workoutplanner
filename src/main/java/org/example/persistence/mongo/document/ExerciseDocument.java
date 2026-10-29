package org.example.persistence.mongo.document;

import java.util.UUID;

public class ExerciseDocument {
    private UUID id;
    private String type;
    private String name;
    private String muscleGroup;
    private String equipment;
    private String activityType;
    private Integer targetHeartRate;

    public ExerciseDocument() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMuscleGroup() {
        return muscleGroup;
    }

    public void setMuscleGroup(String muscleGroup) {
        this.muscleGroup = muscleGroup;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public Integer getTargetHeartRate() {
        return targetHeartRate;
    }

    public void setTargetHeartRate(Integer targetHeartRate) {
        this.targetHeartRate = targetHeartRate;
    }
}
