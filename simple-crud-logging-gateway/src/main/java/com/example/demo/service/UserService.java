package com.example.demo.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.ResourceNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id %s", id)));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(UUID id, User updatedUser) {
        return userRepository.findById(id)
                            .map(user -> {
                                user.setName(updatedUser.getName());
                                user.setEmail(updatedUser.getEmail());
                                return userRepository.save(user);
                            })
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("User not found with id %s", id)));
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
