package durikkiri.project.entity.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserStatusDto {
    private Boolean liked;
    private Boolean owner;
}
