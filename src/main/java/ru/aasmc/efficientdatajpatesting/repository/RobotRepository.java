package ru.aasmc.efficientdatajpatesting.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import ru.aasmc.efficientdatajpatesting.entity.Robot;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Set;

public interface RobotRepository extends JpaRepository<Robot, Long>,
        CustomRobotServerRepository, JpaSpecificationExecutor<Robot> {

    long countAllByTypeAndIdNot(Robot.Type type, Long id);

    @Query("SELECT DISTINCT name from Robot")
    Set<String> findUniqueNames();

    @Override
    @QueryHints(
            @QueryHint(name = "hint_name", value = "hint_value")
    )
    List<Robot> findAll(Specification<Robot> spec);
}
