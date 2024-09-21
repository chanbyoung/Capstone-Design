package durikkiri.project.service;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.post.Like;
import durikkiri.project.entity.post.Post;
import durikkiri.project.exception.NotFoundException;
import durikkiri.project.repository.LikeRepository;
import durikkiri.project.repository.MemberRepository;
import durikkiri.project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    @Transactional
    public void toggleLike(Long postId) {
        String memberLoginId = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Like> existingLike = likeRepository.findByPostIdAndMemberId(postId, memberLoginId);

        if (existingLike.isPresent()) {
            Like like = existingLike.get();
            likeRepository.delete(like);
            like.getPost().updateLikeCount(false);
            return;
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        Member member = memberRepository.findByLoginId(memberLoginId)
                .orElseThrow(() -> new NotFoundException("Member not found"));

        // 새로운 Like 엔티티 생성 후 저장
        Like newLike = Like.toEntity(member, post);
        likeRepository.save(newLike);
        post.updateLikeCount(true);
    }
}
