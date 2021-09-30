package com.opendata.chatbot.service;

import com.opendata.chatbot.dto.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(String userId);

    User saveUser(User user);
}
