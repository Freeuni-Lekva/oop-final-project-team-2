package com.moviemood.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebListener
public class DatabaseInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl("jdbc:mysql://localhost:3306/moviemoodDB");
            dataSource.setUsername("moviemood");
            dataSource.setPassword("moviemood");

            setUpDatabase(dataSource);

            servletContextEvent.getServletContext().setAttribute("dataSource:", dataSource);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

    }

    private void setUpDatabase(BasicDataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()) {

            // creating "users" table
            statement.executeUpdate("\n" +
                    "CREATE TABLE IF NOT EXISTS users (\n" +
                    "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "    email VARCHAR(255) UNIQUE NOT NULL,\n" +
                    "    password_hash VARCHAR(255) NOT NULL,\n" +
                    "    remember_token VARCHAR(255),\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");");



        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        BasicDataSource dataSource = (BasicDataSource) servletContextEvent.getServletContext().getAttribute("datasource");
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close datasource", e);
        }
    }
}
