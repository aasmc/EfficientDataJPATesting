package ru.aasmc.efficientdatajpatesting.repository;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.springframework.data.domain.Page;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

public class CustomRobotServerRepositoryImpl implements CustomRobotServerRepository {

    @PersistenceContext
    private EntityManager em;

    private EntityManagerFactory emf;

    @Override
    public Page<RobotView> findByFilter(RobotFilter filter, int page, int pageSize) {
        SessionFactoryImplementor sessionFactory =
                (SessionFactoryImplementor) emf.unwrap(SessionFactory.class);
        ServiceRegistryImplementor serviceRegistry = sessionFactory.getServiceRegistry();
        emf.createEntityManager()
                .createQuery("")
                .getSingleResult();
        return Page.empty();
    }
}
