package durikkiri.project.service.impl;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.Message;
import durikkiri.project.entity.dto.message.*;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.ForbiddenException;
import durikkiri.project.exception.NotFoundException;
import durikkiri.project.repository.ConversationRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.MessageRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;


    @Override
    public List<ConversationGetsDto> getConversationFromMember() {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        return conversationRepository.findByConversation(member).stream()
                .map(conversation -> ConversationGetsDto.toDto(conversation,member))
                .collect(Collectors.toList());
    }

    @Override
    public ConversationGetDto getConversation(Long conversationId) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        log.info("{}", member.getId());
        Conversation findConversation = conversationRepository.findByIdWithMessage(conversationId, member.getId())
                .orElseThrow(() -> new ForbiddenException("Conversation not found"));

        Long opponentId = findConversation.getMember1().getId().equals(member.getId())
                ? findConversation.getMember2().getId()
                : findConversation.getMember1().getId();

        return ConversationGetDto.toDto(findConversation, opponentId);
    }

    @Override
    @Transactional
    public ConversationGetDto createOrRetrieveConversation(ConversationRequestDto conversationRequestDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member sender = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Member receiver = memberRepository.findById(conversationRequestDto.getReceiverId())
                .orElseThrow(() -> new ForbiddenException("Receiver not found"));
        Post post = postRepository.findById(conversationRequestDto.getPostId())
                .orElseThrow(() -> new ForbiddenException("Post not found"));
        Conversation conversation = conversationRepository.findByMember1OrMember2(sender, receiver, post)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.toEntity(sender, receiver, post);
                    conversationRepository.save(newConversation);
                    return newConversation;
                });
        return ConversationGetDto.toDto(conversation, conversationRequestDto.getReceiverId());
    }

    @Override
    @Transactional
    public void sendMessage(MessageCreateDto messageCreateDto) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member sender = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        Member receiver = memberRepository.findById(messageCreateDto.getReceiverId())
                .orElseThrow(() -> new ForbiddenException("Receiver not found"));
        Post post = postRepository.findById(messageCreateDto.getPostId())
                .orElseThrow(() -> new ForbiddenException("Post not found"));
        Conversation conversation = conversationRepository.findByMember1OrMember2(sender, receiver, post)
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.toEntity(sender, receiver, post);
                    conversationRepository.save(newConversation);
                    return newConversation;
                });

        Message message = Message.builder()
                .content(messageCreateDto.getContent())
                .sender(sender)
                .conversation(conversation)
                .build();

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
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getSender().getId().equals(member.getId())) {
            throw new ForbiddenException("You do not have permission to update this message");
        }
        message.updateContent(messageUpdateDto.getContent());
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new ForbiddenException("User not found"));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getSender().getId().equals(member.getId())) {
            throw new ForbiddenException("You do not have permission to delete this message");
        }
        memberRepository.deleteById(messageId);
    }
}
