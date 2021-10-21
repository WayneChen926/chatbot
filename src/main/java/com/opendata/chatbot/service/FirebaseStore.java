package com.opendata.chatbot.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface FirebaseStore {
    String uploadFiles(MultipartFile file) throws IOException;
    Map<String, Map<String, Object>> download(String fileName) throws IOException;
}
