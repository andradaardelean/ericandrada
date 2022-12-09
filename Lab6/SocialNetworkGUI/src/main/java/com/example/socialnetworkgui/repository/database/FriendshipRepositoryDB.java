package com.example.socialnetworkgui.repository.database;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.exceptions.RepositoryException;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.example.socialnetworkgui.domain.User;
import java.sql.Date;

public class FriendshipRepositoryDB implements Repository<Friendship, Set<String>> {
    private String url;
    private String username;
    private String password;


    public FriendshipRepositoryDB(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public List<Friendship> getAll() {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "select * from friendships";
        UserRepositoryDB userRepo = new UserRepositoryDB(url, username, password);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                String username1 = resultSet.getString("username1");
                String username2 = resultSet.getString("username2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                User user1 = userRepo.find(username1);
                User user2 = userRepo.find(username2);
                Boolean pending = resultSet.getBoolean("status");
                Friendship friendship = new Friendship(user1, user2, date,pending);
                friendships.add(friendship);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public void add(Friendship friendship) throws RepositoryException {
        if (friendship == null) {
            throw new IllegalArgumentException("Friendship must not be null");
        }
        boolean ok = true;
        for (Friendship f : getAll()) {
            if (f.getID().equals(friendship.getID())) {
                ok = false;
                break;
            }
        }
        if (ok) {
            String sql = "insert into friendships(username1, username2, date,status) values (?, ?, ?, ?)";

            try (Connection connection = DriverManager.getConnection(url, username, password);
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, friendship.getU1().getUsername());
                ps.setString(2, friendship.getU2().getUsername());
                //Date date = Date.from(friendship.getDate().atZone(ZoneId.systemDefault()).toInstant());
                Date sqlDate = Date.valueOf(friendship.getDate().toLocalDate());
                ps.setDate(3, sqlDate);
                ps.setBoolean(4,friendship.getPending());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void remove(Friendship friendship) throws RepositoryException {
        String sql = "delete from friendships where username1 = ? and username2 = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, friendship.getU1().getUsername());
            ps.setString(2, friendship.getU2().getUsername());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Friendship find(Set<String> id) throws RepositoryException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        String sql = "select * from friendships where username1 = ? and username2 = ?";

        String[] ID = id.toArray(new String[id.size()]);
        String username1 = ID[0];
        String username2 = ID[1];
        UserRepositoryDB userRepo = new UserRepositoryDB(url, username, password);

        try (Connection connection = DriverManager.getConnection(url, this.username, this.password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username1);
            ps.setString(2, username2);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                String u1 = resultSet.getString("username1");
                String u2 = resultSet.getString("username2");
                LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

                User user1 = userRepo.find(u1);
                User user2 = userRepo.find(u2);
                Boolean pending = resultSet.getBoolean("status");
                Friendship friendship = new Friendship(user1, user2, date, pending);
                if (friendship.getID().equals(id)) {
                    return friendship;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }










    public void update(Friendship frnd, Friendship newFrnd) {
        String sql = "update friendships set username1 = ?, username2 = ?, date = ?, status = ? where username1=? and username2=?";
        try(Connection connection = DriverManager.getConnection(url,username,password);
        PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1,newFrnd.getU1().getUsername());
            ps.setString(2, newFrnd.getU2().getUsername());

            Date sqlDate = Date.valueOf(newFrnd.getDate().toLocalDate());
            ps.setDate(3, sqlDate);
            ps.setBoolean(4,newFrnd.getPending());
            ps.setString(5,frnd.getU1().getUsername());
            ps.setString(6,frnd.getU2().getUsername());

            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
