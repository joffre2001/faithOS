package com.obysoft.faithOS.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ministries")
public class Ministry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(length=1000) private String description;
    private String leaderName;
    @Column(nullable=false) private Boolean active = true;
    @ManyToOne(optional=false) @JoinColumn(name="church_id",nullable=false) private Church church;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLeaderName(){return leaderName;} public void setLeaderName(String v){leaderName=v;}
    public Boolean getActive(){return active;} public void setActive(Boolean v){active=v;} public Church getChurch(){return church;} public void setChurch(Church v){church=v;}
}
