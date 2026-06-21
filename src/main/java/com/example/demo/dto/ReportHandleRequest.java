package com.example.demo.dto;

import lombok.Data;

@Data
public class ReportHandleRequest {
    /** REMOVE=审核下架, NO_VIOLATION=审核无违规 */
    private String action;
}
