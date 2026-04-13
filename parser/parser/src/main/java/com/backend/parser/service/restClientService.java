package com.backend.parser.service;

import com.backend.parser.controller.uploadController;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.Map;

@Service
public class restClientService {

    private final RestClient restClient;
    public restClientService(){
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:5000")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String postStudentData(Map<String, Object> studentData){
        return restClient.post()
                .uri("/studentData")
                .body(studentData)
                .retrieve()            //Retrieves Response from Python and
                .body(String.class);   //Converts it to String
    }

}
