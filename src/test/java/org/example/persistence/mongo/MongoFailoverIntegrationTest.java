package org.example.persistence.mongo;

import org.example.domain.model.User;
import org.example.persistence.mongo.adapter.MongoUserRepositoryAdapter;
import org.example.persistence.mongo.config.MongoConfig;
import org.example.persistence.mongo.repository.MongoUserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MongoFailoverIntegrationTest {

    private static MongoConfig config;
    private static MongoUserRepositoryAdapter userAdapter;

    @BeforeAll
    static void setUp() {
        config = new MongoConfig();

        userAdapter = new MongoUserRepositoryAdapter(
                new MongoUserRepository(config.getDatabase())
        );
    }

    @AfterAll
    static void tearDown() {
        if (config != null) {
            config.close();
        }
    }

    @Test
    void shouldReadAndWriteDataWhenOneReplicaNodeIsDown() {
        UUID id = UUID.randomUUID();

        User user = new User(
                id,
                "failover_" + id.toString().substring(0, 8),
                "failover_" + id + "@example.com"
        );

        userAdapter.save(user);

        User result = userAdapter.findById(id).orElseThrow();

        assertEquals(id, result.getId());
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
    }
}