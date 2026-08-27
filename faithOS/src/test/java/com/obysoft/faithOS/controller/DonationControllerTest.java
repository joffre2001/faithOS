package com.obysoft.faithOS.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.obysoft.faithOS.entity.Church;
import com.obysoft.faithOS.service.ContributionService;
import com.obysoft.faithOS.service.CurrentChurchService;
import com.obysoft.faithOS.service.SensitiveDataService;

class DonationControllerTest {

    private final ContributionService contributions = mock(ContributionService.class);
    private final CurrentChurchService currentChurch = mock(CurrentChurchService.class);
    private final SensitiveDataService sensitiveData = mock(SensitiveDataService.class);
    private final DonationController controller = new DonationController(contributions, currentChurch, sensitiveData);

    @Test
    void returnsPixConfigurationFromAuthenticatedUsersChurch() {
        Church first = church("First Church", "encrypted-one", "FIRST CHURCH", "CHAPECO");
        Church second = church("Second Church", "encrypted-two", "SECOND CHURCH", "CURITIBA");
        when(sensitiveData.decrypt("encrypted-one")).thenReturn("first@example.org");
        when(sensitiveData.decrypt("encrypted-two")).thenReturn("second@example.org");

        when(currentChurch.church()).thenReturn(first);
        assertEquals("first@example.org", controller.configuration().get("key"));

        when(currentChurch.church()).thenReturn(second);
        assertEquals("second@example.org", controller.configuration().get("key"));
        assertEquals("SECOND CHURCH", controller.configuration().get("recipient"));
    }

    @Test
    void rejectsDonationsWhenChurchHasNoPixKey() {
        Church church = church("Unconfigured Church", null, null, null);
        when(currentChurch.church()).thenReturn(church);
        when(sensitiveData.decrypt((String) null)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, controller::configuration);
    }

    private Church church(String name, String key, String recipient, String city) {
        Church church = new Church();
        church.setName(name);
        church.setPixKey(key);
        church.setPixRecipient(recipient);
        church.setPixCity(city);
        return church;
    }
}
