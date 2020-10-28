package com.agilestests.testcase.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class Photo {
    @Id
    private String id;
    private String author;
    private String camera;
    private String tags;
    private String croppedPicture;
    private String fullPicture;

    @JsonCreator
    public Photo(@JsonProperty("id") String id,
                 @JsonProperty("author") String author,
                 @JsonProperty("camera") String camera,
                 @JsonProperty("tags") String tags,
                 @JsonProperty("cropped_picture") String croppedPicture,
                 @JsonProperty("full_picture") String fullPicture) {
        this.id = id;
        this.author = author;
        this.camera = camera;
        this.tags = tags;
        this.croppedPicture = croppedPicture;
        this.fullPicture = fullPicture;
    }
}
