package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.dto.User;
import com.opendata.chatbot.repository.UserRepo;
import com.opendata.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(String userId) {
        return userRepo.findById(userId).orElse(null);
    }

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }
}
