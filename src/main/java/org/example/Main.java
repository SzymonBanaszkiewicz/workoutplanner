package org.example;

import org.example.application.service.ExerciseService;
import org.example.application.service.UserService;
import org.example.application.service.WorkoutService;

import org.example.persistence.mongo.adapter.MongoExerciseRepositoryAdapter;
import org.example.persistence.mongo.adapter.MongoUserRepositoryAdapter;
import org.example.persistence.mongo.adapter.MongoWorkoutRepositoryAdapter;

import org.example.persistence.mongo.config.MongoConfig;
import org.example.persistence.mongo.config.MongoIndexInitializer;

import org.example.persistence.mongo.repository.MongoExerciseRepository;
import org.example.persistence.mongo.repository.MongoUserRepository;
import org.example.persistence.mongo.repository.MongoWorkoutRepository;

import org.example.ui.ConsoleMenu;

public class Main {

    public static void main(String[] args) {
        MongoConfig mongoConfig = null;

        try {
            mongoConfig = new MongoConfig();

            MongoIndexInitializer indexInitializer =
                    new MongoIndexInitializer(mongoConfig.getDatabase());

            indexInitializer.initialize();

            MongoUserRepository mongoUserRepository =
                    new MongoUserRepository(mongoConfig.getDatabase());

            MongoExerciseRepository mongoExerciseRepository =
                    new MongoExerciseRepository(mongoConfig.getDatabase());

            MongoWorkoutRepository mongoWorkoutRepository =
                    new MongoWorkoutRepository(mongoConfig.getDatabase());

            MongoUserRepositoryAdapter userRepository =
                    new MongoUserRepositoryAdapter(mongoUserRepository);

            MongoExerciseRepositoryAdapter exerciseRepository =
                    new MongoExerciseRepositoryAdapter(mongoExerciseRepository);

            MongoWorkoutRepositoryAdapter workoutRepository =
                    new MongoWorkoutRepositoryAdapter(
                            mongoWorkoutRepository,
                            mongoExerciseRepository
                    );

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
            if (mongoConfig != null) {
                mongoConfig.close();
            }
        }
    }
}
