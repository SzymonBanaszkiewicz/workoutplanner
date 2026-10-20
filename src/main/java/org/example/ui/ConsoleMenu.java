
package org.example.ui;

import org.example.application.dto.CreateWorkoutCommand;
import org.example.application.dto.WorkoutSummary;
import org.example.application.service.ExerciseService;
import org.example.application.service.UserService;
import org.example.application.service.WorkoutService;
import org.example.domain.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ConsoleMenu {

    private final UserService userService;
    private final ExerciseService exerciseService;
    private final WorkoutService workoutService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(
            UserService userService,
            ExerciseService exerciseService,
            WorkoutService workoutService
    ) {
        this.userService = userService;
        this.exerciseService = exerciseService;
        this.workoutService = workoutService;
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> registerUser();
                    case "2" -> listUsers();
                    case "3" -> addStrengthExercise();
                    case "4" -> addCardioExercise();
                    case "5" -> listExercises();
                    case "6" -> createWorkout();
                    case "7" -> listUserWorkouts();
                    case "8" -> showWorkoutDetails();
                    case "9" -> deleteWorkout();
                    case "10" -> addExerciseToWorkout();
                    case "11" -> editWorkout();
                    case "12" -> removeExerciseFromWorkout();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option.");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        System.out.println("Goodbye!");
    }

    private void printMenu() {
        System.out.println("\n=== WORKOUT PLANNER ===");
        System.out.println("1. Register user");
        System.out.println("2. List users");
        System.out.println("3. Add strength exercise");
        System.out.println("4. Add cardio exercise");
        System.out.println("5. List exercises");
        System.out.println("6. Create workout");
        System.out.println("7. List user's workouts");
        System.out.println("8. Show workout details");
        System.out.println("9. Delete workout");
        System.out.println("10. Add exercise to workout");
        System.out.println("11. Edit workout");
        System.out.println("12. Remove exercise from workout");
        System.out.println("0. Exit");
    }

    private void registerUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        User user = userService.registerUser(username, email);

        System.out.println("User created successfully.");
        System.out.println("ID: " + user.getId());
    }

    private void listUsers() {
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        for (User user : users) {
            System.out.printf(
                    "ID: %s | Username: %s | Email: %s%n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail()
            );
        }
    }

    private void addStrengthExercise() {
        System.out.print("Exercise name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Muscle group: ");
        String muscleGroup = scanner.nextLine().trim();

        System.out.print("Equipment: ");
        String equipment = scanner.nextLine().trim();

        StrengthExercise exercise =
                exerciseService.createStrengthExercise(
                        name,
                        muscleGroup,
                        equipment
                );

        System.out.println("Strength exercise created.");
        System.out.println("ID: " + exercise.getId());
    }

    private void addCardioExercise() {
        System.out.print("Exercise name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Activity type: ");
        String activityType = scanner.nextLine().trim();

        Integer targetHeartRate = readOptionalInteger(
                "Target heart rate (leave empty if not applicable): "
        );

        CardioExercise exercise =
                exerciseService.createCardioExercise(
                        name,
                        activityType,
                        targetHeartRate
                );

        System.out.println("Cardio exercise created.");
        System.out.println("ID: " + exercise.getId());
    }

    private void listExercises() {
        List<Exercise> exercises = exerciseService.getAllExercises();

        if (exercises.isEmpty()) {
            System.out.println("No exercises found.");
            return;
        }

        for (Exercise exercise : exercises) {
            System.out.printf(
                    "ID: %s | Name: %s | Type: %s%n",
                    exercise.getId(),
                    exercise.getName(),
                    exercise.getClass().getSimpleName()
            );
        }
    }

    private void createWorkout() {
        System.out.print("User ID: ");
        UUID userId = readUuid();

        System.out.print("Workout name: ");
        String name = scanner.nextLine().trim();

        System.out.print(
                "Performed at (yyyy-MM-ddTHH:mm), "
                        + "leave empty for current time: "
        );

        String dateInput = scanner.nextLine().trim();

        LocalDateTime performedAt = dateInput.isEmpty()
                ? LocalDateTime.now()
                : LocalDateTime.parse(dateInput);

        CreateWorkoutCommand command = new CreateWorkoutCommand(
                userId,
                name,
                performedAt
        );

        Workout workout = workoutService.createWorkout(command);

        System.out.println("Workout created successfully.");
        System.out.println("ID: " + workout.getId());
    }


    private void editWorkout() {
        System.out.print("Workout ID: ");
        UUID workoutId = readUuid();

        System.out.print("New workout name: ");
        String newName = scanner.nextLine().trim();

        System.out.print("New date (yyyy-MM-ddTHH:mm): ");
        LocalDateTime newPerformedAt =
                LocalDateTime.parse(scanner.nextLine().trim());

        Workout workout = workoutService.updateWorkout(
                workoutId,
                newName,
                newPerformedAt
        );

        System.out.println("Workout updated successfully.");
        System.out.println("Name: " + workout.getName());
        System.out.println("Date: " + workout.getPerformedAt());
    }


    private void listUserWorkouts() {
        System.out.print("User ID: ");
        UUID userId = readUuid();

        List<WorkoutSummary> workouts =
                workoutService.getWorkoutsForUser(userId);

        if (workouts.isEmpty()) {
            System.out.println("No workouts found for this user.");
            return;
        }

        for (WorkoutSummary workout : workouts) {
            System.out.printf(
                    "ID: %s | Name: %s | Date: %s | Entries: %d%n",
                    workout.getId(),
                    workout.getName(),
                    workout.getPerformedAt(),
                    workout.getExerciseCount()
            );
        }
    }

    private void showWorkoutDetails() {
        System.out.print("Workout ID: ");
        UUID workoutId = readUuid();

        Workout workout = workoutService.getWorkoutById(workoutId);

        System.out.println("\n=== WORKOUT DETAILS ===");
        System.out.println("ID: " + workout.getId());
        System.out.println("Name: " + workout.getName());
        System.out.println("Performed at: " + workout.getPerformedAt());
        System.out.println("Entries: " + workout.getEntries().size());

        if (workout.getEntries().isEmpty()) {
            System.out.println("No exercises added yet.");
            return;
        }

        for (WorkoutEntry entry : workout.getEntries()) {
            System.out.println("\nExercise: " + entry.getExercise().getName());
            System.out.println("Entry ID: " + entry.getId());
            System.out.println("Type: "
                    + entry.getExercise().getClass().getSimpleName());
            System.out.println("Sets: " + displayValue(entry.getSets()));
            System.out.println(
                    "Repetitions: " + displayValue(entry.getRepetitions())
            );
            System.out.println(
                    "Weight (kg): " + displayValue(entry.getWeightKg())
            );
            System.out.println(
                    "Duration (min): "
                            + displayValue(entry.getDurationMinutes())
            );
        }
    }

    private void addExerciseToWorkout() {
        System.out.print("Workout ID: ");
        UUID workoutId = readUuid();

        System.out.print("Exercise ID: ");
        UUID exerciseId = readUuid();

        Integer sets = readOptionalInteger(
                "Sets (leave empty if not applicable): "
        );

        Integer repetitions = readOptionalInteger(
                "Repetitions (leave empty if not applicable): "
        );

        BigDecimal weightKg = readOptionalBigDecimal(
                "Weight in kg (leave empty if not applicable): "
        );

        Integer durationMinutes = readOptionalInteger(
                "Duration in minutes (leave empty if not applicable): "
        );

        Workout workout = workoutService.addExerciseToWorkout(
                workoutId,
                exerciseId,
                sets,
                repetitions,
                weightKg,
                durationMinutes
        );

        System.out.println(
                "Exercise added to workout: " + workout.getName()
        );
    }

    private void deleteWorkout() {
        System.out.print("Workout ID: ");
        UUID workoutId = readUuid();

        workoutService.deleteWorkout(workoutId);
        System.out.println("Workout deleted successfully.");
    }

    private UUID readUuid() {
        return UUID.fromString(scanner.nextLine().trim());
    }

    private Integer readOptionalInteger(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        return input.isEmpty() ? null : Integer.parseInt(input);
    }

    private BigDecimal readOptionalBigDecimal(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            return null;
        }

        return new BigDecimal(input.replace(',', '.'));
    }

    private String displayValue(Object value) {
        return value == null ? "-" : value.toString();
    }


    private void removeExerciseFromWorkout() {
        System.out.print("Workout ID: ");
        UUID workoutId = readUuid();

        System.out.print("Workout entry ID: ");
        UUID entryId = readUuid();

        Workout workout = workoutService.removeExerciseFromWorkout(
                workoutId,
                entryId
        );

        System.out.println(
                "Exercise removed from workout: " + workout.getName()
        );
    }

}
