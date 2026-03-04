package com.example.crm.dto.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class ApiPageResponse<T> {
    private long page;
    private long size;
    private long total;
    private long totalPages;
    private List<T> items;

    public static <T> ApiPageResponse<T> from(IPage<T> pageData) {
        ApiPageResponse<T> response = new ApiPageResponse<>();
        response.setPage(pageData.getCurrent());
        response.setSize(pageData.getSize());
        response.setTotal(pageData.getTotal());
        response.setTotalPages(pageData.getPages());
        response.setItems(pageData.getRecords());
        return response;
    }
}
