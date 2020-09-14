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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import se.callista.blog.tenant_management.domain.entity.Tenant;
import se.callista.blog.tenant_management.repository.TenantRepository;
import se.callista.blog.tenant_management.util.EncryptionService;

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

    private final String databaseName;
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
                                       @Value("${databaseName:}") String databaseName,
                                       @Value("${multitenancy.tenant.datasource.url-prefix}") String urlPrefix,
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
        this.databaseName = databaseName;
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

        String url = urlPrefix+databaseName+"?currentSchema="+schema;
        String encryptedPassword = encryptionService.encrypt(password, secret, salt);
        try {
            createSchema(schema, password);
            runLiquibase(dataSource, schema);
        } catch (DataAccessException e) {
              throw new TenantCreationException("Error when creating schema: " + schema, e);
        } catch (LiquibaseException e) {
            throw new TenantCreationException("Error when populating schema: ", e);
        }
        Tenant tenant = Tenant.builder()
                .tenantId(tenantId)
                .schema(schema)
                .url(url)
                .password(encryptedPassword)
                .build();
        tenantRepo.save(tenant);
    }

    private void createSchema(String schema, String password) {
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE USER " + schema+ " WITH ENCRYPTED PASSWORD '" + password + "'"));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("GRANT CONNECT ON DATABASE " + databaseName + " TO " + schema));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE SCHEMA " + schema + " AUTHORIZATION " + schema));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("ALTER DEFAULT PRIVILEGES IN SCHEMA " + schema + " GRANT ALL PRIVILEGES ON TABLES TO " + schema));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("ALTER DEFAULT PRIVILEGES IN SCHEMA " + schema + " GRANT USAGE ON SEQUENCES TO " + schema));
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("ALTER DEFAULT PRIVILEGES IN SCHEMA " + schema + " GRANT EXECUTE ON FUNCTIONS TO " + schema));
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
