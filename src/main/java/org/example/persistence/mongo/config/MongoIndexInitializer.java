package org.example.persistence.mongo.config;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import org.bson.Document;

import java.util.List;

import static com.mongodb.client.model.Indexes.ascending;

public class MongoIndexInitializer {

    private final MongoDatabase database;

    public MongoIndexInitializer(MongoDatabase database) {
        this.database = database;
    }

    public void initialize() {
        createUserIndexes();
        createWorkoutIndexes();

        initializeCollection("users", userSchema());
        initializeCollection("exercises", exerciseSchema());
        initializeCollection("workouts", workoutSchema());
    }

    private void createUserIndexes() {
        var users = database.getCollection("users");

        users.createIndex(
                ascending("username"),
                new IndexOptions()
                        .unique(true)
                        .name("unique_username")
        );

        users.createIndex(
                ascending("email"),
                new IndexOptions()
                        .unique(true)
                        .name("unique_email")
        );
    }

    private void createWorkoutIndexes() {
        var workouts = database.getCollection("workouts");

        workouts.createIndex(
                ascending("userId"),
                new IndexOptions().name("workouts_by_user")
        );

        workouts.createIndex(
                ascending("entries.exerciseId"),
                new IndexOptions().name("workouts_by_exercise")
        );
    }

    private void initializeCollection(
            String collectionName,
            Document schema
    ) {
        // Utwórz kolekcję, jeśli jeszcze nie istnieje.
        boolean exists = database.listCollectionNames()
                .into(new java.util.ArrayList<>())
                .contains(collectionName);

        if (!exists) {
            database.createCollection(collectionName);
        }

        // Skonfiguruj walidator również dla już istniejącej kolekcji.
        database.runCommand(
                new Document("collMod", collectionName)
                        .append("validator", schema)
                        .append("validationLevel", "strict")
                        .append("validationAction", "error")
        );
    }

    private Document userSchema() {
        return new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required",
                                List.of("_id", "username", "email"))
                        .append("properties",
                                new Document("_id",
                                        new Document("bsonType", "string"))
                                        .append("username",
                                                new Document("bsonType", "string")
                                                        .append("minLength", 1))
                                        .append("email",
                                                new Document("bsonType", "string")
                                                        .append("minLength", 1))
                        )
        );
    }

    private Document exerciseSchema() {
        return new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required",
                                List.of("_id", "type", "name"))
                        .append("properties",
                                new Document("_id",
                                        new Document("bsonType", "string"))
                                        .append("type",
                                                new Document("bsonType", "string")
                                                        .append("enum",
                                                                List.of("STRENGTH", "CARDIO")))
                                        .append("name",
                                                new Document("bsonType", "string")
                                                        .append("minLength", 1))
                                        .append("muscleGroup",
                                                new Document("bsonType",
                                                        List.of("string", "null")))
                                        .append("equipment",
                                                new Document("bsonType",
                                                        List.of("string", "null")))
                                        .append("activityType",
                                                new Document("bsonType",
                                                        List.of("string", "null")))
                                        .append("targetHeartRate",
                                                new Document("bsonType",
                                                        List.of("int", "long", "null")))
                        )
        );
    }

    private Document workoutSchema() {
        Document entrySchema = new Document("bsonType", "object")
                .append("required", List.of("id", "exerciseId"))
                .append("properties",
                        new Document("id",
                                new Document("bsonType", "string"))
                                .append("exerciseId",
                                        new Document("bsonType", "string"))
                                .append("sets",
                                        new Document("bsonType",
                                                List.of("int", "null")))
                                .append("repetitions",
                                        new Document("bsonType",
                                                List.of("int", "null")))
                                .append("durationMinutes",
                                        new Document("bsonType",
                                                List.of("int", "null")))
                                .append("weightKg",
                                        new Document("bsonType",
                                                List.of("decimal", "null")))
                );

        return new Document("$jsonSchema",
                new Document("bsonType", "object")
                        .append("required",
                                List.of(
                                        "_id",
                                        "userId",
                                        "name",
                                        "version",
                                        "entries"
                                ))
                        .append("properties",
                                new Document("_id",
                                        new Document("bsonType", "string"))
                                        .append("userId",
                                                new Document("bsonType", "string"))
                                        .append("name",
                                                new Document("bsonType", "string")
                                                        .append("minLength", 1))
                                        .append("version",
                                                new Document("bsonType",
                                                        List.of("int", "long")))
                                        .append("performedAt",
                                                new Document("bsonType",
                                                        List.of("string", "null")))
                                        .append("entries",
                                                new Document("bsonType", "array")
                                                        .append("items", entrySchema))
                        )
        );
    }
}