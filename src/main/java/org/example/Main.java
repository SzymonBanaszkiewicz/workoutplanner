
package org.example;

import org.example.application.service.ExerciseService;
import org.example.application.service.UserService;
import org.example.application.service.WorkoutService;
import org.example.persistence.adapter.JpaExerciseRepositoryAdapter;
import org.example.persistence.adapter.JpaUserRepositoryAdapter;
import org.example.persistence.adapter.JpaWorkoutRepositoryAdapter;
import org.example.persistence.config.JpaConfig;
import org.example.ui.ConsoleMenu;

public class Main {

    public static void main(String[] args) {
        try {
            JpaUserRepositoryAdapter userRepository =
                    new JpaUserRepositoryAdapter();

            JpaExerciseRepositoryAdapter exerciseRepository =
                    new JpaExerciseRepositoryAdapter();

            JpaWorkoutRepositoryAdapter workoutRepository =
                    new JpaWorkoutRepositoryAdapter();

            UserService userService =
                    new UserService(userRepository);

            ExerciseService exerciseService =
                    new ExerciseService(exerciseRepository);

            WorkoutService workoutService =
                    new WorkoutService(
                            workoutRepository,
                            userRepository,
                            exerciseRepository
                    );

            ConsoleMenu menu = new ConsoleMenu(
                    userService,
                    exerciseService,
                    workoutService
            );

            menu.run();
        } finally {
            JpaConfig.shutdown();
        }
    }
}
