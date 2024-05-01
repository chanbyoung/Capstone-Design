package durikkiri.project.entity.dto.member;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.PostsGetDto;
import durikkiri.project.entity.post.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemberGetDto {
    private Long id;
    private String username;
    private String nickname;
    private List<PostsGetDto> progressProject;
    private List<PostsGetDto> recruitingProject;

    static public MemberGetDto toDto(Member member, List<Post> progressProject,List<Post> recruitingProject) {
        return MemberGetDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .progressProject(postToDto(progressProject))
                .recruitingProject(postToDto(recruitingProject))
                .build();
    }
    private static List<PostsGetDto> postToDto(List<Post> progressProject) {
        return progressProject.stream().map(PostsGetDto::toDto).collect(Collectors.toList());
    }
}
