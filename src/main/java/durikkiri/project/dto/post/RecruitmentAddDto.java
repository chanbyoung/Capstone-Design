package durikkiri.project.dto.post;

import durikkiri.project.entity.post.Field;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentInfo;
import durikkiri.project.entity.post.TechnologyStack;
import durikkiri.project.exception.BadRequestException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentAddDto {

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> technologyStackList;

    private List<FieldDto> fieldList;

    public RecruitmentInfo toEntity(Post post, List<TechnologyStack> technologyStackList) {
        if (this.startDate.isAfter(this.endDate)) {
            throw new BadRequestException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
        }

        RecruitmentInfo recruitmentInfo = RecruitmentInfo.builder()
                .startDate(this.startDate)
                .endDate(this.endDate)
                .status("open")
                .post(post)
                .build();

        List<Field> addFieldList = fieldList.stream()
                .map(filed -> filed.toEntity(recruitmentInfo))
                .collect(Collectors.toMap(Field::getFieldCategory, Function.identity(),
                        (existing, replacement) -> existing)).values()
                .stream()
                .toList();

        recruitmentInfo.updateList(addFieldList, technologyStackList);

        return recruitmentInfo;
    }

}
