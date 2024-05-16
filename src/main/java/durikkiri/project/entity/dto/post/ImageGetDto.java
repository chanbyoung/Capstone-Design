package durikkiri.project.entity.dto.post;

import durikkiri.project.entity.Image;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ImageGetDto {
    private String imageUrl;
    private String originalFileName;


    public static ImageGetDto toDto(Image image) {
        return ImageGetDto.builder()
                .imageUrl(convertToWebUrl(image.getStoreFileName()))
                .originalFileName(image.getUploadFileName())
                .build();
    }

    private static String convertToWebUrl(String imagePath) {
        String baseUrl = "http://localhost:8080/images/";
        return baseUrl +imagePath;
    }
}
