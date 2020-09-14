package se.callista.blog.service.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.sql.DataSource;

@Lazy(false)
@Configuration
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseConfig {

    @Bean
    @ConfigurationProperties("multitenancy.master.liquibase")
    public LiquibaseProperties masterLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public SpringLiquibase liquibase(@LiquibaseDataSource ObjectProvider<DataSource> liquibaseDataSource) {
        LiquibaseProperties liquibaseProperties = masterLiquibaseProperties();
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(liquibaseDataSource.getIfAvailable());
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
        return liquibase;
    }

}