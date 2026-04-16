package com.backend.parser.service;

import com.backend.parser.dto.StudentDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class restClientService {

    private final RestTemplate restTemplate;

    public restClientService() {
        this.restTemplate = new RestTemplate();
    }

    public String postStudentData(List<StudentDTO> studentData) {

        String url = "http://127.0.0.1:8000/studentData";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<StudentDTO>> request = new HttpEntity<>(studentData, headers);

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