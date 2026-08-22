package com.obysoft.faithOS.service;
import java.util.List; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import com.obysoft.faithOS.dto.*; import com.obysoft.faithOS.entity.*; import com.obysoft.faithOS.exception.*; import com.obysoft.faithOS.repository.MinistryRepository;
@Service public class MinistryService{
 private final MinistryRepository repository;private final CurrentChurchService current;
 public MinistryService(MinistryRepository repository,CurrentChurchService current){this.repository=repository;this.current=current;}
 public List<MinistryResponse> all(){Long id=current.church().getId();return repository.findAllByChurchIdOrderByName(id).stream().map(this::response).toList();}
 @Transactional public MinistryResponse create(MinistryRequest r){Church c=current.church();if(repository.existsByNameIgnoreCaseAndChurchId(r.name(),c.getId()))throw new DuplicateResourceException("A ministry with this name already exists.");Ministry m=new Ministry();apply(m,r);m.setChurch(c);return response(repository.save(m));}
 @Transactional public MinistryResponse update(Long id,MinistryRequest r){Ministry m=find(id);apply(m,r);return response(repository.save(m));}
 @Transactional public void delete(Long id){repository.delete(find(id));}
 private Ministry find(Long id){return repository.findByIdAndChurchId(id,current.church().getId()).orElseThrow(()->new ResourceNotFoundException("Ministry not found."));}
 private void apply(Ministry m,MinistryRequest r){m.setName(r.name().trim());m.setDescription(r.description());m.setLeaderName(r.leaderName());m.setActive(r.active()==null?true:r.active());}
 private MinistryResponse response(Ministry m){return new MinistryResponse(m.getId(),m.getName(),m.getDescription(),m.getLeaderName(),m.getActive());}
}
