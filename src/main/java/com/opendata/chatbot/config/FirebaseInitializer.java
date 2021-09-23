package com.opendata.chatbot.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FirebaseInitializer {

    @Value("${spring.boot.SecretManager.key}")
    private String key;

    @PostConstruct
    public void initialize() throws IOException {
        InputStream is = new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8));
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(is))
                .build();

        FirebaseApp.initializeApp(options);

    }
}
