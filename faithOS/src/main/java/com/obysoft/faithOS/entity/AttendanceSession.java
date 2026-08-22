package com.obysoft.faithOS.entity;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "attendance_sessions")
public class AttendanceSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type", nullable = false)
    private AttendanceType type;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "church_id", nullable = false)
    private Church church;

    @ManyToMany
    @JoinTable(name = "attendance_records",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> attendees = new LinkedHashSet<>();

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public AttendanceType getType() { return type; }
    public void setType(AttendanceType type) { this.type = type; }
    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }
    public Church getChurch() { return church; }
    public void setChurch(Church church) { this.church = church; }
    public Set<User> getAttendees() { return attendees; }
    public void setAttendees(Set<User> attendees) { this.attendees = attendees; }
}
