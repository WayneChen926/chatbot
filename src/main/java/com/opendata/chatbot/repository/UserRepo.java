package com.opendata.chatbot.repository;

import com.opendata.chatbot.dto.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends MongoRepository<User,String> {
}
