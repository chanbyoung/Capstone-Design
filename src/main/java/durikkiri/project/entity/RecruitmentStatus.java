package durikkiri.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public enum RecruitmentStatus {
    Y("모집중"),N("모집 완료");
    private final String value;

}
