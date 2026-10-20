
package org.example.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.model.Exercise;
import org.example.persistence.config.JpaConfig;
import org.example.persistence.entity.ExerciseEnt;
import org.example.persistence.mapper.ExerciseMapper;
import org.example.ports.repository.ExerciseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaExerciseRepositoryAdapter implements ExerciseRepository {

    private final EntityManagerFactory entityManagerFactory;

    public JpaExerciseRepositoryAdapter() {
        this(JpaConfig.getEntityManagerFactory());
    }

    public JpaExerciseRepositoryAdapter(
            EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Exercise save(Exercise exercise) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            ExerciseEnt entity = ExerciseMapper.toEntity(exercise);
            ExerciseEnt savedEntity = em.merge(entity);

            tx.commit();

            return ExerciseMapper.toDomain(savedEntity);

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
    public Optional<Exercise> findById(UUID id) {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            ExerciseEnt entity = em.find(ExerciseEnt.class, id);

            return Optional.ofNullable(entity)
                    .map(ExerciseMapper::toDomain);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Exercise> findAll() {
        EntityManager em = entityManagerFactory.createEntityManager();

        try {
            return em.createQuery(
                            "SELECT e FROM ExerciseEnt e",
                            ExerciseEnt.class
                    )
                    .getResultList()
                    .stream()
                    .map(ExerciseMapper::toDomain)
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

            ExerciseEnt entity = em.find(ExerciseEnt.class, id);

            if (entity == null) {
                tx.commit();
                return;
            }

            Long usageCount = em.createQuery(
                            "SELECT COUNT(entry) FROM WorkoutEntryEnt entry "
                                    + "WHERE entry.exercise.id = :exerciseId",
                            Long.class
                    )
                    .setParameter("exerciseId", id)
                    .getSingleResult();

            if (usageCount > 0) {
                throw new IllegalStateException(
                        "Cannot delete exercise because it is used "
                                + "in " + usageCount + " workout entries."
                );
            }

            em.remove(entity);
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

}
