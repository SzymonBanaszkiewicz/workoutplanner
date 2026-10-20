package org.example.persistence.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "exercises")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "exercise_type",
        discriminatorType = DiscriminatorType.STRING
)
public abstract class ExerciseEnt {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    protected ExerciseEnt() {
    }

    protected ExerciseEnt(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}