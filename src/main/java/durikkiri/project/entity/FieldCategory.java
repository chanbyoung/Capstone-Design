package durikkiri.project.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FieldCategory {
    FRONTEND("프론트엔드"), BACKEND("백엔드"), GENERAL("일반 참여자");

    private final String value;
    }

