package com.obysoft.faithOS.repository;
import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import com.obysoft.faithOS.entity.Ministry;
public interface MinistryRepository extends JpaRepository<Ministry,Long>{List<Ministry> findAllByChurchIdOrderByName(Long churchId);Optional<Ministry> findByIdAndChurchId(Long id,Long churchId);boolean existsByNameIgnoreCaseAndChurchId(String name,Long churchId);}
