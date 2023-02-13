package ru.aasmc.efficientdatajpatesting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aasmc.efficientdatajpatesting.entity.Robot;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;
import ru.aasmc.efficientdatajpatesting.repository.RobotRepository;
import ru.aasmc.efficientdatajpatesting.testutils.TestDBFacade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static ru.aasmc.efficientdatajpatesting.entity.RobotTestBuilder.aRobot;

@DataJpaTest
@Import(TestDBFacade.Config.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RobotUpdateServiceTestH2DataJpaNonTransactional {
    @Autowired
    private RobotUpdateService service;
    @Autowired
    private TestDBFacade db;
    @MockBean
    private RobotRestrictions robotRestrictions;

    @BeforeEach
    void beforeEach() {
        db.cleanDatabase();
    }

    @TestConfiguration
    static class Config {

        @Bean
        public RobotUpdateService service(
                RobotRepository robotRepository,
                RobotRestrictions robotRestrictions
        ) {
            return new RobotUpdateService(robotRestrictions, robotRepository);
        }
    }

    @Test
    void shouldSwitchOnSuccessfully() {
        final var id = db.save(aRobot().switched(false)).getId();
        doNothing().when(robotRestrictions).checkSwitchOnReadTransactional(id);

        service.switchOnRobot(id);

        final var savedRobot = db.find(id, Robot.class);
        assertTrue(savedRobot.isSwitched());
    }

    @Test
    void shouldRollbackIfCannotSwitchOn() {
        final var id = db.save(aRobot().switched(false)).getId();
        doThrow(new OperationRestrictedException("")).when(robotRestrictions).checkSwitchOnReadTransactional(id);

        assertThrows(OperationRestrictedException.class, () -> service.switchOnRobot(id));

        final var savedRobot = db.find(id, Robot.class);
        assertFalse(savedRobot.isSwitched());
    }
}




























