package org.example.persistence.mongo.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MongoConfig implements AutoCloseable {

    private final MongoClient mongoClient;
    private final MongoDatabase database;

    public MongoConfig() {
        Properties properties = loadProperties();

        String connectionString = properties.getProperty("mongodb.connection-string");
        String databaseName = properties.getProperty("mongodb.database");

        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException(
                    "Missing property: mongodb.connection-string"
            );
        }

        if (databaseName == null || databaseName.isBlank()) {
            throw new IllegalStateException(
                    "Missing property: mongodb.database"
            );
        }

        this.mongoClient = MongoClients.create(connectionString);
        this.database = mongoClient.getDatabase(databaseName);
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    private Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "application.properties not found"
                );
            }

            properties.load(input);
            return properties;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load application.properties",
                    e
            );
        }
    }
}