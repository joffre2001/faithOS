package com.obysoft.faithOS.repository;
import java.util.List;import java.util.Optional;import org.springframework.data.jpa.repository.JpaRepository;import com.obysoft.faithOS.entity.MinistryMessage;
public interface MinistryMessageRepository extends JpaRepository<MinistryMessage,Long>{List<MinistryMessage> findAllByMinistryIdOrderByCreatedAtAsc(Long ministryId);Optional<MinistryMessage> findByIdAndMinistryId(Long id,Long ministryId);}
