package com.Tubes_PBO.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.Tubes_PBO.models.User;

public class UserDAO {

    public User login(String username, String password) {
        try {
            Connection conn = DatabaseConection.getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean register(String username, String password, String role) {

    String check = "SELECT id FROM users WHERE username = ?";
    String insert = "INSERT INTO users(username, password, role) VALUES (?, ?, ?)";

    try (Connection c = DatabaseConection.getConnection()) {

        PreparedStatement psCheck = c.prepareStatement(check);
        psCheck.setString(1, username);
        ResultSet rs = psCheck.executeQuery();

        if (rs.next()) return false;

        PreparedStatement ps = c.prepareStatement(insert);
        ps.setString(1, username);
        ps.setString(2, password); // nanti bisa di-hash
        ps.setString(3, role);
        ps.executeUpdate();

        return true;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

}
