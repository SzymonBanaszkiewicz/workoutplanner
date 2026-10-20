package org.example.persistence;

import jakarta.persistence.EntityManagerFactory;
import org.example.persistence.config.JpaConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JpaConnectionTest {

    @Test
    void shouldInitializeJpaAndConnectToDatabase() {
        EntityManagerFactory factory =
                JpaConfig.getEntityManagerFactory();

        assertTrue(factory.isOpen());

        JpaConfig.shutdown();
    }
}
