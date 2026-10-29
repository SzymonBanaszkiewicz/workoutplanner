package org.example.persistence.mongo.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.example.persistence.mongo.document.UserDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoUserRepository {

    private final MongoCollection<Document> collection;

    public MongoUserRepository(MongoDatabase database) {
        this.collection = database.getCollection("users");
    }

    public UserDocument save(UserDocument user) {
        Document document = new Document("_id", user.getId().toString())
                .append("username", user.getUsername())
                .append("email", user.getEmail());

        collection.replaceOne(
                eq("_id", user.getId().toString()),
                document,
                new ReplaceOptions().upsert(true)
        );

        return user;
    }

    public Optional<UserDocument> findById(UUID id) {
        Document document = collection.find(
                eq("_id", id.toString())
        ).first();

        return Optional.ofNullable(document).map(this::toUserDocument);
    }

    public Optional<UserDocument> findByEmail(String email) {
        Document document = collection.find(
                eq("email", email)
        ).first();

        return Optional.ofNullable(document).map(this::toUserDocument);
    }

    public List<UserDocument> findAll() {
        List<UserDocument> users = new ArrayList<>();

        for (Document document : collection.find()) {
            users.add(toUserDocument(document));
        }

        return users;
    }

    public void deleteById(UUID id) {
        collection.deleteOne(eq("_id", id.toString()));
    }

    private UserDocument toUserDocument(Document document) {
        UserDocument user = new UserDocument();

        user.setId(UUID.fromString(document.getString("_id")));
        user.setUsername(document.getString("username"));
        user.setEmail(document.getString("email"));

        return user;
    }
}