package org.example.persistence.postgres.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.model.User;
import org.example.persistence.postgres.config.JpaConfig;
import org.example.persistence.postgres.entity.UserEnt;
import org.example.ports.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaUserRepositoryAdapter implements UserRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaUserRepositoryAdapter() {
        this.entityManagerFactory = JpaConfig.getEntityManagerFactory();
    }

    public JpaUserRepositoryAdapter(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public User save(User user) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            UUID id = user.getId();

            if (id == null) {
                id = UUID.randomUUID();
            }

            UserEnt entity = em.find(UserEnt.class, id);

            if (entity == null) {
                entity = new UserEnt(
                        id,
                        user.getUsername(),
                        user.getEmail()
                );
                em.persist(entity);
            } else {
                entity.setUsername(user.getUsername());
                entity.setEmail(user.getEmail());
            }

            tx.commit();

            return new User(
                    entity.getId(),
                    entity.getUsername(),
                    entity.getEmail()
            );

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            UserEnt entity = em.find(UserEnt.class, id);
            return Optional.ofNullable(entity).map(this::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            List<UserEnt> results = em.createQuery(
                            "SELECT u FROM UserEnt u WHERE u.email = :email",
                            UserEnt.class
                    )
                    .setParameter("email", email)
                    .setMaxResults(1)
                    .getResultList();

            return results.stream()
                    .findFirst()
                    .map(this::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public List<User> findAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT u FROM UserEnt u",
                            UserEnt.class
                    )
                    .getResultList()
                    .stream()
                    .map(this::toDomain)
                    .toList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(UUID id) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            UserEnt entity = em.find(UserEnt.class, id);

            if (entity != null) {
                em.remove(entity);
            }

            tx.commit();

        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    private User toDomain(UserEnt entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail()
        );
    }
}