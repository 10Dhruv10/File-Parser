package com.backend.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadResponse {
    private String jobId;
    private String message;
    private int totalFiles;

}
