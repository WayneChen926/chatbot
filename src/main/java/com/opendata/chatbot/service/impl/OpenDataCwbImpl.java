package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.dao.WeatherForecastDto;
import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.entity.Location;
import com.opendata.chatbot.entity.WeatherForecast;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.util.JsonConverter;
import com.opendata.chatbot.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class OpenDataCwbImpl implements OpenDataCwb {

    @Value("${spring.boot.openCWB.taipei}")
    private String taipeiUrl;

    @Value("${spring.boot.openCWB.newTaipei}")
    private String newTaipeiUrl;

    @Value("${spring.boot.openCWB.taoyuan}")
    private String taoyuanUrl;

    @Value("${spring.boot.openCWB.keelung}")
    private String keelungUrl;

    @Value("${spring.boot.openCWB.yilan}")
    private String yilanUrl;

    @Autowired
    private OpenDataRepo openDataRepo;

    @Lookup
    private Location getLocation() {
        return new Location();
    }

    @Lookup
    private WeatherForecast getWeatherForecast() {
        return new WeatherForecast();
    }

    @Override
    public String AllData(String url) {
        String body = null;
        try {
            body = RestTemplateUtil.GetNotValueTemplate(new String(Base64.getDecoder().decode(url), StandardCharsets.UTF_8)).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Base64 decode Error :{}", e.getMessage());
        }
        return body;
    }

    @Override
    public List<Location> cityCwb(String city) {
        var locationList = new LinkedList<Location>();

        Center center = null;
        String[] district;
        switch (city) {
            case "新北市":
                center = JsonConverter.toObject(AllData(newTaipeiUrl), Center.class);
                break;
            case "桃園市":
                center = JsonConverter.toObject(AllData(taoyuanUrl), Center.class);
                break;
            case "台北市":
                center = JsonConverter.toObject(AllData(taipeiUrl), Center.class);
                break;
            case "基隆市":
                center = JsonConverter.toObject(AllData(keelungUrl), Center.class);
                break;
            case "宜蘭縣":
                center = JsonConverter.toObject(AllData(yilanUrl), Center.class);
                break;
        }
        // 比對區
        if (center != null) {
            center.getRecords().getLocations().forEach(locations -> {
                locationList.addAll(locations.getLocation());
            });
        }
        return locationList;
    }

    @Override
    public void weatherForecast(String city) {
        var weatherForecastList = new ArrayList<WeatherForecast>();
        var locationList = cityCwb(city);
        var district = new AtomicReference<String>(null);
        var n = new AtomicInteger();
        locationList.forEach(location -> {
            district.set(location.getLocationName());
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
            var weatherForecastDto = new WeatherForecastDto();
            var w = openDataRepo.findByDistrictAndCity(district.get(), city);
            if (w != null) {
                weatherForecastDto.setId(w.getId());
            } else {
                weatherForecastDto.setId(UUID.randomUUID().toString());
            }
            weatherForecastDto.setCity(city);
            weatherForecastDto.setDistrict(district.get());
            weatherForecastDto.setCreateTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 加入新數據
            weatherForecastDto.setWeatherForecast(weatherForecastList);
            openDataRepo.save(weatherForecastDto);
            // 清空
            weatherForecastList.clear();
        });
    }
}
