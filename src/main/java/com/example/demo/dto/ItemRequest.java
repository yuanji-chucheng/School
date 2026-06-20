package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String category;
    private Integer conditionLevel;
    private String images;
}
