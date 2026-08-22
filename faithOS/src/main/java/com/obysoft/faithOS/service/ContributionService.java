package com.obysoft.faithOS.service;
import java.util.List;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import com.obysoft.faithOS.dto.*;import com.obysoft.faithOS.entity.*;import com.obysoft.faithOS.exception.ResourceNotFoundException;import com.obysoft.faithOS.repository.ContributionRepository;
@Service public class ContributionService{
 private final ContributionRepository repository;private final CurrentChurchService current;
 public ContributionService(ContributionRepository repository,CurrentChurchService current){this.repository=repository;this.current=current;}
 public List<ContributionResponse> all(){return repository.findAllByChurchIdOrderByContributionDateDesc(current.church().getId()).stream().map(this::response).toList();}
 @Transactional public ContributionResponse create(ContributionRequest r){Contribution c=new Contribution();apply(c,r);c.setChurch(current.church());return response(repository.save(c));}
 @Transactional public ContributionResponse update(Long id,ContributionRequest r){Contribution c=find(id);apply(c,r);return response(repository.save(c));}
 @Transactional public void delete(Long id){repository.delete(find(id));}
 private Contribution find(Long id){return repository.findByIdAndChurchId(id,current.church().getId()).orElseThrow(()->new ResourceNotFoundException("Contribution not found."));}
 private void apply(Contribution c,ContributionRequest r){c.setDonorName(r.donorName());c.setAmount(r.amount());c.setContributionDate(r.contributionDate());c.setType(r.type().trim());c.setMethod(r.method());c.setNotes(r.notes());}
 private ContributionResponse response(Contribution c){return new ContributionResponse(c.getId(),c.getDonorName(),c.getAmount(),c.getContributionDate(),c.getType(),c.getMethod(),c.getNotes());}
}
