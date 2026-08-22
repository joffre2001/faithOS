package com.obysoft.faithOS.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.obysoft.faithOS.dto.AttendanceRequest;
import com.obysoft.faithOS.entity.AttendanceSession;
import com.obysoft.faithOS.entity.AttendanceType;
import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.exception.ResourceNotFoundException;
import com.obysoft.faithOS.repository.AttendanceSessionRepository;
import com.obysoft.faithOS.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTenantTest {
    @Mock AttendanceSessionRepository sessions;
    @Mock UserRepository users;
    @Mock CurrentChurchService current;
    @InjectMocks AttendanceService service;

    @Test
    void rejectsAttendeesThatDoNotBelongToTheCurrentChurch() {
        Church church = mock(Church.class);
        when(church.getId()).thenReturn(10L);
        when(current.church()).thenReturn(church);
        when(users.findAllByIdInAndChurchId(Set.of(99L), 10L)).thenReturn(List.of());
        var request = new AttendanceRequest(
                "Sunday worship", AttendanceType.WORSHIP, LocalDate.of(2026, 8, 23), Set.of(99L));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found in your church");
        verify(sessions, never()).save(org.mockito.ArgumentMatchers.any(AttendanceSession.class));
    }
}
