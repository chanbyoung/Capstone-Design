package durikkiri.project.entity.dto.member;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.PostsGetDto;
import durikkiri.project.entity.post.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.parameters.P;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MemberGetDto {
    private Long id;
    private String username;
    private String nickname;
    private String major;
    private List<PostsGetDto> progressProject;
    private List<PostsGetDto> recruitingProject;
    private List<PostsGetDto> myApplyProject;
    private List<PostsGetDto> myLikeProject;

    static public MemberGetDto toDto(Member member,
                                     List<Post> progressProject,List<Post> recruitingProject, List<Post> myApplyProject, List<Post> myLikeProject) {
        return MemberGetDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .major(member.getMajor())
                .progressProject(postToDto(progressProject))
                .recruitingProject(postToDto(recruitingProject))
                .myApplyProject(postToDto(myApplyProject))
                .myLikeProject(postToDto(myLikeProject))
                .build();
    }
    private static List<PostsGetDto> postToDto(List<Post> projectList) {
        if (projectList == null) {
            return Collections.emptyList();
        }

        return projectList.stream().map(PostsGetDto::toDto).collect(Collectors.toList());    }
}
