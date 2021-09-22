package com.opendata.chatbot.util;

import com.opendata.chatbot.service.AesECB;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HeadersUtil {

    @Value("${spring.line.channelToken}")
    private String channelToken;

    @Autowired
    private AesECB aesECBImpl;

    public HttpHeaders setHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Authorization", "Bearer {" + aesECBImpl.aesDecrypt(channelToken) + "}");
        return headers;
    }
}
