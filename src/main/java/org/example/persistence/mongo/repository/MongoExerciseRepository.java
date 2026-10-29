package org.example.persistence.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.example.persistence.mongo.document.ExerciseDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoExerciseRepository {

    private final MongoCollection<Document> collection;

    public MongoExerciseRepository(MongoDatabase database) {
        this.collection = database.getCollection("exercises");
    }

    public ExerciseDocument save(ExerciseDocument exercise) {
        Document document = toBson(exercise);

        collection.replaceOne(
                eq("_id", exercise.getId().toString()),
                document,
                new ReplaceOptions().upsert(true)
        );

        return exercise;
    }

    public Optional<ExerciseDocument> findById(UUID id) {
        Document document = collection.find(
                eq("_id", id.toString())
        ).first();

        return Optional.ofNullable(document).map(this::toExerciseDocument);
    }

    public List<ExerciseDocument> findAll() {
        List<ExerciseDocument> exercises = new ArrayList<>();

        for (Document document : collection.find()) {
            exercises.add(toExerciseDocument(document));
        }

        return exercises;
    }

    public void deleteById(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }

    private Document toBson(ExerciseDocument exercise) {
        Document document = new Document("_id", exercise.getId().toString())
                .append("type", exercise.getType())
                .append("name", exercise.getName());

        if ("STRENGTH".equals(exercise.getType())) {
            document.append("muscleGroup", exercise.getMuscleGroup())
                    .append("equipment", exercise.getEquipment());
        } else if ("CARDIO".equals(exercise.getType())) {
            document.append("activityType", exercise.getActivityType())
                    .append("targetHeartRate", exercise.getTargetHeartRate());
        } else {
            throw new IllegalArgumentException(
                    "Unknown exercise type: " + exercise.getType()
            );
        }

        return document;
    }

    private ExerciseDocument toExerciseDocument(Document document) {
        ExerciseDocument exercise = new ExerciseDocument();

        exercise.setId(UUID.fromString(document.getString("_id")));
        exercise.setType(document.getString("type"));
        exercise.setName(document.getString("name"));
        exercise.setMuscleGroup(document.getString("muscleGroup"));
        exercise.setEquipment(document.getString("equipment"));
        exercise.setActivityType(document.getString("activityType"));
        exercise.setTargetHeartRate(document.getInteger("targetHeartRate"));

        return exercise;
    }
}