package durikkiri.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import durikkiri.project.entity.Post;
import durikkiri.project.entity.dto.post.PostSearchContent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static durikkiri.project.entity.QPost.*;

@Repository
@RequiredArgsConstructor
public class DslPostRepository {
    private final JPAQueryFactory query;

    public Page<Post> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        BooleanBuilder builder = new BooleanBuilder();

        if (postSearchContent != null) {
            builder.and(post.title.contains(postSearchContent.getTitle()));
        }

        List<Post> posts = query.select(post)
                .from(post)
                .where(builder)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct().fetch();
        Long count = query.select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(posts,pageable,count);
    }
}
