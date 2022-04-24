package se.callista.blog.service.config;

import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.util.StringUtils;
import se.callista.blog.service.multi_tenancy.connection.TenantAwareConnectionFactory;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties({R2dbcProperties.class})
public class R2dbcConfig extends AbstractR2dbcConfiguration {

  private final R2dbcProperties r2dbcProperties;

  @Bean
  @Override
  public ConnectionFactory connectionFactory() {
    ConnectionFactory connectionFactory =  ConnectionFactoryBuilder
        .withOptions(getConnectionFactoryOptions(r2dbcProperties))
        .build();
    return new TenantAwareConnectionFactory(connectionFactory);
  }

  // Implementation taken from non-public class
  // org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsInitializer
  private ConnectionFactoryOptions.Builder getConnectionFactoryOptions(R2dbcProperties properties) {
    ConnectionFactoryOptions urlOptions = ConnectionFactoryOptions.parse(properties.getUrl());
    ConnectionFactoryOptions.Builder optionsBuilder = urlOptions.mutate();
    if (StringUtils.hasText(properties.getUsername())) {
      optionsBuilder.option(ConnectionFactoryOptions.USER, properties.getUsername());
    }
    if (StringUtils.hasText(properties.getPassword())) {
      optionsBuilder.option(ConnectionFactoryOptions.PASSWORD, properties.getPassword());
    }
    if (properties.isGenerateUniqueName()) {
      optionsBuilder.option(ConnectionFactoryOptions.DATABASE, properties.determineUniqueName());
    } else if (StringUtils.hasText(properties.getName())) {
      optionsBuilder.option(ConnectionFactoryOptions.DATABASE, properties.getName());
    }
    if (properties.getProperties() != null) {
      properties.getProperties().forEach((key, value) -> optionsBuilder.option(Option.valueOf(key), value));
    }
    return optionsBuilder;
  }

  @Bean
  @Primary
  ReactiveTransactionManager connectionFactoryTransactionManager() {
    return new R2dbcTransactionManager(connectionFactory());
  }


}