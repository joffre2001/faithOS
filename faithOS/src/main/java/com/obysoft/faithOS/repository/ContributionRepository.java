package com.obysoft.faithOS.repository;
import java.time.LocalDate; import java.util.List; import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import com.obysoft.faithOS.entity.Contribution;
public interface ContributionRepository extends JpaRepository<Contribution,Long>{List<Contribution> findAllByChurchIdOrderByContributionDateDesc(Long churchId);List<Contribution> findAllByChurchIdAndContributionDateBetween(Long churchId,LocalDate from,LocalDate to);Optional<Contribution> findByIdAndChurchId(Long id,Long churchId);}
