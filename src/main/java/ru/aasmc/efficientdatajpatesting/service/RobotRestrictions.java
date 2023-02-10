package ru.aasmc.efficientdatajpatesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.aasmc.efficientdatajpatesting.annotation.ReadTransactional;
import ru.aasmc.efficientdatajpatesting.exception.OperationRestrictedException;
import ru.aasmc.efficientdatajpatesting.repository.RobotRepository;

@Service
public class RobotRestrictions {
    private final RobotRepository robotRepository;

    @Autowired
    public RobotRestrictions(RobotRepository robotRepository) {
        this.robotRepository = robotRepository;
    }

    @Transactional(readOnly = true)
    public void checkSwitchOn(Long serverId) {
        innerCheckSwitchOn(serverId);
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void checkSwitchOnRequiresNew(Long serverId) {
        innerCheckSwitchOn(serverId);
    }

    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public void checkSwitchOnNoRollbackFor(Long serverId) {
        innerCheckSwitchOn(serverId);
    }

    @ReadTransactional
    public void checkSwitchOnReadTransactional(Long serverId) {
        innerCheckSwitchOn(serverId);
    }

    private void innerCheckSwitchOn(Long robotId) {
        final var robot = robotRepository.findById(robotId)
                .orElseThrow();

        if (robot.isSwitched()) {
            throw new OperationRestrictedException(
                    String.format("Robot %s is already switched on", robot.getName())
            );
        }
        final var count = robotRepository.countAllByTypeAndIdNot(robot.getType(), robotId);
        if (count >= 3) {
            throw new OperationRestrictedException(
                    String.format("There is already 3 switched on robots of type %s", robot.getType())
            );
        }
    }

}
