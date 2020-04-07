package fr.frogdevelopment.ep.application.xls.clean;

import static org.mockito.Mockito.inOrder;

import fr.frogdevelopment.ep.utils.UnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class CleanUpDataTest extends UnitTest {

    @InjectMocks
    private CleanUpData cleanUpData;

    @Mock
    private CleanUpExport cleanUpExport;
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
        var inOrder = inOrder(cleanUpExport, cleanUpTimetables, cleanUpSchedules, cleanUpVolunteers, cleanUpTeams);
        inOrder.verify(cleanUpExport).call();
        inOrder.verify(cleanUpSchedules).call();
        inOrder.verify(cleanUpTimetables).call();
        inOrder.verify(cleanUpVolunteers).call();
        inOrder.verify(cleanUpTeams).call();
    }
}
