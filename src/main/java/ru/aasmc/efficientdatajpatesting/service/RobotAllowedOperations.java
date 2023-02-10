package ru.aasmc.efficientdatajpatesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import ru.aasmc.efficientdatajpatesting.annotation.ReadTransactional;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ru.aasmc.efficientdatajpatesting.service.OperationStatus.*;

@Service
public class RobotAllowedOperations {

    private static final Logger LOG = LoggerFactory.getLogger(RobotAllowedOperations.class);

    private final RobotRestrictions robotRestrictions;

    @Autowired
    public RobotAllowedOperations(RobotRestrictions robotRestrictions) {
        this.robotRestrictions = robotRestrictions;
    }

    @Transactional(readOnly = true)
    public Map<Long, OperationStatus> getRobotsSwitchOnStatus(Collection<Long> robotIds) {
        return innerGetRobotsSwitchOnStatus(robotIds, robotRestrictions::checkSwitchOn);
    }

    @Transactional(readOnly = true)
    public Map<Long, OperationStatus> getRobotsSwitchOnStatusRequiresNew(
            Collection<Long> robotIds
    ) {
        return innerGetRobotsSwitchOnStatus(
                robotIds,
                robotRestrictions::checkSwitchOnRequiresNew
        );
    }

    @Transactional(readOnly = true)
    public Map<Long, OperationStatus> getRobotSwitchOStatusNoRollbackFor(
            Collection<Long> robotIds
    ) {
        return innerGetRobotsSwitchOnStatus(
                robotIds,
                robotRestrictions::checkSwitchOnNoRollbackFor
        );
    }

    @ReadTransactional
    public Map<Long, OperationStatus> getRobotsSwitchOnStatusReadTransactional(
            Collection<Long> robotIds
    ) {
        return innerGetRobotsSwitchOnStatus(robotIds,
                robotRestrictions::checkSwitchOnReadTransactional);
    }

    private Map<Long, OperationStatus> innerGetRobotsSwitchOnStatus(
            Collection<Long> robotIds,
            final Consumer<Long> restrictionChecker
    ) {
        final Map<Long, OperationStatus> result = robotIds.stream()
                .collect(Collectors.toMap(
                        k -> k,
                        k -> getOperationStatus(k, restrictionChecker)
                ));
        return result;
    }

    private OperationStatus getOperationStatus(Long robotId,
                                               Consumer<Long> restrictionChecker) {
        try {
            restrictionChecker.accept(robotId);
            return ALLOWED;
        } catch (NoSuchElementException e) {
            LOG.debug(String.format("Server with id %s is absent", robotId), e);
            return ROBOT_IS_ABSENT;
        } catch (OperationRestrictedException e) {
            LOG.debug(String.format("Server with id %s cannot be switched on", robotId), e);
            return RESTRICTED;
        }
    }

}





























