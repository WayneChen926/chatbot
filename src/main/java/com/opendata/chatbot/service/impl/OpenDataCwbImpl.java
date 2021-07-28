package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.entity.Center;
import com.opendata.chatbot.entity.Location;
import com.opendata.chatbot.entity.Locations;
import com.opendata.chatbot.entity.Records;
import com.opendata.chatbot.service.OpenDataCWB;
import com.opendata.chatbot.util.JsonConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenDataCwbImpl implements OpenDataCWB {

    @Autowired
    Center center;

    @Override
    public String taipeiCwb(Center center) {
        List<Location> locationList = new ArrayList<>();
        center.getRecords().getLocations().forEach(locations->{
            for (Location location : locations.getLocation()) {
                if (location.getLocationName().equals("士林區")) {
                    locationList.add(location);
                }
                ;
            }
        });
        return JsonConverter.toJsonString(locationList);
    }
}
