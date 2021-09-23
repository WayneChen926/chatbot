package com.opendata.chatbot.controller;

import com.opendata.chatbot.entity.Source;
import com.opendata.chatbot.service.AesECB;
import com.opendata.chatbot.service.LineService;
import com.opendata.chatbot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class WebController {

    @Autowired
    private LineService lineService;

    @Autowired
    private AesECB aesECB;

    @Autowired
    private UserService userService;

    /*
     * LineBot WebHook 驗證回訊息
     */
    @PostMapping("/webHook")
    public ResponseEntity<String> webHook(@RequestBody String requestBody,
                                          @RequestHeader("X-Line-Signature") String line_headers) {
        log.info("Begin Controller => {}", requestBody);
        return lineService.WebHook(requestBody, line_headers);
    }

    @PostMapping("/source")
    public String saveUserId(@RequestBody Source source) throws ExecutionException, InterruptedException {
        return userService.saveUserId(source);
    }

    @GetMapping("/source/{id}")
    public Source saveUserId(@PathVariable String id) throws ExecutionException, InterruptedException {
        return userService.getUserId(id);
    }

    @GetMapping("/source")
    public List<Source> saveUserId() throws ExecutionException, InterruptedException {
        return userService.getAllUser();
    }
}
