package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.PostSearchContent;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface PostCustomRepository {
    Slice<Post> getPostsByCursor(Pageable pageable, PostSearchContent postSearchContent);
    List<Post> getLikePostList(Category category);
    List<Post> progressProject(Member member);
    List<Post> myRecruitingProject(Member member);
    List<Post> myApplyProject(Member member);

}
