package se.callista.blog.service.multitenancy.config.tenant.liquibase;

import java.util.Collection;
import java.util.Collections;
import javax.sql.DataSource;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import se.callista.blog.service.multitenancy.domain.entity.Tenant;
import se.callista.blog.service.multitenancy.repository.TenantRepository;

/**
 * Based on MultiTenantSpringLiquibase, this class provides Liquibase support for
 * multi-tenancy based on a dynamic collection of DataSources.
 */
@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class DynamicSchemaBasedMultiTenantSpringLiquibase implements InitializingBean, ResourceLoaderAware {

    private final TenantRepository masterTenantRepository;

    private final DataSource dataSource;

    @Qualifier("tenantLiquibaseProperties")
    private final LiquibaseProperties liquibaseProperties;

    private ResourceLoader resourceLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Schema based multitenancy enabled");
        this.runOnAllSchemas(dataSource, masterTenantRepository.findAll());
    }

    protected void runOnAllSchemas(DataSource dataSource, Collection<Tenant> tenants) throws LiquibaseException {
        for(Tenant tenant : tenants) {
            log.info("Initializing Liquibase for tenant " + tenant.getTenantId());
            SpringLiquibase liquibase = this.getSpringLiquibase(dataSource, tenant.getSchema());
            liquibase.afterPropertiesSet();
            log.info("Liquibase ran for tenant " + tenant.getTenantId());
        }
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(getResourceLoader());
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(schema);
        if (liquibaseProperties.getParameters() != null) {
            liquibaseProperties.getParameters().put("schema", schema);
            liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        } else {
            liquibase.setChangeLogParameters(Collections.singletonMap("schema", schema));
        }
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setLiquibaseSchema(liquibaseProperties.getLiquibaseSchema());
        liquibase.setLiquibaseTablespace(liquibaseProperties.getLiquibaseTablespace());
        liquibase.setDatabaseChangeLogTable(liquibaseProperties.getDatabaseChangeLogTable());
        liquibase.setDatabaseChangeLogLockTable(liquibaseProperties.getDatabaseChangeLogLockTable());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        liquibase.setLabels(liquibaseProperties.getLabels());
        liquibase.setRollbackFile(liquibaseProperties.getRollbackFile());
        liquibase.setTestRollbackOnUpdate(liquibaseProperties.isTestRollbackOnUpdate());
        return liquibase;
    }

}
