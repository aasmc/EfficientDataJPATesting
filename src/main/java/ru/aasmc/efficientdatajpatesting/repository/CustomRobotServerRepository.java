package ru.aasmc.efficientdatajpatesting.repository;

import org.springframework.data.domain.Page;

public interface CustomRobotServerRepository {
    Page<RobotView> findByFilter(RobotFilter filter, int page, int pageSize);
}
