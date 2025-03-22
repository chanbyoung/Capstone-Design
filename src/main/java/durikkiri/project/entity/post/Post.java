package durikkiri.project.entity.post;

import durikkiri.project.dto.post.PostAddDto;
import durikkiri.project.entity.BaseEntity;
import durikkiri.project.entity.Image;
import durikkiri.project.entity.Member;
import durikkiri.project.dto.post.PostUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Post extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String title;

    private String content;

    private Long viewCount;

    private Long likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Image image;

    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private RecruitmentInfo recruitmentInfo;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public void updatePost(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
    }

    public void updateViewCount() {
        this.viewCount ++;
    }

    public void updateLikeCount(Boolean flag) {
        if (flag) {
            this.likeCount ++;
            return;
        }
        this.likeCount --;
    }

    public void updateComment(Comment comment) {
        this.commentList.add(comment);
    }


    public static Post of(PostAddDto postAddDto, Member member) {
        return Post.builder()
                .title(postAddDto.getTitle())
                .category(postAddDto.getCategory())
                .member(member)
                .commentList(new ArrayList<>())
                .content(postAddDto.getContent())
                .likeCount(0L)
                .viewCount(0L)
                .build();
    }
}
