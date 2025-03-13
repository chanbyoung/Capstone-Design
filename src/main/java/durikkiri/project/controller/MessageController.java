package durikkiri.project.controller;

import durikkiri.project.annotation.AuthUser;
import durikkiri.project.dto.message.ConversationGetDto;
import durikkiri.project.dto.message.ConversationRequestDto;
import durikkiri.project.dto.message.ConversationsGetDto;
import durikkiri.project.dto.message.MessageCreateDto;
import durikkiri.project.dto.message.MessageDto;
import durikkiri.project.dto.message.MessageUpdateDto;
import durikkiri.project.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/conversation")
    public ResponseEntity<List<ConversationsGetDto>> getAllConversation(@AuthUser Long memberId) {
        List<ConversationsGetDto> conversationFromMember = messageService.getConversationFromMember(memberId);
        return ResponseEntity.ok(conversationFromMember);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ConversationGetDto> getConversation(@PathVariable Long conversationId,
            @AuthUser Long memberId) {
        ConversationGetDto conversation = messageService.getConversation(conversationId, memberId);
        log.info("{}", conversation.getId());
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/conversation")
    public ResponseEntity<ConversationGetDto> createOrRetrieveConversation(
            @RequestBody ConversationRequestDto conversationRequestDto,
            @AuthUser Long memberId) {
        log.info("postId={}, receiverId={}", conversationRequestDto.getPostId(),
                conversationRequestDto.getReceiverId());
        return ResponseEntity.ok(
                messageService.createOrRetrieveConversation(conversationRequestDto, memberId));
    }


    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessageCreateDto messageCreateDto,
            @AuthUser Long memberId) {
        messageService.sendMessage(messageCreateDto, memberId);
        return ResponseEntity.status(CREATED).body("Message sent successfully");
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable Long messageId) {
        MessageDto message = messageService.getMessage(messageId);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<String> updateMessage(@PathVariable Long messageId,
            @RequestBody MessageUpdateDto messageUpdateDto,
            @AuthUser Long memberId) {
        messageService.updateMessage(messageId, messageUpdateDto, memberId);
        return ResponseEntity.ok("Message updated successfully");
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId,
            @AuthUser Long memberId) {
        messageService.deleteMessage(messageId, memberId);
        return ResponseEntity.ok("Message deleted successfully");
    }
}
