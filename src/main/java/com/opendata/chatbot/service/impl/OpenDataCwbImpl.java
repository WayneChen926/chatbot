package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.entity.*;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.JsonConverter;
import com.opendata.chatbot.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class OpenDataCwbImpl implements OpenDataCwb {

    @Value("${spring.boot.openCWB.taipei}")
    private String url;

    @Autowired
    private Center center;

    @Lookup
    private Location getLocation() {
        return new Location();
    }

    @Lookup
    private WeatherForecast getWeatherForecast() {
        return new WeatherForecast();
    }

    @Override
    public Center AllData() {
        String body = null;
        try {
            body = RestTemplateUtil.GetNotValueTemplate(new String(Base64.getDecoder().decode(url), StandardCharsets.UTF_8)).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Base64 decode Error :{}", e.getMessage());
        }
        return JsonConverter.toObject(body, Center.class);
    }

    @Override
    public Location taipeiCwb(String district) {
        var location = new AtomicReference<Location>(getLocation());
        //比對不到 物件就為null
        location.set(null);
        // 取出全部資料
        center = AllData();
        // 比對區
        center.getRecords().getLocations().forEach(locations -> {
            locations.getLocation().forEach(lo -> {
                if (lo.getLocationName().equals(district)) {
                    location.set(lo);
                }
            });
        });
        return location.get();
    }

    @Override
    public String weatherForecast(String district) {
        var weatherForecastList = new ArrayList<WeatherForecast>();
        Location location = taipeiCwb(district);
        AtomicInteger n = new AtomicInteger();
        if (null != location) {
            location.getWeatherElement().forEach(weatherElement -> {
                n.set(0);
                var weatherForecast = getWeatherForecast();
                weatherForecast.setDescription(weatherElement.getDescription());
                weatherForecast.setElementName(weatherElement.getElementName());
                weatherElement.getTime().forEach(time -> {
                    n.getAndIncrement();
                    if (n.get() < 2) {
                        weatherForecast.setStartTime(time.getStartTime());
                        weatherForecast.setDataTime(time.getDataTime());
                        time.getElementValue().forEach(elementValue -> {
                            // 過濾 Wx 數字單位
                            if (!elementValue.getMeasures().equals("自定義 Wx 單位")) {
                                weatherForecast.setValue(elementValue.getValue());
                                weatherForecast.setMeasures(elementValue.getMeasures());
                            }
                        });
                    }
                });
                weatherForecastList.add(weatherForecast);
            });
            return JsonConverter.toJsonString(weatherForecastList);
        } else {
            return null;
        }
    }
}
