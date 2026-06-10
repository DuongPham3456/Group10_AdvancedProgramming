package com.tinthanh.prototype.config;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MySQLConfiguration {

    /**
     * This application is configured to use SQL Server via Spring Boot datasource properties.
     * To link to SQL Server, update src/main/resources/application.properties with:
     *
     * spring.datasource.url=jdbc:sqlserver://<HOST>:1433;databaseName=<DB_NAME>;encrypt=false
     * spring.datasource.username=<USERNAME>
     * spring.datasource.password=<PASSWORD>
     * spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
     * spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
     * spring.jpa.hibernate.ddl-auto=update
     *
     * You can also use environment variables by setting DB_URL, DB_USERNAME and DB_PASSWORD.
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        // Ensure we build a HikariDataSource so that 'jdbcUrl' is set correctly
        return DataSourceBuilder.create()
                .type(com.zaxxer.hikari.HikariDataSource.class)
                .build();
    }
}
