package org.example.persistence.mongo;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.domain.model.User;
import org.example.domain.model.Workout;
import org.example.persistence.mongo.adapter.MongoUserRepositoryAdapter;
import org.example.persistence.mongo.adapter.MongoWorkoutRepositoryAdapter;
import org.example.persistence.mongo.config.MongoConfig;
import org.example.persistence.mongo.config.MongoIndexInitializer;
import org.example.persistence.mongo.repository.MongoExerciseRepository;
import org.example.persistence.mongo.repository.MongoUserRepository;
import org.example.persistence.mongo.repository.MongoWorkoutRepository;
import org.example.persistence.mongo.repository.OptimisticLockException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.set;
import static org.junit.jupiter.api.Assertions.*;

class MongoIntegrityIntegrationTest {

    private static MongoConfig config;
    private static MongoDatabase database;
    private static MongoUserRepositoryAdapter userAdapter;
    private static MongoWorkoutRepositoryAdapter workoutAdapter;

    @BeforeAll
    static void setUp() {
        config = new MongoConfig();
        database = config.getDatabase();

        new MongoIndexInitializer(database).initialize();

        userAdapter = new MongoUserRepositoryAdapter(
                new MongoUserRepository(database)
        );

        workoutAdapter = new MongoWorkoutRepositoryAdapter(
                new MongoWorkoutRepository(database),
                new MongoExerciseRepository(database)
        );
    }

    @BeforeEach
    void cleanDatabase() {
        database.getCollection("users").deleteMany(new Document());
        database.getCollection("exercises").deleteMany(new Document());
        database.getCollection("workouts").deleteMany(new Document());
    }

    @AfterAll
    static void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void shouldRejectDuplicateUsername() {
        userAdapter.save(new User(
                UUID.randomUUID(),
                "duplicate_user",
                "first@example.com"
        ));

        assertThrows(MongoWriteException.class, () ->
                userAdapter.save(new User(
                        UUID.randomUUID(),
                        "duplicate_user",
                        "second@example.com"
                ))
        );
    }

    @Test
    void shouldRejectDuplicateEmail() {
        userAdapter.save(new User(
                UUID.randomUUID(),
                "first_user",
                "same@example.com"
        ));

        assertThrows(MongoWriteException.class, () ->
                userAdapter.save(new User(
                        UUID.randomUUID(),
                        "second_user",
                        "same@example.com"
                ))
        );
    }

    @Test
    void shouldNotCreateDuplicateDocumentsWhenUpdatingUser() {
        UUID id = UUID.randomUUID();

        userAdapter.save(new User(
                id,
                "before_update",
                "before@example.com"
        ));

        userAdapter.save(new User(
                id,
                "after_update",
                "after@example.com"
        ));

        assertEquals(1, userAdapter.findAll().size());
        assertEquals(
                "after_update",
                userAdapter.findById(id).orElseThrow().getUsername()
        );
    }

    @Test
    void shouldFailWhenWorkoutReferencesMissingExercise() {
        UUID exerciseId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();

        // Dokument zawiera referencję do ćwiczenia,
        // którego nie ma w kolekcji exercises.
        Document entry = new Document("id", UUID.randomUUID().toString())
                .append("exerciseId", exerciseId.toString())
                .append("sets", 3)
                .append("repetitions", 10)
                .append("weightKg", null)
                .append("durationMinutes", null);

        Document workoutDocument = new Document("_id", workoutId.toString())
                .append("userId", UUID.randomUUID().toString())
                .append("name", "Invalid workout")
                .append("performedAt", LocalDateTime.now().toString())
                .append("version", 0L)
                .append("entries", java.util.List.of(entry));

        database.getCollection("workouts").insertOne(workoutDocument);

        // Adapter nie powinien po cichu ignorować brakującego ćwiczenia.
        assertThrows(RuntimeException.class, () ->
                workoutAdapter.findById(workoutId)
        );
    }

    @Test
    void shouldRejectDocumentsThatViolateSchemaValidation() {
        String collectionName = "schema_validation_test";

        database.getCollection(collectionName).drop();

        try {
            Document validator = new Document("$jsonSchema",
                    new Document("bsonType", "object")
                            .append("required",
                                    java.util.List.of("_id", "name", "version"))
                            .append("properties",
                                    new Document("_id",
                                            new Document("bsonType", "string"))
                                            .append("name",
                                                    new Document("bsonType", "string"))
                                            .append("version",
                                                    new Document("bsonType", "long"))
                            )
            );

            database.runCommand(
                    new Document("create", collectionName)
                            .append("validator", validator)
                            .append("validationLevel", "strict")
                            .append("validationAction", "error")
            );

            MongoCollection<Document> collection =
                    database.getCollection(collectionName);

            assertThrows(MongoWriteException.class, () ->
                    collection.insertOne(
                            new Document("_id", UUID.randomUUID().toString())
                                    .append("name", 123)
                                    .append("version", 0L)
                    )
            );
        } finally {
            database.getCollection(collectionName).drop();
        }
    }

    @Test
    void shouldUpdateWorkoutDocumentAtomically() {
        MongoCollection<Document> collection =
                database.getCollection("atomicity_test");

        collection.drop();

        try {
            String id = UUID.randomUUID().toString();

            collection.insertOne(new Document("_id", id)
                    .append("name", "Before")
                    .append("version", 0L)
                    .append("entries", java.util.List.of(
                            new Document("id", UUID.randomUUID().toString())
                                    .append("sets", 3)
                    ))
            );

            Document updated = collection.findOneAndUpdate(
                    eq("_id", id),
                    combine(
                            set("name", "After"),
                            inc("version", 1L),
                            set("entries", java.util.List.of(
                                    new Document("id", UUID.randomUUID().toString())
                                            .append("sets", 5)
                            ))
                    ),
                    new FindOneAndUpdateOptions()
                            .returnDocument(ReturnDocument.AFTER)
            );

            assertNotNull(updated);
            assertEquals("After", updated.getString("name"));
            assertEquals(1L, updated.getLong("version"));
            assertEquals(
                    5,
                    ((Document) ((java.util.List<?>) updated.get("entries"))
                            .get(0)).getInteger("sets")
            );
        } finally {
            collection.drop();
        }
    }

    @Test
    void shouldRejectUpdateBasedOnStaleVersion() {
        MongoCollection<Document> collection =
                database.getCollection("conditional_update_test");

        collection.drop();

        try {
            String id = UUID.randomUUID().toString();

            collection.insertOne(new Document("_id", id)
                    .append("name", "Original")
                    .append("version", 2L)
            );

            // Aktualizacja jest dopuszczona tylko wtedy,
            // gdy wersja dokumentu nadal wynosi 2.
            var result = collection.updateOne(
                    and(
                            eq("_id", id),
                            eq("version", 2L)
                    ),
                    combine(
                            set("name", "Updated"),
                            inc("version", 1L)
                    )
            );

            assertEquals(1, result.getMatchedCount());
            assertEquals(1, result.getModifiedCount());

            // Ta sama aktualizacja ze starą wersją nie może przejść.
            var staleResult = collection.updateOne(
                    and(
                            eq("_id", id),
                            eq("version", 2L)
                    ),
                    combine(
                            set("name", "Stale update"),
                            inc("version", 1L)
                    )
            );

            assertEquals(0, staleResult.getMatchedCount());
            assertEquals(0, staleResult.getModifiedCount());

            Document stored = collection.find(eq("_id", id)).first();

            assertNotNull(stored);
            assertEquals("Updated", stored.getString("name"));
            assertEquals(3L, stored.getLong("version"));
        } finally {
            collection.drop();
        }
    }

    @Test
    void shouldRejectStaleWorkoutUpdate() {
        UUID userId = UUID.randomUUID();
        UUID workoutId = UUID.randomUUID();

        Workout original = new Workout(
                workoutId,
                userId,
                "Original workout",
                LocalDateTime.of(2026, 10, 20, 10, 0)
        );

        workoutAdapter.save(original);

        Workout firstCopy = workoutAdapter.findById(workoutId)
                .orElseThrow();

        Workout staleCopy = workoutAdapter.findById(workoutId)
                .orElseThrow();

        firstCopy.setName("Updated workout");
        workoutAdapter.save(firstCopy);

        staleCopy.setName("Stale update");

        assertThrows(
                OptimisticLockException.class,
                () -> workoutAdapter.save(staleCopy)
        );

        Workout stored = workoutAdapter.findById(workoutId)
                .orElseThrow();

        assertEquals("Updated workout", stored.getName());
        assertEquals(1L, stored.getVersion());
    }
}