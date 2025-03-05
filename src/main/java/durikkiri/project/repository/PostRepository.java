package durikkiri.project.repository;

import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, PostCustomRepository{
    @Query("""
           select p
           from Post p
           join fetch p.member m
           join fetch p.recruitmentInfo r
           join fetch r.fieldList f
           join fetch r.recruitmentTechStackList t
           join fetch t.technologyStack
           left join fetch p.image
           where p.id = :id
           """)
    Optional<Post> findPostWithField(@Param("id") Long id);

    @Query("select p from Post p left join fetch p.image where p.category != :category and p.recruitmentInfo.status = 'open' order by p.createdAt limit 10")
    List<Post> getHome(@Param("category") Category category);
}
