
package org.example.application.service;

import org.example.domain.model.User;
import org.example.ports.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(String username, String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "User with this email already exists"
            );
        }

        User user = new User(username, email);
        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found: " + id
                ));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "User not found with email: " + email
                ));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
