package durikkiri.project.service.impl;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.Message;
import durikkiri.project.entity.dto.message.MessageCreateDto;
import durikkiri.project.entity.dto.message.MessageDto;
import durikkiri.project.entity.dto.message.MessageUpdateDto;
import durikkiri.project.exception.ForbiddenException;
import durikkiri.project.exception.NotFoundException;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.MessageRepository;
import durikkiri.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService {
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<MessageDto> getMessagesForMember() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));;
        return messageRepository.findByReceiver(member).stream()
                .map(MessageDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendMessage(MessageCreateDto messageCreateDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member sender = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Member receiver = memberRepository.findById(messageCreateDto.getReceiverId())
                .orElseThrow(() -> new ForbiddenException("Receiver not found"));

        Message message = Message.toEntity(sender, receiver, messageCreateDto.getContent());
        messageRepository.save(message);
    }

    @Override
    public MessageDto getMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message not found"));
        return MessageDto.toDto(message);
    }

    @Override
    @Transactional
    public void updateMessage(Long messageId, MessageUpdateDto messageUpdateDto) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
        message.updateContent(messageUpdateDto.getContent());
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        memberRepository.deleteById(messageId);
    }
}
