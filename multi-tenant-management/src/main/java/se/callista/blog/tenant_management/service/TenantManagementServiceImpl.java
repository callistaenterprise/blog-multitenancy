package se.callista.blog.tenant_management.service;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import se.callista.blog.tenant_management.domain.entity.Tenant;
import se.callista.blog.tenant_management.repository.TenantRepository;

import javax.sql.DataSource;

@Slf4j
@Service
@EnableConfigurationProperties(LiquibaseProperties.class)
public class TenantManagementServiceImpl implements TenantManagementService {

    private final EncryptionService encryptionService;
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;
    private final TenantRepository tenantRepo;

    private final String urlPrefix;
    private final String liquibaseChangeLog;
    private final String liquibaseContexts;
    private final String secret;
    private final String salt;

    @Autowired
    public TenantManagementServiceImpl(EncryptionService encryptionService,
                                       DataSource dataSource,
                                       JdbcTemplate jdbcTemplate,
                                       @Qualifier("masterLiquibaseProperties")
                                       LiquibaseProperties liquibaseProperties,
                                       ResourceLoader resourceLoader,
                                       TenantRepository tenantRepo,
                                       @Value("${multitenancy.master.datasource.url}") String urlPrefix,
                                       @Value("${multitenancy.tenant.liquibase.changeLog}") String liquibaseChangeLog,
                                       @Value("${multitenancy.tenant.liquibase.contexts:#{null}") String liquibaseContexts,
                                       @Value("${encryption.secret}") String secret,
                                       @Value("${encryption.salt}") String salt
    ) {
        this.encryptionService = encryptionService;
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.liquibaseProperties = liquibaseProperties;
        this.resourceLoader = resourceLoader;
        this.tenantRepo = tenantRepo;
        this.urlPrefix = urlPrefix;
        this.liquibaseChangeLog = liquibaseChangeLog;
        this.liquibaseContexts = liquibaseContexts;
        this.secret = secret;
        this.salt = salt;
    }

    private static final String VALID_SCHEMA_NAME_REGEXP = "[A-Za-z0-9_]*";

    @Override
    public void createTenant(String tenantId, String schema, String password) {

        // Verify schema string to prevent SQL injection
        if (!schema.matches(VALID_SCHEMA_NAME_REGEXP)) {
            throw new TenantCreationException("Invalid schema name: " + schema);
        }

        String url = urlPrefix+"?currentSchema="+schema;
        String encryptedPassword = encryptionService.encrypt(password, secret, salt);
        Tenant tenant = Tenant.builder()
                .tenantId(tenantId)
                .schema(schema)
                .url(url)
                .password(encryptedPassword)
                .build();
        tenantRepo.save(tenant);
    }

    private void runLiquibase(DataSource dataSource, String schema) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquibase(dataSource, schema);
        liquibase.afterPropertiesSet();
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(schema);
        liquibase.setChangeLog(liquibaseChangeLog);
        liquibase.setContexts(liquibaseContexts);
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setShouldRun(liquibaseProperties.isEnabled());
        return liquibase;
    }
}
