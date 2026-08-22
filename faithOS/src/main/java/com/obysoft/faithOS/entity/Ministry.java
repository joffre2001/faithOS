package com.obysoft.faithOS.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "ministries")
public class Ministry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String name;
    @Column(length=1000) private String description;
    private String leaderName;
    @ManyToOne @JoinColumn(name="leader_id") private User leader;
    @ManyToMany
    @JoinTable(name="ministry_members",
            joinColumns=@JoinColumn(name="ministry_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> members = new LinkedHashSet<>();
    @Column(nullable=false) private Boolean active = true;
    @ManyToOne(optional=false) @JoinColumn(name="church_id",nullable=false) private Church church;
    public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public String getLeaderName(){return leaderName;} public void setLeaderName(String v){leaderName=v;}
    public User getLeader(){return leader;} public void setLeader(User v){leader=v;}
    public Set<User> getMembers(){return members;} public void setMembers(Set<User> v){members=v;}
    public Boolean getActive(){return active;} public void setActive(Boolean v){active=v;} public Church getChurch(){return church;} public void setChurch(Church v){church=v;}
}
