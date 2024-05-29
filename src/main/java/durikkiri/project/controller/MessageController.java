package durikkiri.project.controller;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.dto.message.*;
import durikkiri.project.service.MessageService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/conversation")
    public ResponseEntity<List<ConversationGetsDto>> getAllConversation() {
        List<ConversationGetsDto> conversationFromMember = messageService.getConversationFromMember();
        return ResponseEntity.ok(conversationFromMember);
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ConversationGetDto> getConversation(@PathVariable Long conversationId) {
        ConversationGetDto conversation = messageService.getConversation(conversationId);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping
    public ResponseEntity<String> sendMessage(@RequestBody MessageCreateDto messageCreateDto) {
        messageService.sendMessage(messageCreateDto);
        return ResponseEntity.status(CREATED).body("Message sent successfully");
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<MessageDto> getMessage(@PathVariable Long messageId) {
        MessageDto message = messageService.getMessage(messageId);
        return ResponseEntity.ok(message);
    }

    @PatchMapping("/{messageId}")
    public ResponseEntity<String> updateMessage(@PathVariable Long messageId, @RequestBody MessageUpdateDto messageUpdateDto) {
        messageService.updateMessage(messageId, messageUpdateDto);
        return ResponseEntity.ok("Message updated successfully");
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        return ResponseEntity.ok("Message deleted successfully");
    }
}
