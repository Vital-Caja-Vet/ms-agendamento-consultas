package com.vitalcajavet.msagendamentoconsultas.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseInitializerConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializerConfig.class);
    private static final String POSTGRES_JDBC_PREFIX = "jdbc:postgresql://";
    private static final String DEFAULT_DATABASE = "postgres";
    private static final int DEFAULT_PORT = 5432;

    private final DataSourceProperties properties;

    public DatabaseInitializerConfig(DataSourceProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        String url = properties.determineUrl();
        String username = properties.determineUsername();
        String password = properties.determinePassword();

        ensurePostgresDatabaseExists(url, username, password);

        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    private void ensurePostgresDatabaseExists(String url, String username, String password) {
        if (!StringUtils.hasText(url) || !url.startsWith(POSTGRES_JDBC_PREFIX)) {
            return;
        }

        DatabaseTarget target = parsePostgresUrl(url);
        if (target == null) {
            logger.warn("Could not parse JDBC url '{}'.", url);
            return;
        }

        String adminUrl = buildAdminUrl(target);

        try (Connection connection = DriverManager.getConnection(adminUrl, username, password)) {
            if (!databaseExists(connection, target.database())) {
                createDatabase(connection, target.database());
                logger.info("Database '{}' created automatically.", target.database());
            } else {
                logger.debug("Database '{}' already exists.", target.database());
            }
        } catch (SQLException ex) {
            logger.warn("Failed to ensure database '{}' exists: {}", target.database(), ex.getMessage());
            if (logger.isDebugEnabled()) {
                logger.debug("Full error while creating database", ex);
            }
        }
    }

    private boolean databaseExists(Connection connection, String database) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
            statement.setString(1, database);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void createDatabase(Connection connection, String database) throws SQLException {
        String sanitizedName = database.replace("\"", "\"\"");
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE \"" + sanitizedName + "\"");
        }
    }

    private String buildAdminUrl(DatabaseTarget target) {
        return POSTGRES_JDBC_PREFIX + target.hostSegment() + ":" + target.port() + "/" + DEFAULT_DATABASE;
    }

    private DatabaseTarget parsePostgresUrl(String url) {
        String jdbcLessUrl = url.substring("jdbc:".length());
        URI uri;
        try {
            uri = URI.create(jdbcLessUrl);
        } catch (IllegalArgumentException ex) {
            logger.debug("Invalid JDBC url '{}': {}", url, ex.getMessage());
            return null;
        }

        String path = uri.getPath();
        if (!StringUtils.hasText(path)) {
            return null;
        }

        String database = path.startsWith("/") ? path.substring(1) : path;
        if (!StringUtils.hasText(database)) {
            return null;
        }

        String host = uri.getHost();
        if (!StringUtils.hasText(host)) {
            host = "localhost";
        }

        int port = uri.getPort() > 0 ? uri.getPort() : DEFAULT_PORT;
        String hostSegment = host.contains(":") && !host.startsWith("[") ? "[" + host + "]" : host;

        return new DatabaseTarget(hostSegment, port, database);
    }

    private record DatabaseTarget(String hostSegment, int port, String database) {
    }
}

