package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.repository.UserRepo;
import com.opendata.chatbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepo userRepo;

    @Override
    public List<User> getAllUsers() {
        var obj = redisTemplate.opsForValue().get("AllUser");
        if (obj != null) {
            return (List<User>) obj;
        } else {
            var userList = userRepo.findAll();
            redisTemplate.opsForValue().set("AllUser", userList);
            return userList;
        }
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
