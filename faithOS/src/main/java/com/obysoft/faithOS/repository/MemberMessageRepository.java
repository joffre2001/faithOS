package com.obysoft.faithOS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.obysoft.faithOS.entity.MemberMessage;

public interface MemberMessageRepository extends JpaRepository<MemberMessage, Long> {

    @Query("""
            select message from MemberMessage message
            where message.church.id = :churchId
              and ((message.sender.id = :firstUserId and message.recipient.id = :secondUserId)
                or (message.sender.id = :secondUserId and message.recipient.id = :firstUserId))
            order by message.createdAt
            """)
    List<MemberMessage> conversation(
            @Param("churchId") Long churchId,
            @Param("firstUserId") Long firstUserId,
            @Param("secondUserId") Long secondUserId);

    Optional<MemberMessage> findByIdAndChurchId(Long id, Long churchId);
    long deleteByExpiresAtBefore(java.time.LocalDateTime cutoff);
}
