package ru.aasmc.efficientdatajpatesting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.aasmc.efficientdatajpatesting.entity.Robot;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;
import ru.aasmc.efficientdatajpatesting.repository.RobotRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
@AutoConfigureTestDatabase
public class RobotUpdateServiceTestH2 {
    @Autowired
    private RobotUpdateService service;
    @Autowired
    private RobotRepository robotRepository;
    @MockBean
    private RobotRestrictions robotRestrictions;

    @BeforeEach
    void beforeEach() {
        robotRepository.deleteAll();
    }

    @Test
    void shouldSwitchOnSuccessfully() {
        final var robot = new Robot();
        robot.setSwitched(false);
        robot.setType(Robot.Type.DRIVER);
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
        robot.setType(Robot.Type.DRIVER);
        robot.setName("some_name");
        robotRepository.save(robot);
        doThrow(new OperationRestrictedException("")).when(robotRestrictions).checkSwitchOnReadTransactional(robot.getId());

        assertThrows(OperationRestrictedException.class, () -> {
            service.switchOnRobot(robot.getId());
        });
        final var savedRobot = robotRepository.findById(robot.getId()).orElseThrow();
        assertFalse(savedRobot.isSwitched());
    }
}




























