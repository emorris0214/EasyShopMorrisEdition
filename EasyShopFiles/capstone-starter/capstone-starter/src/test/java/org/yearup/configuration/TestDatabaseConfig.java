package org.yearup.configuration;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class TestDatabaseConfig
{
    @Value("${datasource.url}")
    private String serverUrl;

    @Value("${datasource.username}")
    private String username;

    @Value("${datasource.password}")
    private String password;

    @Value("${datasource.testdb}")
    private String testDb;

    private String buildJdbcUrl(String dbName)
    {
        // Ensure no trailing slash or accidental double slashes
        return serverUrl.endsWith("/") ? serverUrl + dbName : serverUrl + "/" + dbName;
    }

    @PostConstruct
    public void setup()
    {
        try (Connection connection = DriverManager.getConnection(buildJdbcUrl("sys"), username, password);
             Statement statement = connection.createStatement())
        {
            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
            statement.execute("CREATE DATABASE " + testDb + ";");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test database.");
        }
    }

//    @PreDestroy
//    public void cleanup()
//    {
//        try (Connection connection = DriverManager.getConnection(buildJdbcUrl("sys"), username, password);
//             Statement statement = connection.createStatement())
//        {
//            statement.execute("DROP DATABASE IF EXISTS " + testDb + ";");
//        }
//        catch (SQLException e)
//        {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to clean up test database.");
//        }
//    }

    @Bean
    public DataSource dataSource() throws SQLException, IOException
    {
        String fullUrl = buildJdbcUrl(testDb);

        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setUrl(fullUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setAutoCommit(false);
        dataSource.setSuppressClose(true);

        // Run schema and seed SQL
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        try (Reader reader = new BufferedReader(
                new FileReader(new ClassPathResource("test-data.sql").getFile())))
        {
            runner.runScript(reader);
            dataSource.getConnection().commit();
        }

        return dataSource;
    }
}
