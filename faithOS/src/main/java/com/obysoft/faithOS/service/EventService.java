package com.obysoft.faithOS.service;
import java.util.List;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;
import com.obysoft.faithOS.dto.*;import com.obysoft.faithOS.entity.*;import com.obysoft.faithOS.exception.ResourceNotFoundException;import com.obysoft.faithOS.repository.ChurchEventRepository;
@Service public class EventService{
 private final ChurchEventRepository repository;private final CurrentChurchService current;
 public EventService(ChurchEventRepository repository,CurrentChurchService current){this.repository=repository;this.current=current;}
 public List<EventResponse> all(){return repository.findAllByChurchIdOrderByStartsAtAsc(current.church().getId()).stream().map(this::response).toList();}
 @Transactional public EventResponse create(EventRequest r){ChurchEvent e=new ChurchEvent();apply(e,r);e.setChurch(current.church());return response(repository.save(e));}
 @Transactional public EventResponse update(Long id,EventRequest r){ChurchEvent e=find(id);apply(e,r);return response(repository.save(e));}
 @Transactional public void delete(Long id){repository.delete(find(id));}
 private ChurchEvent find(Long id){return repository.findByIdAndChurchId(id,current.church().getId()).orElseThrow(()->new ResourceNotFoundException("Event not found."));}
 private void apply(ChurchEvent e,EventRequest r){if(r.endsAt()!=null&&r.endsAt().isBefore(r.startsAt()))throw new IllegalArgumentException("End date cannot be before start date.");e.setTitle(r.title().trim());e.setDescription(r.description());e.setStartsAt(r.startsAt());e.setEndsAt(r.endsAt());e.setLocation(r.location());e.setCategory(r.category());}
 private EventResponse response(ChurchEvent e){return new EventResponse(e.getId(),e.getTitle(),e.getDescription(),e.getStartsAt(),e.getEndsAt(),e.getLocation(),e.getCategory());}
}
