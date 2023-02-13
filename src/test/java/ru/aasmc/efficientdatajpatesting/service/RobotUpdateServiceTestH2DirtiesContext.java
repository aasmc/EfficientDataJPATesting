package ru.aasmc.efficientdatajpatesting.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import ru.aasmc.efficientdatajpatesting.entity.Robot;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;
import ru.aasmc.efficientdatajpatesting.repository.RobotRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static ru.aasmc.efficientdatajpatesting.entity.Robot.Type.DRIVER;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RobotUpdateServiceTestH2DirtiesContext {
    @Autowired
    private RobotUpdateService service;
    @Autowired
    private RobotRepository robotRepository;
    @MockBean
    private RobotRestrictions robotRestrictions;

    @Test
    void shouldSwitchOnSuccessfully() {
        final var robot = new Robot();
        robot.setSwitched(false);
        robot.setType(DRIVER);
        robot.setName("some_name");
        robotRepository.save(robot);
        doNothing().when(robotRestrictions).checkSwitchOnReadTransactional(robot.getId());

        service.switchOnRobot(robot.getId());

        final var savedRobot = robotRepository.findById(robot.getId()).orElseThrow();
        assertTrue(savedRobot.isSwitched());
    }

    @Test
    void shouldRollbackIfCannotSwitchOn() {
        final var robot = new Robot();
        robot.setSwitched(false);
        robot.setType(DRIVER);
        robot.setName("some_name");
        robotRepository.save(robot);
        doThrow(new OperationRestrictedException("")).when(robotRestrictions)
                .checkSwitchOnReadTransactional(robot.getId());

        assertThrows(OperationRestrictedException.class, () -> service.switchOnRobot(robot.getId()));

        final var savedRobot = robotRepository.findById(robot.getId()).orElseThrow();
        assertFalse(savedRobot.isSwitched());
    }
}
