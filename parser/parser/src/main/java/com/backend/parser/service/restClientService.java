package com.backend.parser.service;

import com.backend.parser.dto.StudentDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class restClientService {

    private final RestTemplate restTemplate;

    public restClientService() {
        this.restTemplate = new RestTemplate();
    }

    public String postStudentData(List<StudentDTO> studentData, String jobId) {
        java.util.HashMap<String, Object> payload= new HashMap<>();
        payload.put("jobId", jobId);
        payload.put("students", studentData);

        String url = "http://127.0.0.1:8000/studentData";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(payload, headers);

        System.out.println("Sending JSON: " + request);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class //?
        );

        return response.getBody();
    }
}