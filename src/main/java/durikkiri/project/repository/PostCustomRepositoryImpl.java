package durikkiri.project.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import durikkiri.project.entity.*;
import durikkiri.project.entity.post.Category;
import durikkiri.project.entity.post.Post;
import durikkiri.project.dto.post.PostSearchContent;
import durikkiri.project.entity.post.QTechnologyStack;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.util.CollectionUtils;

import static durikkiri.project.entity.QApply.*;
import static durikkiri.project.entity.QImage.*;
import static durikkiri.project.entity.post.Category.*;
import static durikkiri.project.entity.post.QPost.post;
import static durikkiri.project.entity.post.QRecruitmentInfo.*;
import static durikkiri.project.entity.post.QTechnologyStack.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class PostCustomRepositoryImpl implements PostCustomRepository {

    private static final String OPEN = "open";

    private final JPAQueryFactory query;
    @Override
    public Page<Post> getPostsByOffset(Pageable pageable, PostSearchContent postSearchContent) {
        BooleanExpression condition = buildSearchConditionForOffset(postSearchContent);

        OrderSpecifier<?> orderSpecifier =
                (postSearchContent.getCreatedByAsc() != null && postSearchContent.getCreatedByAsc())
                        ? post.createdAt.asc()
                        : post.createdAt.desc();

        List<Post> posts = query.selectFrom(post)
                .distinct() // 조인으로 인한 중복 제거
                .leftJoin(post.image, image).fetchJoin()
                .join(post.recruitmentInfo, recruitmentInfo).fetchJoin()
                .join(recruitmentInfo.technologyStackList, technologyStack).fetchJoin()
                .where(condition)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> count = query.select(post.count())
                .from(post)
                .where(condition);

        return PageableExecutionUtils.getPage(posts, pageable, count::fetchOne);
    }

    private BooleanExpression buildSearchConditionForOffset(PostSearchContent content) {
        if (content == null) {
            return null;
        }

        // 기본 조건: 항상 true인 조건으로 시작
        BooleanExpression predicate = Expressions.asBoolean(true).isTrue();

        if (!content.getWithClosed()) {
            predicate.and(post.recruitmentInfo.status.eq(OPEN));
        }
        if (content.getCategory() != null) {
            predicate = predicate.and(post.category.eq(content.getCategory()));
        }
        if (content.getTitle() != null) {
            predicate = predicate.and(post.title.contains(content.getTitle()));
        }
        if (!CollectionUtils.isEmpty(content.getTechnologyStackList())) {
            predicate = predicate.and(
                    post.recruitmentInfo.technologyStackList.any()
                            .technologyStack.name.in(content.getTechnologyStackList())
            );
        }
        return predicate;
    }
    @Override
    public List<Post> getLikePostList(Category category) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.category.eq(category));
        builder.and(post.recruitmentInfo.status.eq(OPEN));
        return query.select(post)
                .from(post)
                .leftJoin(post.image, image)
                .fetchJoin()
                .leftJoin(post.recruitmentInfo, recruitmentInfo)
                .fetchJoin()
                .where(builder)
                .orderBy(post.likeCount.desc())
                .orderBy(post.viewCount.desc())
                .limit(10)
                .fetch();
    }

    //마이페이지에서 현재 진행중인 프로젝트/스터디를 찾는 로직
    @Override
    public List<Post> progressProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        addCondition(member, builder);

        return query.select(post)
                .from(post)
                .join(post.recruitmentInfo, recruitmentInfo)
                .fetchJoin()
                .where(builder)
                .fetch();
    }

    private static void addCondition(Member member, BooleanBuilder builder) {
        builder.and(post.category.notIn(GENERAL));
        builder.and(apply.createdBy.eq(member.getNickname()));
        builder.and(apply.applyStatus.eq(ApplyStatus.ACCEPT));
    }

    @Override
    public List<Post> myRecruitingProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.member.eq(member));
        builder.and(post.category.notIn(GENERAL));
        return query.select(post)
                .from(post)
                .leftJoin(post.member, QMember.member)
                .fetchJoin()
                .where(builder)
                .fetch();
    }
    @Override
    public List<Post> myApplyProject(Member member) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(
                apply.member.eq(member)
                        .and(post.category.notIn(GENERAL))
                        .and(apply.applyStatus.eq(ApplyStatus.READ)
                                .or(apply.applyStatus.eq(ApplyStatus.UNREAD))
                        )
        );

        return query.select(post)
                .from(post)
//                .join(post.appliesList, apply).fetchJoin()
                .join(apply.member, QMember.member).fetchJoin()
                .where(apply.member.eq(member))
                .distinct()
                .fetch();
    }
}
