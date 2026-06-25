package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HelpRequestDto {
    private String title;
    private String description;
    private BigDecimal reward;
}
