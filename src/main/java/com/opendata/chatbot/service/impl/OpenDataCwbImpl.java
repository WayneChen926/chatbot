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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    @Value("${spring.boot.openCWB.cwbUrl}")
    private String cwbUrl;

    @Autowired
    private OpenDataRepo openDataRepo;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Lookup
    private WeatherForecast getWeatherForecast() {
        return new WeatherForecast();
    }

    @Override
    public String AllData(String url) {
        String body = null;
        try {
            body = RestTemplateUtil.GetNotValueTemplate(url).getBody();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Base64 decode Error :{}", e.getMessage());
        }
        return body;
    }

    @Override
    public void cityCwb() {
        var locationList = new LinkedList<Location>();
        var url = new String(Base64.getDecoder().decode(cwbUrl), StandardCharsets.UTF_8);
        for (int i = 1; i <= 87; i += 4) {
            var s = String.format("%03d", i);

            var openDataCwbUrl = replaceVariable(url, new String[]{s});

            rabbitTemplate.convertAndSend("tpu.queue", openDataCwbUrl,
                    correlationData -> {
                        correlationData.getMessageProperties().setDelay(2000);
                        return correlationData;
                    });
            Center center = new Center();
            try {
                Thread.sleep(2000);
                center = JsonConverter.toObject(AllData(openDataCwbUrl), Center.class);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String city = center.getRecords().getLocations().get(0).getLocationsName();

            // 取得各區域氣象資料 list
            assert false;
            if (center != null) {
                center.getRecords().getLocations().forEach(locations -> {
                    locationList.addAll(locations.getLocation());
                });
            }

            log.info("Job City = {}", city);
            weatherForecast(city, locationList);
            // 清空
            locationList.clear();
        }
    }

    @Override
    public String replaceVariable(String url, String[] vars) {
        int countVarInUrl = 0;
        String urlChk = url;

        while (urlChk.contains("$")) {
            int pos = urlChk.indexOf("$");
            urlChk = urlChk.substring(pos + 1);
            countVarInUrl++;
        }
        if (countVarInUrl != vars.length) {
            log.error("openData Url Replace Error");
        }
        for (String var : vars) {
            url = url.replaceFirst("\\$", var);
        }
        return url;
    }


    @Override
    public void weatherForecast(String locationsName, List<Location> locationList) {
        var weatherForecastList = new ArrayList<WeatherForecast>();
        var district = new AtomicReference<String>(null);
        String city = locationsName.replace("臺", "台");
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
