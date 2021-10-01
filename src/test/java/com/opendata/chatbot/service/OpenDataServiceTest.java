package com.opendata.chatbot.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OpenDataServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OpenDataCwb openDataCwb;

    @Autowired
    private LineService lineService;

    @Test
    void weatherForecast() throws Exception {
    }
}
