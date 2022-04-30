package se.callista.blog.management.annotation;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

@SpringBootIntegrationTest
@DBRider(dataSourceBeanName = "masterDataSource")
@DBUnit(caseSensitiveTableNames = true, dataTypeFactoryClass = PostgresqlDataTypeFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpringBootDbIntegrationTest {
}
