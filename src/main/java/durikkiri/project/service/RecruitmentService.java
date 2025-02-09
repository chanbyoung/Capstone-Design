package durikkiri.project.service;

import durikkiri.project.entity.dto.post.FieldDto;
import durikkiri.project.entity.dto.post.RecruitmentAddDto;
import durikkiri.project.entity.post.Post;
import durikkiri.project.entity.post.RecruitmentInfo;
import durikkiri.project.entity.post.RecruitmentTechStack;
import durikkiri.project.entity.post.TechnologyStack;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.repository.RecruitmentInfoTechStackRepository;
import durikkiri.project.repository.RecruitmentRepository;
import durikkiri.project.repository.TechnologyStackRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final TechnologyStackRepository technologyStackRepository;
    private final RecruitmentInfoTechStackRepository recruitmentInfoTechStackRepository;
    private final Validator validator;

    /**
     * 모집 정보를 처리합니다.
     */
    public void processRecruitmentInfo(RecruitmentAddDto recruitmentAddDto, Post post) {
        // 필드 리스트 유효성 검사
        checkFieldValid(recruitmentAddDto.getFieldList());

        // 기술 스택 리스트 처리 (조회 후 존재하지 않는 경우 새로 생성)
        List<TechnologyStack> technologyStacks = getOrCreateTechnologyStacks(
                recruitmentAddDto.getTechnologyStackList());

        // 모집 정보 저장
        RecruitmentInfo savedRecruitment = recruitmentRepository.save(
                recruitmentAddDto.toEntity(post));
        log.info("Recruitment info created for post id: {}", post.getId());

        // 매핑 테이블 저장
        saveRecruitmentInfoTechStack(technologyStacks, savedRecruitment);
    }


    /**
     * 모집 정보에 포함된 Field 리스트의 유효성을 검사합니다.
     */
    public void checkFieldValid(List<FieldDto> fields) {
        if (fields == null || fields.isEmpty()) {
            throw new BadRequestException("Field list is empty for non-general category");
        }
        for (FieldDto field : fields) {
            Set<ConstraintViolation<FieldDto>> violations = validator.validate(field);
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new BadRequestException("Field validation failed: " + errorMessage);
            }
        }
    }


    private void saveRecruitmentInfoTechStack(List<TechnologyStack> saveTechnologyStacks,
            RecruitmentInfo saveRecruitment) {
        List<RecruitmentTechStack> saveList = saveTechnologyStacks.stream()
                .map(techStack -> RecruitmentTechStack.builder()
                        .recruitmentInfo(saveRecruitment)
                        .technologyStack(techStack)
                        .build())
                .toList();

        recruitmentInfoTechStackRepository.saveAll(saveList);
    }

    /**
     * 기술 스택 리스트를 받아 DB에 존재하는 스택은 조회하고, 존재하지 않는 경우 새 엔티티를 생성 및 저장한 후 전체 리스트를 반환합니다.
     */
    public List<TechnologyStack> getOrCreateTechnologyStacks(List<String> technologyStackNames) {
        // DB에 존재하는 기술 스택 조회
        List<TechnologyStack> existingStacks = technologyStackRepository.findByNameIn(
                technologyStackNames);
        Set<String> existingNames = existingStacks.stream()
                .map(TechnologyStack::getName)
                .collect(Collectors.toSet());

        // 존재하지 않는 기술 스택을 찾아서 새 엔티티 생성
        List<TechnologyStack> newStacks = technologyStackNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(TechnologyStack::new)
                .collect(Collectors.toList());

        // 새 엔티티가 있다면 DB에 저장 후 전체 리스트에 추가
        if (!newStacks.isEmpty()) {
            List<TechnologyStack> savedStacks = technologyStackRepository.saveAll(newStacks);
            existingStacks.addAll(savedStacks);
            log.info("Created new TechnologyStacks: {}", savedStacks);
        }
        return existingStacks;
    }
}
