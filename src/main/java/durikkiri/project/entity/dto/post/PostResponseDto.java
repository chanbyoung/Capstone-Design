package durikkiri.project.entity.dto.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Slice;

import java.util.List;

@Getter
@Setter
public class PostResponseDto {
    private List<PostsGetDto> content;
    private boolean hasNext;

    public PostResponseDto(Slice<PostsGetDto> slice) {
        this.content = slice.getContent();
        this.hasNext = slice.hasNext();
    }
}
