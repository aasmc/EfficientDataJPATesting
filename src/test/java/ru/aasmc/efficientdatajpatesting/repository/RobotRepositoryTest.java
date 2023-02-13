package ru.aasmc.efficientdatajpatesting.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.aasmc.efficientdatajpatesting.testutils.TestDBFacade;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.aasmc.efficientdatajpatesting.entity.RobotTestBuilder.aRobot;

@DataJpaTest
@Import(TestDBFacade.class)
public class RobotRepositoryTest {

    @Autowired
    private RobotRepository robotRepository;

    @Autowired
    private TestDBFacade db;

    @Test
    void shouldReturnUniqueNames() {
        db.saveAll(
                aRobot().name("s1"),
                aRobot().name("s1"),
                aRobot().name("s2")
        );

        final var names = robotRepository.findUniqueNames();

        assertEquals(Set.of("s1", "s2"), names);
    }

}
