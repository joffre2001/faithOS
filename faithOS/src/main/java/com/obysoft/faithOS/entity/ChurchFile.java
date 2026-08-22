package com.obysoft.faithOS.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "church_files")
public class ChurchFile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "original_name", nullable = false) private String originalName;
    @Column(name = "stored_name", nullable = false, unique = true) private String storedName;
    @Column(name = "content_type") private String contentType;
    @Column(name = "file_size", nullable = false) private Long size;
    @Column(name = "created_at", nullable = false, updatable = false) private LocalDateTime createdAt;
    @ManyToOne(optional = false) @JoinColumn(name = "church_id", nullable = false) private Church church;
    @ManyToOne @JoinColumn(name = "uploaded_by") private User uploadedBy;

    @PrePersist void prePersist() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Church getChurch() { return church; }
    public void setChurch(Church church) { this.church = church; }
    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }
}
