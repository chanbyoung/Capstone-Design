package durikkiri.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import durikkiri.project.entity.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.dto.post.PostSearchContent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.List;

import static durikkiri.project.entity.QApply.*;
import static durikkiri.project.entity.QImage.*;
import static durikkiri.project.entity.post.Category.*;
import static durikkiri.project.entity.post.QPost.post;
import static durikkiri.project.entity.post.RecruitmentStatus.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DslPostRepository {
    private final JPAQueryFactory query;

    public Slice<Post> getPosts(Pageable pageable, PostSearchContent postSearchContent) {
        BooleanBuilder builder = searchCondition(postSearchContent);

        List<Post> posts = query.select(post)
                .from(post)
                .leftJoin(post.image, image)
                .fetchJoin()
                .where(builder)
                .orderBy(post.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }
        log.info("dslpostRepository.hasNext = {}" , hasNext);
        return new SliceImpl<>(posts, pageable, hasNext);
    }

    private static BooleanBuilder searchCondition(PostSearchContent postSearchContent) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.status.eq(Y));
        if (postSearchContent != null) {
            if (postSearchContent.getCategory() != null) {
                builder.and(post.category.eq(postSearchContent.getCategory()));
            }
            if (postSearchContent.getTitle() != null) {
                builder.and(post.title.contains(postSearchContent.getTitle()));
            }
            if (postSearchContent.getCursorCreatedAt() != null) {
                builder.and(post.createdAt.lt(postSearchContent.getCursorCreatedAt()));
            }
        }
        return builder;
    }

    public List<Post> getLikePostList(Category category) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.category.eq(category));
        builder.and(post.status.eq(Y));
        return query.select(post)
                .from(post)
                .leftJoin(post.image, image)
                .fetchJoin()
                .where(builder)
                .orderBy(post.likeCount.desc())
                .orderBy(post.viewCount.desc())
                .limit(10)
                .fetch();
    }
    //마이페이지에서 현재 진행중인 프로젝트/스터디를 찾는 로직
    public List<Post> progressProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        addCondition(member, builder);

        return query.select(post)
                .from(post)
                .leftJoin(post.appliesList, apply)
                .fetchJoin()
                .where(builder)
                .fetch();
    }

    private static void addCondition(Member member, BooleanBuilder builder) {
        builder.and(post.category.notIn(GENERAL));
        builder.and(apply.createdBy.eq(member.getNickname()));
        builder.and(apply.applyStatus.eq(ApplyStatus.ACCEPT));
    }

    public List<Post> myRecruitingProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.member.eq(member));
        builder.and(post.status.eq(Y));
        builder.and(post.category.notIn(GENERAL));
        return query.select(post)
                .from(post)
                .leftJoin(post.member, QMember.member)
                .fetchJoin()
                .where(builder)
                .fetch();
    }

    public List<Post> myApplyProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(apply.member.eq(member));
        builder.and(post.category.notIn(GENERAL));
        builder.or(apply.applyStatus.eq(ApplyStatus.READ)).
                or(apply.applyStatus.eq(ApplyStatus.UNREAD));

        return query.select(post)
                .from(post)
                .join(post.appliesList, apply).fetchJoin()
                .join(apply.member, QMember.member).fetchJoin()
                .where(apply.member.eq(member))
                .distinct()
                .fetch();
    }
}
