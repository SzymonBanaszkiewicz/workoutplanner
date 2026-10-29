package org.example.persistence.mongo.mapper;

import org.example.domain.model.User;
import org.example.persistence.mongo.document.UserDocument;

import java.util.Objects;

public class UserMapper {

    private UserMapper() {}

    public static UserDocument toDocument(User user) {
        Objects.requireNonNull(user, "User cannot be null");

        UserDocument document = new UserDocument();
        document.setId(user.getId());
        document.setUsername(user.getUsername());
        document.setEmail(user.getEmail());

        return document;
    }

    public static User toDomain(UserDocument document) {
        Objects.requireNonNull(document, "UserDocument cannot be null");

        return new User(
                document.getId(),
                document.getUsername(),
                document.getEmail()
        );
    }
}