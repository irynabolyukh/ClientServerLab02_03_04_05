package org.clientserver.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UserDao {

    private final Connection connection;

    public UserDao(final String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Can't find SQLite JDBC class", e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        initTable();
    }

    private void initTable() {
        try (final Statement statement = connection.createStatement()) {
            statement.execute("create table if not exists 'users'('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'login' VARCHAR(50) NOT NULL, 'password' VARCHAR(250) NOT NULL, 'role' VARCHAR(20) NOT NULL, UNIQUE (login))");
        } catch (final SQLException e) {
            throw new RuntimeException("Can't create table", e);
        }
    }

    public int insert(final User user) {
        try (final PreparedStatement insertStatement = connection.prepareStatement("insert into 'users'('login', 'password', 'role') values (?, ?, ?)")) {
            insertStatement.setString(1, user.getLogin());
            insertStatement.setString(2, user.getPassword());
            insertStatement.setString(3, user.getRole());
            insertStatement.execute();

            final ResultSet result = insertStatement.getGeneratedKeys();
            return result.getInt("last_insert_rowid()");
        } catch (SQLException e) {
            throw new RuntimeException("Can't insert user", e);
        }
    }

    public User getByLogin(final String login) {
        try (final PreparedStatement insertStatement = connection.prepareStatement("select * from 'users' where login = ?")) {
            insertStatement.setString(1, login);
            final ResultSet resultSet = insertStatement.executeQuery();

            if (resultSet.next()) {
                return User.builder()
                        .id(resultSet.getInt("id"))
                        .login(resultSet.getString("login"))
                        .password(resultSet.getString("password"))
                        .role(resultSet.getString("role"))
                        .build();
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException("Can't get user by login: " + login, e);
        }
    }

}