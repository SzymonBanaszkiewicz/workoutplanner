
package org.example.application.service;

import org.example.application.dto.CreateWorkoutCommand;
import org.example.application.dto.WorkoutSummary;
import org.example.domain.model.Exercise;
import org.example.domain.model.User;
import org.example.domain.model.Workout;
import org.example.domain.model.WorkoutEntry;
import org.example.ports.repository.ExerciseRepository;
import org.example.ports.repository.UserRepository;
import org.example.ports.repository.WorkoutRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(
            WorkoutRepository workoutRepository,
            UserRepository userRepository,
            ExerciseRepository exerciseRepository
    ) {
        this.workoutRepository = workoutRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Workout createWorkout(CreateWorkoutCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found: " + command.getUserId()
                ));

        Workout workout = new Workout(
                user.getId(),
                command.getName(),
                command.getPerformedAt()
        );

        return workoutRepository.save(workout);
    }


    public Workout updateWorkout(
            UUID workoutId,
            String newName,
            LocalDateTime newPerformedAt
    ) {
        Workout workout = getWorkoutById(workoutId);

        workout.setName(newName);
        workout.setPerformedAt(newPerformedAt);

        return workoutRepository.save(workout);
    }


    public Workout getWorkoutById(UUID id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Workout not found: " + id
                ));
    }

    public List<WorkoutSummary> getWorkoutsForUser(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NoSuchElementException("User not found: " + userId);
        }

        return workoutRepository.findByUserId(userId)
                .stream()
                .map(workout -> new WorkoutSummary(
                        workout.getId(),
                        workout.getUserId(),
                        workout.getName(),
                        workout.getPerformedAt(),
                        workout.getEntries().size()
                ))
                .toList();
    }

    public void deleteWorkout(UUID id) {
        getWorkoutById(id);
        workoutRepository.deleteById(id);
    }

    public Workout addExerciseToWorkout(
            UUID workoutId,
            UUID exerciseId,
            Integer sets,
            Integer repetitions,
            BigDecimal weightKg,
            Integer durationMinutes
    ) {
        Workout workout = getWorkoutById(workoutId);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Exercise not found: " + exerciseId
                ));

        WorkoutEntry entry = new WorkoutEntry(
                exercise,
                sets,
                repetitions,
                weightKg,
                durationMinutes
        );

        workout.addEntry(entry);

        return workoutRepository.save(workout);
    }


    public Workout removeExerciseFromWorkout(UUID workoutId, UUID entryId) {
        Workout workout = getWorkoutById(workoutId);

        boolean removed = workout.removeEntry(entryId);

        if (!removed) {
            throw new NoSuchElementException(
                    "Exercise entry not found in workout: " + entryId
            );
        }

        return workoutRepository.save(workout);
    }

}
