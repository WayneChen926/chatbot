package com.opendata.chatbot.controller;

import com.opendata.chatbot.service.AesECB;
import com.opendata.chatbot.service.LineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class WebController {

    @Autowired
    private LineService lineService;

    @Autowired
    private AesECB aesECB;

    @PostMapping("/webHook")
    public ResponseEntity<String> webHook(@RequestBody String requestBody,
                                          @RequestHeader("X-Line-Signature") String line_headers) {
        return lineService.WebHook(requestBody,line_headers);
    }
}
