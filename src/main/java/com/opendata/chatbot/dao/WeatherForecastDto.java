package com.opendata.chatbot.dao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.opendata.chatbot.entity.WeatherForecast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "weatherForecast")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherForecastDto {
    @Id
    private String id;
    @Field("district")
    private String district;
    @Field("weatherForecast")
    private List<WeatherForecast> weatherForecast;
    @Field("createTime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private String createTime;
}
