package com.opendata.chatbot.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class RestTemplateUtil {
    private final static RestTemplate restTemplate;

    static {
        var requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(20000);
        requestFactory.setConnectionRequestTimeout(20000);
        requestFactory.setReadTimeout(30000);
        restTemplate = new RestTemplate(requestFactory);
    }

    public static ResponseEntity<String> PostTemplate(String url, String json, HttpHeaders headers) {
        return restTemplate.exchange(url, HttpMethod.POST,
                new HttpEntity<>(json, headers),
                String.class);
    }

    public static ResponseEntity<String> PostXEncoded(String url, HttpEntity<MultiValueMap<String, Object>> parameter) {
        return restTemplate.postForEntity(URI.create(url), parameter, String.class);
    }

    public static ResponseEntity<String> GetTemplate(String url, String json, HttpHeaders headers) {
        return restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(json, headers),
                String.class);
    }

    public static ResponseEntity<String> GetNotValueTemplate(String url) {
        return restTemplate.getForEntity(URI.create(url), String.class);
    }
}
