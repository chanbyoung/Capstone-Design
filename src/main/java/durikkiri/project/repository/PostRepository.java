package durikkiri.project.repository;

import durikkiri.project.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select p from Post p left join fetch p.fieldList where p.id = :id")
    Optional<Post> findPostWithField(@Param("id") Long id);
}
