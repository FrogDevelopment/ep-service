package fr.frogdevelopment.ep.implementation.xls.clean;

import static org.mockito.Mockito.inOrder;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class CleanUpDataTest {

    @InjectMocks
    private CleanUpData cleanUpData;

    @Mock
    private CleanUpTeams cleanUpTeams;
    @Mock
    private CleanUpSchedules cleanUpSchedules;
    @Mock
    private CleanUpVolunteers cleanUpVolunteers;
    @Mock
    private CleanUpTimetables cleanUpTimetables;

    @Test
    void shouldCallAddCleanUps() {
        // when
        cleanUpData.call();

        // then
        var inOrder = inOrder(cleanUpTimetables, cleanUpSchedules, cleanUpVolunteers, cleanUpTeams);
        inOrder.verify(cleanUpSchedules).call();
        inOrder.verify(cleanUpTimetables).call();
        inOrder.verify(cleanUpVolunteers).call();
        inOrder.verify(cleanUpTeams).call();
    }
}
