package ru.aasmc.efficientdatajpatesting.testutils;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Import({TestDBFacade.Config.class})
public @interface DBTest {

    @AliasFor(annotation = DataJpaTest.class, attribute = "properties")
    String[] properties() default {};
}
