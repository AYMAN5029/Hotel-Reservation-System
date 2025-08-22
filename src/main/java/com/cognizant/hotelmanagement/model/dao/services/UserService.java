package com.cognizant.hotelmanagement.model.dao.services;

import com.cognizant.hotelmanagement.model.pojo.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    User createUser(User user);
    Optional<User> loginUser(String username, String password);
    Optional<User> getUserById(Long userId);
    Optional<User> getUserByUsername(String username);
    User findByUsername(String username);
    Optional<User> getUserByEmail(String email);
    List<User> getAllUsers();
    User updateUser(Long userId, User user);
    void deleteUser(Long userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}