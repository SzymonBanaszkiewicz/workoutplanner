
package org.example.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.model.Workout;
import org.example.domain.model.WorkoutEntry;
import org.example.persistence.config.JpaConfig;
import org.example.persistence.entity.ExerciseEnt;
import org.example.persistence.entity.UserEnt;
import org.example.persistence.entity.WorkoutEnt;
import org.example.persistence.entity.WorkoutEntryEnt;
import org.example.persistence.mapper.ExerciseMapper;
import org.example.ports.repository.WorkoutRepository;

import java.util.*;

public class JpaWorkoutRepositoryAdapter implements WorkoutRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaWorkoutRepositoryAdapter() {
        this(JpaConfig.getEntityManagerFactory());
    }

    public JpaWorkoutRepositoryAdapter(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    @Override
    public Workout save(Workout workout) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            WorkoutEnt entity = em.find(WorkoutEnt.class, workout.getId());

            if (entity == null) {
                entity = new WorkoutEnt(
                        workout.getId(),
                        workout.getName(),
                        workout.getPerformedAt()
                );

                UserEnt user = em.getReference(
                        UserEnt.class, workout.getUserId()
                );
                entity.setUser(user);
                em.persist(entity);
            } else {
            if (workout.getVersion() == null
                    || !workout.getVersion().equals(entity.getVersion())) {
                throw new jakarta.persistence.OptimisticLockException(
                        "Workout was modified by another operation. "
                                + "Reload it before saving."
                );
            }

            entity.setName(workout.getName());
            entity.setPerformedAt(workout.getPerformedAt());
        }


            Map<UUID, WorkoutEntryEnt> existingEntries = new HashMap<>();

            for (WorkoutEntryEnt existing : entity.getEntries()) {
                existingEntries.put(existing.getId(), existing);
            }

            Set<UUID> incomingIds = new HashSet<>();

            for (WorkoutEntry entry : workout.getEntries()) {
                incomingIds.add(entry.getId());

                ExerciseEnt exercise = em.getReference(
                        ExerciseEnt.class,
                        entry.getExercise().getId()
                );

                WorkoutEntryEnt existing = existingEntries.get(entry.getId());

                if (existing != null) {
                    existing.setSets(entry.getSets());
                    existing.setRepetitions(entry.getRepetitions());
                    existing.setWeightKg(entry.getWeightKg());
                    existing.setDurationMinutes(entry.getDurationMinutes());
                    existing.setExercise(exercise);
                } else {
                    WorkoutEntryEnt newEntry = new WorkoutEntryEnt(
                            entry.getId(),
                            entry.getSets(),
                            entry.getRepetitions(),
                            entry.getWeightKg(),
                            entry.getDurationMinutes(),
                            exercise
                    );

                    entity.addEntry(newEntry);
                }
            }

            for (WorkoutEntryEnt existing :
                    new java.util.ArrayList<>(entity.getEntries())) {
                if (!incomingIds.contains(existing.getId())) {
                    entity.removeEntry(existing);
                }
            }


            tx.commit();


            return findById(workout.getId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Saved workout could not be found"
                            ));

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
    public Optional<Workout> findById(UUID id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            WorkoutEnt entity = em.createQuery(
                            "SELECT DISTINCT w FROM WorkoutEnt w " +
                                    "LEFT JOIN FETCH w.entries " +
                                    "WHERE w.id = :id",
                            WorkoutEnt.class
                    )
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            return Optional.ofNullable(entity)
                    .map(this::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Workout> findAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT DISTINCT w FROM WorkoutEnt w " +
                                    "LEFT JOIN FETCH w.entries",
                            WorkoutEnt.class
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
    public List<Workout> findByUserId(UUID userId) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT DISTINCT w FROM WorkoutEnt w " +
                                    "LEFT JOIN FETCH w.entries " +
                                    "WHERE w.user.id = :userId",
                            WorkoutEnt.class
                    )
                    .setParameter("userId", userId)
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

            WorkoutEnt entity = em.find(WorkoutEnt.class, id);

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

    private Workout toDomain(WorkoutEnt entity) {
        Workout workout = new Workout(
                entity.getId(),
                entity.getUser().getId(),
                entity.getName(),
                entity.getPerformedAt()
        );

        for (WorkoutEntryEnt entry : entity.getEntries()) {
            WorkoutEntry domainEntry = new WorkoutEntry(
                    entry.getId(),
                    ExerciseMapper.toDomain(entry.getExercise()),
                    entry.getSets(),
                    entry.getRepetitions(),
                    entry.getWeightKg(),
                    entry.getDurationMinutes()
            );

            workout.addEntry(domainEntry);
        }

        workout.setVersion(entity.getVersion());
        return workout;
    }
}
