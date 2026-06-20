package com.example.demo.dto;

import lombok.Data;

@Data
public class AuditRequest {
    /** true通过 false驳回 */
    private Boolean approved;
    private String reason;
}
