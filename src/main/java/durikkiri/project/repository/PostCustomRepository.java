package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import durikkiri.project.dto.post.PostSearchContent;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostCustomRepository {
    Page<Post> getPostsByOffset(Pageable pageable, PostSearchContent postSearchContent);
    List<Post> getLikePostList(Category category);
    List<Post> progressProject(Member member);
    List<Post> myRecruitingProject(Member member);
    List<Post> myApplyProject(Member member);

}
