package durikkiri.project.entity.post;

import durikkiri.project.dto.post.RecruitmentAddDto;
import durikkiri.project.entity.Apply;
import durikkiri.project.dto.post.FieldDto;
import durikkiri.project.dto.post.PostUpdateDto;
import durikkiri.project.exception.BadRequestException;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "recruitment_info_id")
    private List<TechnologyStack> technologyStackList;

    @OneToMany(mappedBy = "recruitmentInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Field> fieldList;

    @OneToMany(mappedBy = "recruitmentInfo", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Apply> appliesList;

    public static RecruitmentInfo of(RecruitmentAddDto recruitmentAddDto, Post post,
            List<TechnologyStack> technologyStackList) {
        if (recruitmentAddDto.getStartDate().isAfter(recruitmentAddDto.getEndDate())) {
            throw new BadRequestException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }

        RecruitmentInfo recruitmentInfo = RecruitmentInfo.builder()
                .startDate(recruitmentAddDto.getStartDate())
                .endDate(recruitmentAddDto.getEndDate())
                .status("open")
                .post(post)
                .build();

        List<Field> addFieldList = recruitmentAddDto.getFieldList().stream()
                .map(filed -> filed.toEntity(recruitmentInfo))
                .collect(Collectors.toMap(Field::getFieldCategory, Function.identity(),
                        (existing, replacement) -> existing)).values()
                .stream()
                .toList();

        recruitmentInfo.updateList(addFieldList, technologyStackList);

        return recruitmentInfo;
    }

    public void updateStatus() {
        boolean recruitmentDeadline = this.fieldList.stream()
                .allMatch(field ->
                        field.getCurrentRecruitment() == field.getTotalRecruitment());
        if (recruitmentDeadline) {
            // 모든 field의 currentRecruitment와 totalRecruitment가 같은 경우
            this.status = "closed";
        } else {
            this.status = "open";
        }
    }

    public void updateRecuruitmentInfo(PostUpdateDto postUpdateDto, List<TechnologyStack> technologyStackList) {
        this.startDate = postUpdateDto.getStartDate();
        this.endDate = postUpdateDto.getEndDate();
        fieldUpdate(postUpdateDto);
        technologyStackUpdate(technologyStackList);
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

    private void technologyStackUpdate(List<TechnologyStack> newTechnologyStackList) {
        // 새로운 기술 스택을 id 기준으로 매핑
        Map<Long, TechnologyStack> newTechMap = newTechnologyStackList.stream()
                .collect(Collectors.toMap(TechnologyStack::getId, Function.identity()));

        // 기존 기술 스택 중, 새로운 목록에 포함되지 않는 항목을 제거
        Set<Long> processedTechIds = new HashSet<>();
        this.technologyStackList.removeIf(existingTech -> {
            Long techId = existingTech.getId();
            if (newTechMap.containsKey(techId)) {
                processedTechIds.add(techId);
                return false;
            }
            return true; // 새로운 목록에 없으면 제거
        });

        // 새로운 기술 스택 중 기존에 없던 항목 추가
        newTechMap.keySet().stream()
                .filter(techId -> !processedTechIds.contains(techId))
                .forEach(techId -> this.technologyStackList.add(newTechMap.get(techId)));
    }

    public void updateList(List<Field> addFieldList, List<TechnologyStack> technologyStackList) {
        this.fieldList = addFieldList;
        this.technologyStackList = technologyStackList;
    }
}
