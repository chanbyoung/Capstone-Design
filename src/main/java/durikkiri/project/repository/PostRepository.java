package durikkiri.project.repository;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select p from Post p left join fetch p.fieldList left join fetch p.image where p.id = :id")
    Optional<Post> findPostWithField(@Param("id") Long id);

    @Query("select p from Post p left join fetch p.image where p.category != :category and p.status = :status order by p.createdAt limit 10")
    List<Post> getHome(@Param("category") Category category, @Param("status") RecruitmentStatus status);
}
