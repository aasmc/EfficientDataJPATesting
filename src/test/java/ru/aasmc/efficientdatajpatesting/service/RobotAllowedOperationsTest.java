package ru.aasmc.efficientdatajpatesting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aasmc.efficientdatajpatesting.entity.Robot;
import ru.aasmc.efficientdatajpatesting.testutils.TestDBFacade;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.aasmc.efficientdatajpatesting.entity.RobotTestBuilder.aRobot;
import static ru.aasmc.efficientdatajpatesting.service.OperationStatus.ALLOWED;
import static ru.aasmc.efficientdatajpatesting.service.OperationStatus.RESTRICTED;

@DataJpaTest
@Import(TestDBFacade.Config.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class RobotAllowedOperationsTest {

    @Autowired
    private TestDBFacade db;
    @Autowired
    private RobotAllowedOperations robotAllowedOperations;

    @BeforeEach
    void beforeEach() {
        db.cleanDatabase();
    }

    @Test
    @Disabled("Always fails due to rollback-only behavior")
    void shouldNotAllowSomeRobotsToSwitchOn() {
        innerShouldNotAllowSomeRobotsToSwitchOn(robotAllowedOperations::getRobotsSwitchOnStatus);
    }

    @Test
    void shouldNotAllowSomeRobotsToSwitchOnRequiresNew() {
        innerShouldNotAllowSomeRobotsToSwitchOn(robotAllowedOperations::getRobotsSwitchOnStatusRequiresNew);
    }

    @Test
    void shouldNotAllowSomeRobotsToSwitchOnNoRollbackFor() {
        innerShouldNotAllowSomeRobotsToSwitchOn(robotAllowedOperations::getRobotSwitchOnStatusNoRollbackFor);
    }

    @Test
    void shouldNotAllowSomeRobotsToSwitchOnReadTransactional() {
        innerShouldNotAllowSomeRobotsToSwitchOn(robotAllowedOperations::getRobotsSwitchOnStatusReadTransactional);
    }


    private void innerShouldNotAllowSomeRobotsToSwitchOn(
            Function<Collection<Long>, Map<Long, OperationStatus>> function
    ) {
        final var driver = db.save(aRobot().switched(true).type(Robot.Type.DRIVER));
        final var loader = db.save(aRobot().switched(false).type(Robot.Type.LOADER));
        final var vacuumTemplate = aRobot().switched(false).type(Robot.Type.VACUUM);
        final var vacuum = db.save(vacuumTemplate);
        db.saveAll(
                vacuumTemplate.switched(true),
                vacuumTemplate.switched(true),
                vacuumTemplate.switched(true)
        );
        final var robotIds = List.of(driver.getId(), loader.getId(), vacuum.getId());
        final var operations = function.apply(robotIds);
        assertEquals(RESTRICTED, operations.get(driver.getId()));
        assertEquals(ALLOWED, operations.get(loader.getId()));
        assertEquals(RESTRICTED, operations.get(vacuum.getId()));
    }

    @TestConfiguration
    static class Config {

        @Bean
        public RobotRestrictions serverRestrictions() {
            return new RobotRestrictions();
        }
        @Bean
        public RobotAllowedOperations serverAllowedOperations() {
            return new RobotAllowedOperations(serverRestrictions());
        }
    }
}
