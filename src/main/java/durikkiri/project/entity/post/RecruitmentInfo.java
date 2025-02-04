package durikkiri.project.entity.post;

import durikkiri.project.entity.Apply;
import durikkiri.project.entity.dto.post.FieldDto;
import durikkiri.project.entity.dto.post.PostUpdateDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_info_id")
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String status;

    @OneToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "recruitmentInfo", cascade = CascadeType.PERSIST , orphanRemoval = true)
    private List<RecruitmentTechStack> recruitmentTechStackList;

    @OneToMany(mappedBy = "recruitmentInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Field> fieldList;

    @OneToMany(mappedBy = "recruitmentInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Apply> appliesList;

    @Builder
    public RecruitmentInfo(Long id, LocalDate startDate, LocalDate endDate, String status,
            Post post) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.post = post;
        this.recruitmentTechStackList = new ArrayList<>();
        this.fieldList = new ArrayList<>();
        this.appliesList = new ArrayList<>();
    }

    public void updateStatus() {
        boolean recruitmentDeadline = this.fieldList.stream()
                .allMatch(field ->
                        field.getCurrentRecruitment() == field.getTotalRecruitment());
        boolean dateDeadline= endDate.isBefore(LocalDate.now());
        if (recruitmentDeadline || dateDeadline) {
            // 모든 field의 currentRecruitment와 totalRecruitment가 같은 경우
            // Post 엔티티 업데이트 로직 실행
            this.status = "closed";
        } else {
            this.status = "open";
        }
    }

    public void updateRecuruitmentInfo(PostUpdateDto postUpdateDto) {
//        this.technologySkillList = postUpdateDto.getTechnologyStackList();
        this.startDate = postUpdateDto.getStartDate();
        this.endDate = postUpdateDto.getEndDate();
        fieldUpdate(postUpdateDto);
    }

    private void fieldUpdate(PostUpdateDto postUpdateDto) {
        Set<String> processedFieldCategories = new HashSet<>();
        Map<String, FieldDto> fieldDtoMap = postUpdateDto.getFieldList().stream().collect(
                Collectors.toMap(FieldDto::getFieldCategory, Function.identity()));

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

    public void updateFiledList(List<Field> addFieldList) {
        this.fieldList = addFieldList;
    }
}
