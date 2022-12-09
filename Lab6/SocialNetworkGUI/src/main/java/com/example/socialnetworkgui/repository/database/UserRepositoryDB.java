package com.example.socialnetworkgui.repository.database;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.*;

public class UserRepositoryDB implements Repository<User, String> {
    private String url;
    private String username;
    private String password;

    public UserRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public User find(String username) {
        if(username == null){
            throw new IllegalArgumentException("ID must not be null.");
        }

        String sql = "SELECT * FROM users where users.username = ?";
        User user;

        try(Connection connection = DriverManager.getConnection(url,this.username,password);
        PreparedStatement statement = connection.prepareStatement(sql)){
            statement.setString(1,username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                String username1 = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");

                user = new User(username1, password, email);
                return user;
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public int size() {
        return this.getAll().size();
    }

    @Override
    public List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");

                User user = new User(username, password, email);
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void add(User user) {
        if(user == null){
            throw new IllegalArgumentException("User must not be null.");
        }
        boolean ok = true;
        for(User u: getAll()){
            if(u.getID().equals(user.getUsername())){
                ok = false;
                break;
            }
        }
        if(ok) {
            String sql = "insert into users (username, password, email) values (?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void remove(User user) {

        String sql = "delete from users where username = ?";
        String sql1 = "delete from friendships where username1 = ? or username2 = ?";
        try(Connection connection = DriverManager.getConnection(url,username,password)){
            PreparedStatement ps = connection.prepareStatement(sql);
            PreparedStatement ps1 = connection.prepareStatement(sql1);

            ps1.setString(1,user.getUsername());
            ps1.setString(2,user.getUsername());
            ps.setString(1,user.getUsername());
            ps1.executeUpdate();
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void update(String id, String username, String password, String email) {
        String sql = "update users set username = ?, password = ?, email = ? where username = ?";


        try(Connection connection = DriverManager.getConnection(url,this.username, this.password)){
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1,username);
            ps.setString(2,password);
            ps.setString(3,email);
            ps.setString(4,id);

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
