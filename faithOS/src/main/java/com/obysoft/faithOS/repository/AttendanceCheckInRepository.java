package com.obysoft.faithOS.repository;
import java.util.List;import java.util.Optional;import org.springframework.data.jpa.repository.JpaRepository;import com.obysoft.faithOS.entity.AttendanceCheckIn;
public interface AttendanceCheckInRepository extends JpaRepository<AttendanceCheckIn,Long>{List<AttendanceCheckIn> findAllBySessionIdOrderByCheckedInAt(Long sessionId);Optional<AttendanceCheckIn> findBySessionIdAndUserId(Long sessionId,Long userId);}
