package com.opendata.chatbot.controller;

import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.service.LineService;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class WebController {

    @Autowired
    private LineService lineServiceImpl;

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    /*
     * LineBot WebHook 驗證回訊息
     */
    @PostMapping("/webHook")
    public ResponseEntity<String> webHook(@RequestBody String requestBody,
                                          @RequestHeader("X-Line-Signature") String line_headers) {
        log.info("Begin Controller => {}", requestBody);
        return lineServiceImpl.WebHook(requestBody, line_headers);
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        return userServiceImpl.getAllUsers();
    }
}
