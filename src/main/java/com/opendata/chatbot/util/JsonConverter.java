package com.opendata.chatbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonConverter {
    private final static ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
    }

    public static<T> String toJsonString(T obj){
        MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        try{
            log.info(obj.toString());
            return MAPPER.writeValueAsString(obj);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    public static <T> T toObject(String json,Class<T> obj){
        try{
            return MAPPER.readValue(json,obj);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
}
