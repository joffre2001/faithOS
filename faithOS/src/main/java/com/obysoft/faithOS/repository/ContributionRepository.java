package com.obysoft.faithOS.repository;
import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import com.obysoft.faithOS.entity.Contribution;
public interface ContributionRepository extends JpaRepository<Contribution,Long>{List<Contribution> findAllByChurchIdOrderByContributionDateDesc(Long churchId);Optional<Contribution> findByIdAndChurchId(Long id,Long churchId);}
