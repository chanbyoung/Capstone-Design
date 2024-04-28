package durikkiri.project.entity.post;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    PROJECT("프로젝트"), STUDY("스터디"), GENERAL("자유글");
    private final String value;

}
