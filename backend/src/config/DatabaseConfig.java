package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();

            // Read connection settings from environment variables with sensible defaults for local dev
            String jdbcUrl = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/dustbusters");
            String dbUser = System.getenv().getOrDefault("DB_USER", "postgres");
            String dbPass = System.getenv().getOrDefault("DB_PASS", "mysecretpassword");

            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPass);
            config.setMaximumPoolSize(10);

            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}