package ssuPlector.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ImageDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ImageDetailRequestDTO {

        private String imagePath;

        private Boolean isMainImage;
    }
}
