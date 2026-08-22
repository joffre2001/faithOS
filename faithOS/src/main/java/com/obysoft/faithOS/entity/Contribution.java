package com.obysoft.faithOS.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name="contributions")
public class Contribution {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private String donorName;
    @Column(nullable=false,precision=14,scale=2) private BigDecimal amount;
    @Column(nullable=false) private LocalDate contributionDate;
    @Column(nullable=false) private String type;
    private String method;
    @Column(length=1000) private String notes;
    @ManyToOne(optional=false) @JoinColumn(name="church_id",nullable=false) private Church church;
    public Long getId(){return id;} public String getDonorName(){return donorName;} public void setDonorName(String v){donorName=v;}
    public BigDecimal getAmount(){return amount;} public void setAmount(BigDecimal v){amount=v;}
    public LocalDate getContributionDate(){return contributionDate;} public void setContributionDate(LocalDate v){contributionDate=v;}
    public String getType(){return type;} public void setType(String v){type=v;} public String getMethod(){return method;} public void setMethod(String v){method=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;} public Church getChurch(){return church;} public void setChurch(Church v){church=v;}
}
