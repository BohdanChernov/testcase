package com.agilestests.testcase.service;

import com.agilestests.testcase.models.Photo;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ImageResponseDto {
    private List<Photo> pictures;
    private int page;
    private int pageCount;
    private boolean hasMore;

    @JsonCreator
    public ImageResponseDto(@JsonProperty("pictures") List<Photo> pictures,
                            @JsonProperty("page") int page,
                            @JsonProperty("pageCount") int pageCount,
                            @JsonProperty("hasMore") boolean hasMore) {
        this.pictures = pictures;
        this.page = page;
        this.pageCount = pageCount;
        this.hasMore = hasMore;
    }
}

