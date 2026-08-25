package com.obysoft.faithOS.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity @Table(name="attendance_check_ins",uniqueConstraints=@UniqueConstraint(columnNames={"session_id","user_id"}))
public class AttendanceCheckIn {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false) @JoinColumn(name="session_id",nullable=false) private AttendanceSession session;
 @ManyToOne(optional=false) @JoinColumn(name="user_id",nullable=false) private User user;
 @Column(name="checked_in_at",nullable=false) private LocalDateTime checkedInAt;
 @Enumerated(EnumType.STRING) @Column(name="attendance_status",nullable=false) private AttendanceStatus status;
 @Enumerated(EnumType.STRING) @Column(name="check_in_source",nullable=false) private CheckInSource source;
 public Long getId(){return id;} public AttendanceSession getSession(){return session;} public void setSession(AttendanceSession v){session=v;} public User getUser(){return user;} public void setUser(User v){user=v;} public LocalDateTime getCheckedInAt(){return checkedInAt;} public void setCheckedInAt(LocalDateTime v){checkedInAt=v;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public CheckInSource getSource(){return source;} public void setSource(CheckInSource v){source=v;}
}
