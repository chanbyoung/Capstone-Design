package durikkiri.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "post_id")
    private Post post;
    private String uploadFileName; //고객이 업로드한 파일명
    private String storeFileName; //서버 내부에서 관리하는 파일명
    private String fullPath; //fileService 에서 저장

    public static Image toEntity(MultipartFile image, String fileDir, Post savePost) {
        String uploadFileName = image.getOriginalFilename();
        String storeFileName = createStoreFileName(uploadFileName);
        return Image.builder()
                .uploadFileName(uploadFileName)
                .storeFileName(storeFileName)
                .post(savePost)
                .fullPath(fileDir+storeFileName)
                .build();
    }
    private static String createStoreFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid+ "." +ext;
    }
    private static String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public void updateImage(Image newImage) {
        this.uploadFileName = newImage.getUploadFileName();
        this.storeFileName = newImage.getStoreFileName();
        this.fullPath = newImage.getFullPath();
    }
}
