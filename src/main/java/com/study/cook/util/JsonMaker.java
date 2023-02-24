package com.study.cook.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class JsonMaker {

    // map -> json
    public static String getJson(Map<String, Object> result) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(result);
        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        return json;
    }
}
