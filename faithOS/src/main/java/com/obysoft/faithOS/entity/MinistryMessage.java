package com.obysoft.faithOS.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity @Table(name="ministry_messages")
public class MinistryMessage {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="ministry_id",nullable=false) private Ministry ministry;
    @ManyToOne(optional=false) @JoinColumn(name="sender_id",nullable=false) private User sender;
    @Column(length=2000) private String message;
    @Column(name="attachment_name") private String attachmentName;
    @Column(name="attachment_type") private String attachmentType;
    @Column(name="attachment_size") private Long attachmentSize;
    @Column(name="attachment_data",columnDefinition="bytea") private byte[] attachmentData;
    @Column(name="created_at",nullable=false,updatable=false) private LocalDateTime createdAt;
    @PrePersist void created(){createdAt=LocalDateTime.now();}
    public Long getId(){return id;} public Ministry getMinistry(){return ministry;} public void setMinistry(Ministry v){ministry=v;}
    public User getSender(){return sender;} public void setSender(User v){sender=v;} public String getMessage(){return message;} public void setMessage(String v){message=v;}
    public String getAttachmentName(){return attachmentName;} public void setAttachmentName(String v){attachmentName=v;} public String getAttachmentType(){return attachmentType;} public void setAttachmentType(String v){attachmentType=v;}
    public Long getAttachmentSize(){return attachmentSize;} public void setAttachmentSize(Long v){attachmentSize=v;} public byte[] getAttachmentData(){return attachmentData;} public void setAttachmentData(byte[] v){attachmentData=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
