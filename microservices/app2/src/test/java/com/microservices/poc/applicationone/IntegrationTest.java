package com.microservices.poc.applicationone;

import com.microservices.poc.applicationone.ApplicationoneApp;
import com.microservices.poc.applicationone.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { ApplicationoneApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
