package com.agilestests.testcase.service;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ImageResponseDto {
    private List<Map<String, String>> pictures;
    private int page;
    private int pageCount;
    private boolean hasMore;
}

