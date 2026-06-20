package com.example.demo.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应格式 {rows, total}
 */
@Data
public class PageResult<T> {
    private List<T> rows;
    private long total;

    public PageResult(List<T> rows, long total) {
        this.rows = rows;
        this.total = total;
    }
}
