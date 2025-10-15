package com.example.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.demo.model.User;

@Repository
public class UserRepository {
    private List<User> users = new ArrayList<>();

    public UserRepository() {
        users = new ArrayList<>();
    }

    public List<User> findAll() {
        return users;
    }
    
    public Optional<User> findById(UUID id) {
        return users.stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst();
    }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        } else {
            deleteById(user.getId());
        }
        users.add(user);
        return user;
    }

    public void deleteById(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }

}
