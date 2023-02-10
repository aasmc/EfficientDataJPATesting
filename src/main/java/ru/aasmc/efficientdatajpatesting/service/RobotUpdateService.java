package ru.aasmc.efficientdatajpatesting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.aasmc.efficientdatajpatesting.repository.RobotRepository;

@Service
public class RobotUpdateService {
    private final RobotRestrictions robotRestrictions;
    private final RobotRepository robotRepository;

    @Autowired
    public RobotUpdateService(RobotRestrictions robotRestrictions, RobotRepository robotRepository) {
        this.robotRestrictions = robotRestrictions;
        this.robotRepository = robotRepository;
    }

    @Transactional
    public void switchOnRobot(Long robotId) {
        final var robot =
                robotRepository.findById(robotId).orElseThrow();
        robot.setSwitched(true);
        robotRepository.flush();
        // will throw exception if not allowed to be switched on
        robotRestrictions.checkSwitchOnReadTransactional(robotId);
    }
}
