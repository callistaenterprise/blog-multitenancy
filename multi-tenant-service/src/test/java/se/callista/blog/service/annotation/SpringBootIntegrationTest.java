package se.callista.blog.service.annotation;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SpringBootTest
@Tag("integration")
@ActiveProfiles("integration-test")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringBootIntegrationTest {
}
