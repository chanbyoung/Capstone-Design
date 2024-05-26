package durikkiri.project.repository;

import durikkiri.project.entity.Member;
import durikkiri.project.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiver(Member receiver);

}
