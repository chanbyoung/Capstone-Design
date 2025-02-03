package durikkiri.project.entity.post;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.BaseEntity;
import durikkiri.project.entity.Image;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.dto.post.FieldDto;
import durikkiri.project.entity.dto.post.PostUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @OneToOne(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private RecuruitmentInfo recuruitmentInfo;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Comment> commentList;

    public void updatePost(PostUpdateDto postUpdateDto) {
        this.title = postUpdateDto.getTitle();
        this.content = postUpdateDto.getContent();
        this.retechnologyStackList =postUpdateDto.getTechnologyStackList();
        this.startDate = postUpdateDto.getStartDate();
        this.endDate = postUpdateDto.getEndDate();
        if (!postUpdateDto.getCategory().equals(Category.GENERAL)) { //일반글일 경우 필드 수정 로직 실행 안함
            fieldUpdate(postUpdateDto);
        }
    }

    private void fieldUpdate(PostUpdateDto postUpdateDto) {
        Set<String> processedFieldCategories = new HashSet<>();
        Map<String, FieldDto> fieldDtoMap = postUpdateDto.getFieldList().stream().collect(Collectors.toMap(FieldDto::getFieldCategory, Function.identity()));

        fieldList.removeIf(field -> {
            String fieldCategory = field.getFieldCategory();
            FieldDto fieldDto = fieldDtoMap.get(fieldCategory);

            // 일치하는 카테고리가 있어 업데이트 할 필드 목록이 존재하는 경우
            if (fieldDto != null) {
                field.updateField(fieldDto);
                processedFieldCategories.add(fieldCategory);
                return false;
            }
            return true; // 일치하는 카테고리가 없으면 삭제
        } );

        //새로 추가해야 할 필드 추가
        fieldDtoMap.keySet().stream()
                .filter(fieldCategory -> !processedFieldCategories.contains(fieldCategory))
                .forEach(fieldCategory -> {
                    Field newField = fieldDtoMap.get(fieldCategory).toEntity(this);
                    this.fieldList.add(newField);
                });
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

}
