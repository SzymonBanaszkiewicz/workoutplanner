package org.example.persistence.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.example.persistence.mongo.document.WorkoutDocument;
import org.example.persistence.mongo.document.WorkoutEntryDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoWorkoutRepository {

    private final MongoCollection<Document> collection;

    public MongoWorkoutRepository(MongoDatabase database) {
        this.collection = database.getCollection("workouts");
    }

    public WorkoutDocument save(WorkoutDocument workout) {
        if (workout.getVersion() == null) {
            workout.setVersion(0L);
        }

        Document document = toBson(workout);

        Document existing = collection
                .find(eq("_id", workout.getId().toString()))
                .first();

        if (existing == null) {
            try {
                collection.insertOne(document);
                return workout;
            } catch (com.mongodb.MongoWriteException exception) {
                // Inny wątek mógł wstawić dokument o tym samym ID.
                if (exception.getError().getCode() == 11000) {
                    throw new OptimisticLockException(
                            "Workout already exists: " + workout.getId()
                    );
                }
                throw exception;
            }
        }

        long expectedVersion = workout.getVersion();
        long nextVersion = expectedVersion + 1;

        Document replacement = toBson(workout);
        replacement.put("version", nextVersion);

        UpdateResult result = collection.replaceOne(
                Filters.and(
                        eq("_id", workout.getId().toString()),
                        eq("version", expectedVersion)
                ),
                replacement
        );

        if (result.getMatchedCount() != 1) {
            throw new OptimisticLockException(
                    "Workout was modified by another operation: "
                            + workout.getId()
            );
        }

        workout.setVersion(nextVersion);

        return workout;
    }


    public Optional<WorkoutDocument> findById(UUID id) {
        Document document = collection
                .find(eq("_id", id.toString()))
                .first();

        return Optional.ofNullable(document)
                .map(this::toWorkoutDocument);
    }

    public List<WorkoutDocument> findAll() {
        List<WorkoutDocument> workouts = new ArrayList<>();

        for (Document document : collection.find()) {
            workouts.add(toWorkoutDocument(document));
        }

        return workouts;
    }

    public List<WorkoutDocument> findByUserId(UUID userId) {
        List<WorkoutDocument> workouts = new ArrayList<>();

        for (Document document :
                collection.find(eq("userId", userId.toString()))) {
            workouts.add(toWorkoutDocument(document));
        }

        return workouts;
    }

    public void deleteById(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }

    private Document toBson(WorkoutDocument workout) {
        List<Document> entries = new ArrayList<>();

        if (workout.getEntries() != null) {
            for (WorkoutEntryDocument entry : workout.getEntries()) {
                Document entryDocument = new Document()
                        .append("id", entry.getId().toString())
                        .append("exerciseId", entry.getExerciseId().toString())
                        .append("sets", entry.getSets())
                        .append("repetitions", entry.getRepetitions())
                        .append("durationMinutes", entry.getDurationMinutes());

                BigDecimal weight = entry.getWeightKg();

                entryDocument.append(
                        "weightKg",
                        weight == null ? null : new Decimal128(weight)
                );

                entries.add(entryDocument);
            }
        }

        return new Document("_id", workout.getId().toString())
                .append("version", workout.getVersion())
                .append("userId", workout.getUserId().toString())
                .append("name", workout.getName())
                .append(
                        "performedAt",
                        workout.getPerformedAt() == null
                                ? null
                                : workout.getPerformedAt().toString()
                )
                .append("entries", entries);
    }

    private WorkoutDocument toWorkoutDocument(Document document) {
        WorkoutDocument workout = new WorkoutDocument();

        workout.setId(UUID.fromString(document.getString("_id")));
        workout.setUserId(UUID.fromString(document.getString("userId")));
        workout.setName(document.getString("name"));

        Object version = document.get("version");
        workout.setVersion(
                version == null ? null : ((Number) version).longValue()
        );

        String performedAt = document.getString("performedAt");
        workout.setPerformedAt(
                performedAt == null
                        ? null
                        : LocalDateTime.parse(performedAt)
        );

        List<WorkoutEntryDocument> entries = new ArrayList<>();

        List<Document> storedEntries = document.getList(
                "entries",
                Document.class
        );

        if (storedEntries != null) {
            for (Document entryDocument : storedEntries) {
                WorkoutEntryDocument entry = new WorkoutEntryDocument();

                entry.setId(UUID.fromString(entryDocument.getString("id")));

                entry.setExerciseId(
                        UUID.fromString(entryDocument.getString("exerciseId"))
                );

                entry.setSets(entryDocument.getInteger("sets"));
                entry.setRepetitions(entryDocument.getInteger("repetitions"));
                entry.setDurationMinutes(
                        entryDocument.getInteger("durationMinutes")
                );

                Object storedWeight = entryDocument.get("weightKg");

                if (storedWeight instanceof Decimal128 decimal128) {
                    entry.setWeightKg(decimal128.bigDecimalValue());
                } else if (storedWeight instanceof Number number) {
                    // Obsługa ewentualnych starszych dokumentów.
                    entry.setWeightKg(new BigDecimal(number.toString()));
                } else {
                    entry.setWeightKg(null);
                }

                entries.add(entry);
            }
        }

        workout.setEntries(entries);

        return workout;
    }
}
