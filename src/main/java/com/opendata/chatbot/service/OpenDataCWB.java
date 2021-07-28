package com.opendata.chatbot.service;

import com.opendata.chatbot.entity.Center;
import org.springframework.stereotype.Service;

@Service
public interface OpenDataCWB {
    String taipeiCwb(Center center);
}
