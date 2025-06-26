package com.moviemood.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.moviemood.bean.User;
import com.moviemood.config.Config;
import com.moviemood.dao.UserDao;
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
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(Config.get("jdbc.url"));
            dataSource.setUsername(Config.get("jdbc.username"));
            dataSource.setPassword(Config.get("jdbc.password"));

            setUpDatabase(dataSource);

            UserDao userDao = new UserDao(dataSource);
            servletContextEvent.getServletContext().setAttribute("userDao", userDao);
            servletContextEvent.getServletContext().setAttribute("dataSource", dataSource);
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
                    " username VARCHAR(100) UNIQUE NOT NULL,\n" +
                    "    email VARCHAR(255) UNIQUE NOT NULL,\n" +
                    "    password_hash VARCHAR(255) NOT NULL,\n" +
                    "    remember_token VARCHAR(255),\n" +
                    "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                    ");");

            // Creates friendships table
            statement.executeUpdate(
                    " CREATE TABLE IF NOT EXISTS friendships (" +
                        " user1_id INT NOT NULL, " +
                        " user2_id INT NOT NULL, " +
                        " creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        " FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE, " +
                        " FOREIGN KEY (user2_ID) REFERENCES users(id) ON DELETE CASCADE, " +
                        " PRIMARY KEY (user1_id, user2_id), " +
                        " CHECK (user1_id < user2_id)" +
                        ");"
            );


            // Creates friend_requests table
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS friend_requests (" +
                            "id INT PRIMARY KEY AUTO_INCREMENT, " +
                            "sender_id INT NOT NULL, " +
                            "receiver_id INT NOT NULL, " +
                            "status ENUM('pending', 'accepted', 'rejected') NOT NULL DEFAULT 'pending', " +
                            "request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            "FOREIGN KEY (sender_id) REFERENCES users(id)  ON DELETE CASCADE, " +
                            "FOREIGN KEY (receiver_id) REFERENCES users(id)  ON DELETE CASCADE" +
                            ");"
            );



        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        BasicDataSource dataSource = (BasicDataSource) servletContextEvent.getServletContext().getAttribute("dataSource");
        try {
            dataSource.close();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close datasource", e);
        }
    }
}
