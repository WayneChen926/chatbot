package com.opendata.chatbot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Time {
    private String startTime;
    private String endTime;
    private List<ElementValue> elementValue;
    private String dataTime;
}
