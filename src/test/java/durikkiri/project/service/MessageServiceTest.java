package durikkiri.project.service;

import durikkiri.project.entity.Conversation;
import durikkiri.project.entity.Member;
import durikkiri.project.entity.Message;
import durikkiri.project.entity.dto.message.*;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.BadRequestException;
import durikkiri.project.exception.ForbiddenException;
import durikkiri.project.repository.ConversationRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.MessageRepository;
import durikkiri.project.repository.PostRepository;
import durikkiri.project.service.impl.MessageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @InjectMocks
    private MessageServiceImpl messageServiceImpl;
    @Mock
    private PostRepository postRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ConversationRepository conversationRepository;

    private MessageService messageService;
    private final String testUser = "TESTUSER";
    private Member member;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        messageService = messageServiceImpl;
        SecurityContextHolder.setContext(securityContext);

        member = Member.builder()
                .id(1L)
                .loginId(testUser)
                .password("testPassword")
                .username(testUser)
                .build();
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn(testUser);
        lenient().when(memberRepository.findByLoginId(testUser)).thenReturn(Optional.ofNullable(member));
    }



    @Test
    void sendMessage() {
        // given
        MessageCreateDto messageCreateDto = new MessageCreateDto();
        messageCreateDto.setPostId(1L);
        messageCreateDto.setReceiverId(2L);
        messageCreateDto.setContent("testContent");
        Member receiverMember = mock(Member.class);
        Post mockPost = mock(Post.class);

        when(memberRepository.findByLoginId(member.getLoginId())).thenReturn(Optional.of(member));
        when(memberRepository.findById(messageCreateDto.getReceiverId())).thenReturn(Optional.of(receiverMember));
        when(postRepository.findById(messageCreateDto.getPostId())).thenReturn(Optional.of(mockPost));
        when(conversationRepository.findByMember1OrMember2(member, receiverMember, mockPost)).thenReturn(Optional.of(mock(Conversation.class)));

        // when
        messageService.sendMessage(messageCreateDto);

        // then
        verify(messageRepository, times(1)).save(any());
    }

    @Test
    void getMessage() {
        //given
        Message message = Message.builder()
                .id(1L)
                .sender(mock(Member.class))
                .conversation(mock(Conversation.class))
                .content("testContent")
                .build();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        //when
        MessageDto messageDto = messageService.getMessage(message.getId());

        //then
        assertThat(message.getId()).isEqualTo(messageDto.getId());
    }

    @Test
    void updateMessage() {
        //given
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto();
        messageUpdateDto.setContent("updateContent");

        Message message = Message.builder()
                .id(1L)
                .sender(member)
                .conversation(mock(Conversation.class))
                .content("testContent")
                .build();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        //when
        messageService.updateMessage(message.getId(), messageUpdateDto);

        //then
        assertThat(message.getContent()).isEqualTo(messageUpdateDto.getContent());
    }

    @Test
    void failUpdateMessage() {
        //given
        MessageUpdateDto messageUpdateDto = new MessageUpdateDto();
        messageUpdateDto.setContent("updateContent");

        Message message = Message.builder()
                .id(1L)
                .sender(mock(Member.class))
                .conversation(mock(Conversation.class))
                .content("testContent")
                .build();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        //when
        ForbiddenException exception = assertThrows(ForbiddenException.class, () ->
                messageService.updateMessage(message.getId(), messageUpdateDto));

        //then
        assertEquals(exception.getMessage(), "You do not have permission to update this message");
    }

    @Test
    void deleteMessage() {
        //given
        Message message = Message.builder()
                .id(1L)
                .sender(member)
                .conversation(mock(Conversation.class))
                .content("testContent")
                .build();
        when(messageRepository.findById(message.getId())).thenReturn(Optional.of(message));

        //when
         messageService.deleteMessage(message.getId());

         //then
        verify(messageRepository, times(1)).deleteById(message.getId());
    }

    @Test
    void getConversationFromMember() {
        //given
        Conversation conversation = Conversation.builder()
                .id(1L)
                .member1(member)
                .member2(mock(Member.class))
                .post(mock(Post.class))
                .messages(List.of(mock(Message.class)))
                .build();
        List<Conversation> conversationList = List.of(conversation);
        when(conversationRepository.findByConversation(member)).thenReturn(conversationList);

        //when
        List<ConversationsGetDto> conversationDtoList = messageService.getConversationFromMember();

        //then
        assertThat(conversationDtoList.get(0).getId()).isEqualTo(conversation.getId());
        assertThat(conversationDtoList.size()).isEqualTo(1);
    }

    @Test
    void getConversation() {
        //given
        Member receiverMember = Member.builder()
                .id(2L)
                .build();
        Message mockMessage = mock(Message.class);
        Conversation conversation = Conversation.builder()
                .id(1L)
                .member1(member)
                .member2(receiverMember)
                .post(mock(Post.class))
                .messages(List.of(mockMessage))
                .build();

        when(conversationRepository.findByIdWithMessage(conversation.getId(),member.getId())).thenReturn(Optional.of(conversation));
        when(mockMessage.getSender()).thenReturn(member);
        //when
        ConversationGetDto conversationDto = messageService.getConversation(conversation.getId());

        //then
        assertThat(conversationDto.getId()).isEqualTo(conversation.getId());
        assertThat(conversationDto.getOpponentId()).isNotEqualTo(member.getId());
    }

    @Test
    void createOrRetrieveConversation() {
        //given
        Member receiverMember = Member.builder()
                .id(2L)
                .build();
        ConversationRequestDto conversationRequestDto = new ConversationRequestDto();
        conversationRequestDto.setPostId(1L);
        conversationRequestDto.setReceiverId(receiverMember.getId());

        when(memberRepository.findById(conversationRequestDto.getReceiverId())).thenReturn(Optional.of(receiverMember));
        when(postRepository.findById(conversationRequestDto.getPostId())).thenReturn(Optional.of(mock(Post.class)));

        //when
        ConversationGetDto newConversationDto = messageService.createOrRetrieveConversation(conversationRequestDto);

        //then
        assertThat(newConversationDto.getOpponentId()).isEqualTo(receiverMember.getId());
        verify(conversationRepository, times(1)).save(any());
    }

    /**
     * 자기 자신과 채팅방 생성을 하려고 한 경우
     */
    @Test
    void failCreateOrRetrieveConversation() {
        //given
        ConversationRequestDto conversationRequestDto = new ConversationRequestDto();
        conversationRequestDto.setReceiverId(member.getId());

        //when
        BadRequestException exception = assertThrows(BadRequestException.class, () ->
                messageService.createOrRetrieveConversation(conversationRequestDto));

        //then
        assertEquals("자기 자신과의 채팅방 생성은 불가능합니다", exception.getMessage());
    }
}