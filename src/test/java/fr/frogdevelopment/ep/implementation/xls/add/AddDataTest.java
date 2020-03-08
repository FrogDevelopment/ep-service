package fr.frogdevelopment.ep.implementation.xls.add;

import static org.mockito.BDDMockito.anyCollection;
import static org.mockito.BDDMockito.inOrder;

import fr.frogdevelopment.ep.implementation.xls.Result;
import java.util.Collections;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unitTest")
@ExtendWith(MockitoExtension.class)
class AddDataTest {

    @InjectMocks
    private AddData addData;

    @Mock
    private AddTimetables addTimetables;
    @Mock
    private AddTeams addTeams;
    @Mock
    private AddVolunteers addVolunteers;
    @Mock
    private AddSchedules addSchedules;

    @Test
    void shouldCallAddAdds() {
        // given
        var result = Result.builder()
                .teams(Collections.emptyList())
                .volunteers(Collections.emptyList())
                .timetables(Collections.emptyList())
                .schedules(Collections.emptyList())
                .build();

        // when
        addData.call(result);

        // then
        var inOrder = inOrder(addTeams, addVolunteers, addTimetables, addSchedules);
        inOrder.verify(addTeams).call(anyCollection());
        inOrder.verify(addVolunteers).call(anyCollection());
        inOrder.verify(addTimetables).call(anyCollection());
        inOrder.verify(addSchedules).call(anyCollection());
    }
}
