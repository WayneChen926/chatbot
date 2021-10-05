package com.opendata.chatbot.repository;

import com.opendata.chatbot.dao.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<User,String> {
}
