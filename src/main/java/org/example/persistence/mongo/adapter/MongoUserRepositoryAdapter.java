package org.example.persistence.mongo.adapter;

import org.example.domain.model.User;
import org.example.persistence.mongo.document.UserDocument;
import org.example.persistence.mongo.mapper.UserMapper;
import org.example.persistence.mongo.repository.MongoUserRepository;
import org.example.ports.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MongoUserRepositoryAdapter implements UserRepository {

    private final MongoUserRepository mongoRepository;

    public MongoUserRepositoryAdapter(MongoUserRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public User save(User user) {
        UserDocument document = UserMapper.toDocument(user);
        UserDocument savedDocument = mongoRepository.save(document);

        return UserMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return mongoRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return mongoRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        mongoRepository.deleteById(id);
    }
}