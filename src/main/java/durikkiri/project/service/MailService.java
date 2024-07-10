package durikkiri.project.service;

import durikkiri.project.entity.dto.ExistDto;
import durikkiri.project.entity.dto.FindDto;
import durikkiri.project.entity.dto.MailDto;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "pcb7893@naver.com";
    private final Map<String, String> verificationCodes = new HashMap<>();
    private final MemberRepository memberRepository;

    public String generateVerificationCode() {
        Random random = new Random();
        int verificationCode = 100000 + random.nextInt(900000); // 6자리 인증 코드 생성
        return String.valueOf(verificationCode);
    }

    public void sendVerificationCode(String toEmail) {
        boolean flag = memberRepository.existsByEmail(toEmail);
        if (flag) {
            throw new BadRequestException("이미 가입한 이력이 있는 이메일 입니다.");
        }
        sendVerificationEmail(toEmail);
    }

    private void sendVerificationEmail(String toEmail) {
        String verificationCode = generateVerificationCode();
        verificationCodes.put(toEmail, verificationCode); // 인증 코드를 저장

        MailDto mailDto = new MailDto();
        mailDto.setAddress(toEmail);
        mailDto.setSubject("두리끼리 인증 번호입니다.");
        mailDto.setMessage("인증번호는 : " + verificationCode);

        sendMail(mailDto);
    }

    public boolean verifyCode(ExistDto existDto) {
        String storedCode = verificationCodes.get(existDto.getEmail());
        return storedCode != null && storedCode.equals(existDto.getCode());
    }

    public void sendMail(MailDto mailDto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDto.getAddress());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(mailDto.getSubject());
        message.setText(mailDto.getMessage());
        mailSender.send(message);
    }

    public void existsMemberSendVerificationCode(FindDto findDto) {
        if (findDto.getLoginId().isEmpty()) {
            boolean flag = memberRepository.existsByEmailAndUsername(findDto.getEmail(), findDto.getUsername());
            if (!flag) {
                throw new BadRequestException("존재하지 않는 회원입니다.");
            }
        } else {
            boolean flag = memberRepository.existsByEmailAndUsernameAndLoginId(findDto.getEmail(), findDto.getUsername(), findDto.getLoginId());
            if (!flag) {
                throw new BadRequestException("존재하지 않는 회원입니다.");
            }
        }
        sendVerificationEmail(findDto.getEmail());
    }

}
