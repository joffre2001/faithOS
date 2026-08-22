package com.obysoft.faithOS.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name="church_events")
public class ChurchEvent {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String title;
    @Column(length=1500) private String description;
    @Column(nullable=false) private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private String location;
    private String category;
    @ManyToOne(optional=false) @JoinColumn(name="church_id",nullable=false) private Church church;
    public Long getId(){return id;} public String getTitle(){return title;} public void setTitle(String v){title=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public LocalDateTime getStartsAt(){return startsAt;} public void setStartsAt(LocalDateTime v){startsAt=v;}
    public LocalDateTime getEndsAt(){return endsAt;} public void setEndsAt(LocalDateTime v){endsAt=v;}
    public String getLocation(){return location;} public void setLocation(String v){location=v;}
    public String getCategory(){return category;} public void setCategory(String v){category=v;}
    public Church getChurch(){return church;} public void setChurch(Church v){church=v;}
}
