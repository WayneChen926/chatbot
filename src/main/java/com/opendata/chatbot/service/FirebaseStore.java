package com.opendata.chatbot.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseStore {
    String uploadFiles(MultipartFile file) throws IOException;
    String download(String fileName) throws IOException;
}
