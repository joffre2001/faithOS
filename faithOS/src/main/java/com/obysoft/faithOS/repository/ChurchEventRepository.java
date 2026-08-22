package com.obysoft.faithOS.repository;
import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import com.obysoft.faithOS.entity.ChurchEvent;
public interface ChurchEventRepository extends JpaRepository<ChurchEvent,Long>{List<ChurchEvent> findAllByChurchIdOrderByStartsAtAsc(Long churchId);Optional<ChurchEvent> findByIdAndChurchId(Long id,Long churchId);}
