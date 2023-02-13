package ru.aasmc.efficientdatajpatesting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import ru.aasmc.efficientdatajpatesting.entity.Robot;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;
import ru.aasmc.efficientdatajpatesting.testutils.TestDBFacade;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static ru.aasmc.efficientdatajpatesting.entity.RobotTestBuilder.aRobot;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Import(TestDBFacade.Config.class)
public class RobotUpdateServiceTestH2TestDataBuilder {
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

    @Test
    void shouldSwitchOnSuccessfully() {
        final var id = db.save(aRobot().switched(false)).getId();
        doNothing().when(robotRestrictions).checkSwitchOnReadTransactional(id);

        service.switchOnRobot(id);

        final var savedServer  = db.find(id, Robot.class);
        assertTrue(savedServer.isSwitched());
    }

    @Test
    void shouldRollbackIfCannotSwitchOn() {
        final var id = db.save(aRobot().switched(false)).getId();
        doThrow(new OperationRestrictedException("")).when(robotRestrictions).checkSwitchOnReadTransactional(id);

        assertThrows(OperationRestrictedException.class, () -> service.switchOnRobot(id));

        final var savedRobot  = db.find(id, Robot.class);
        assertFalse(savedRobot.isSwitched());
    }
}
