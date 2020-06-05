package org.clientserver.entities;

import org.json.JSONObject;

import java.sql.*;
import java.util.*;

public class DaoGroup {

    private final Connection connection;

    public DaoGroup(final String dbFile){
        try{
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:"+dbFile);
        } catch (ClassNotFoundException e) {
            System.out.println("Can't load SQLite JDBC class");
            throw new RuntimeException("Can't find class", e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        initTable();
    }

    private void initTable() {
        try(final Statement statement = connection.createStatement()){
            String query = "create table if not exists 'groups'" +
                    " ('id' INTEGER PRIMARY KEY, 'name' text not null," +
                    " 'description' text not null, unique(name));";
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't create groups table", e);
        }
    }

    public int insertGroup(final Group group){
        if(isNameUnique(group.getName())) {
            String query = "insert into 'groups' ('id', 'name', 'description') values (?, ?, ?);";
            try (final PreparedStatement insertStatement = connection.prepareStatement(query)) {

                insertStatement.setInt(1, group.getId());
                insertStatement.setString(2, group.getName());
                insertStatement.setString(3, group.getDescription());

                insertStatement.execute();

                return group.getId();
            } catch (SQLException e) {
                throw new RuntimeException("Can't insert group", e);
            }
        }
        return -1;
    }

    public int updateGroup(Group group){
        if(isNameUnique(group.getName())) {
            try (final PreparedStatement preparedStatement =
                         connection.prepareStatement("update 'groups' set name = ?, description = ?  where id = ?")) {
                preparedStatement.setString(1, group.getName());
                preparedStatement.setString(2, group.getDescription());
                preparedStatement.setInt(3, group.getId());
                preparedStatement.executeUpdate();
                return group.getId();
            } catch (SQLException e) {
                throw new RuntimeException("Can't update group", e);
            }
        }
        return -1;
    }

    public boolean isNameUnique(final String groupName){
        try(final Statement statement = connection.createStatement()){
            final ResultSet resultSet = statement.executeQuery(
                    String.format("select count(*) as num_of_groups from 'groups' where name = '%s'", groupName)
            );
            resultSet.next();
            return resultSet.getInt("num_of_groups") == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Can't find group", e);
        }
    }

    public List<Group> getAll(){
        try(final Statement statement = connection.createStatement()){

            final String sql = String.format("select * from 'groups'");
            final ResultSet resultSet = statement.executeQuery(sql);

            final List<Group> groups = new ArrayList<>();
            while(resultSet.next()){
                groups.add(new Group(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("description")));
            }
            return groups;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of groups", e);
        }
    }

    public Group getGroup(final int id){
        try(final Statement statement = connection.createStatement()){
            final String sql = String.format("select * from 'groups' where id = %s", id);
            final ResultSet resultSet = statement.executeQuery(sql);

            Group group = new Group(resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("description"));
            return group;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get product", e);
        }
    }

    public void deleteAll(){
        try(final Statement statement = connection.createStatement()){
            String query = "delete from 'groups'";
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete groups", e);
        }
    }

    public void deleteTable(){
        try(final Statement statement = connection.createStatement()){
            String query = "drop table 'groups'";
            statement.execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete table", e);
        }
    }

    public int deleteGroup(final int id){
        try(final PreparedStatement preparedStatement = connection.prepareStatement("delete from 'groups' where id = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            return id;
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete product", e);
        }
    }

    public JSONObject toJSONObject(List<Group> groups){
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("{\"list\":[");

        for (Group g: groups) {
            stringBuffer.append(g.toJSON().toString() + ", ");
        }
        stringBuffer.delete(stringBuffer.length()-2, stringBuffer.length()-1);
        stringBuffer.append("]}");

        return new JSONObject(stringBuffer.toString());
    }
}

