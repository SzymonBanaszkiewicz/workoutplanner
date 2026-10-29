package org.example.persistence.mongo.mapper;

import org.example.domain.model.Workout;
import org.example.domain.model.WorkoutEntry;
import org.example.domain.model.Exercise;
import org.example.persistence.mongo.document.WorkoutDocument;
import org.example.persistence.mongo.document.WorkoutEntryDocument;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WorkoutMapper {

    private WorkoutMapper() {}

    public static WorkoutDocument toDocument(Workout workout) {
        Objects.requireNonNull(workout, "Workout cannot be null");

        WorkoutDocument document = new WorkoutDocument();
        document.setId(workout.getId());
        document.setVersion(workout.getVersion());
        document.setUserId(workout.getUserId());
        document.setName(workout.getName());
        document.setPerformedAt(workout.getPerformedAt());

        List<WorkoutEntryDocument> entries = workout.getEntries()
                .stream()
                .map(entry -> {
                    WorkoutEntryDocument entryDocument = new WorkoutEntryDocument();
                    entryDocument.setId(entry.getId());
                    entryDocument.setExerciseId(entry.getExercise().getId());
                    entryDocument.setSets(entry.getSets());
                    entryDocument.setRepetitions(entry.getRepetitions());
                    entryDocument.setWeightKg(entry.getWeightKg());
                    entryDocument.setDurationMinutes(entry.getDurationMinutes());
                    return entryDocument;
                })
                .collect(Collectors.toList());

        document.setEntries(entries);

        return document;
    }

    public static Workout toDomain(
            WorkoutDocument document,
            Function<UUID, Exercise> exerciseFinder
    ) {
        Objects.requireNonNull(document, "WorkoutDocument cannot be null");
        Objects.requireNonNull(exerciseFinder, "Exercise finder cannot be null");

        Workout workout = new Workout(
                document.getId(),
                document.getUserId(),
                document.getName(),
                document.getPerformedAt()
        );

        workout.setVersion(document.getVersion());

        if (document.getEntries() != null) {
            for (WorkoutEntryDocument entryDocument : document.getEntries()) {
                Exercise exercise = Objects.requireNonNull(
                        exerciseFinder.apply(entryDocument.getExerciseId()),
                        "Exercise not found: " + entryDocument.getExerciseId()
                );

                WorkoutEntry entry = new WorkoutEntry(
                        entryDocument.getId(),
                        exercise,
                        entryDocument.getSets(),
                        entryDocument.getRepetitions(),
                        entryDocument.getWeightKg(),
                        entryDocument.getDurationMinutes()
                );

                workout.addEntry(entry);
            }
        }

        return workout;
    }
}