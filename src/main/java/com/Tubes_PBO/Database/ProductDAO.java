package com.Tubes_PBO.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.Tubes_PBO.models.Product;

public class ProductDAO {

    // =========================
    // GET PRODUCT BY ID
    // =========================
    public static Product getById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = DatabaseConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // GET ALL PRODUCTS
    // =========================
    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection c = DatabaseConection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {

            while (r.next()) {
                Product p = new Product(
                        r.getInt("product_id"),
                        r.getString("product_name"),
                        r.getDouble("price"),
                        r.getInt("stock")
                );
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================
    // KURANGI STOCK (ORDER)
    // =========================
    public static void decreaseStock(int productId, int qty) {
        String sql = "UPDATE products SET stock = stock - ? " +
                     "WHERE product_id = ? AND stock >= ?";

        try (Connection conn = DatabaseConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.setInt(3, qty);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================
    // TAMBAH STOCK (KARYAWAN)
    // =========================
    public static void addStock(int productId, int qty) {
        String sql = "UPDATE products SET stock = stock + ? WHERE product_id = ?";

        try (Connection c = DatabaseConection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
