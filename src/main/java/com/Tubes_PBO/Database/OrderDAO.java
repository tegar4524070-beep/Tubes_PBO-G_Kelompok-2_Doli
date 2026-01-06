package com.Tubes_PBO.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.Tubes_PBO.models.Order;

public class OrderDAO {

    // ===============================
    // GET PENDING ORDER BY USER
    // ===============================
    public static Order getPendingOrderByUser(int userId) {

        String sql = """
            SELECT * FROM orders
            WHERE user_id = ?
            AND status = 'pending'
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (Connection c = DatabaseConection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("order_id"));
                order.setCustomerId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                return order;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // ===============================
    // CREATE NEW ORDER
    // ===============================
    public static Order createNewOrder(int userId) {

    String sql = """
        INSERT INTO orders (user_id, total_price, status, created_at)
        VALUES (?, 0, 'pending', NOW())
    """;

    try (Connection c = DatabaseConection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, userId);
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            Order order = new Order();
            order.setId(rs.getInt(1));      // order_id
            order.setCustomerId(userId);    // user_id
            order.setTotalPrice(0);         // ⬅️ SET MANUAL
            order.setStatus("pending");
            order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return order;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}


    // ===============================
    // ADD PRODUCT TO ORDER
    // ===============================
    public static boolean addProductToOrder(int userId, int productId, int qty) {

        String getPendingOrder = """
            SELECT order_id FROM orders
            WHERE user_id = ? AND status = 'pending'
            LIMIT 1
        """;

        String insertOrder = """
            INSERT INTO orders (user_id, total_price, status, created_at)
            VALUES (?, 0, 'pending', NOW())
        """;

        String getProduct = """
            SELECT price, stock FROM products
            WHERE product_id = ?
        """;

        String checkItem = """
            SELECT quantity FROM order_items
            WHERE order_id = ? AND product_id = ?
        """;

        String insertItem = """
            INSERT INTO order_items
            (order_id, product_id, quantity, price_each, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;

        String updateItem = """
            UPDATE order_items
            SET quantity = quantity + ?,
                subtotal = subtotal + ?
            WHERE order_id = ? AND product_id = ?
        """;

        String updateOrderTotal = """
            UPDATE orders
            SET total_price = total_price + ?
            WHERE order_id = ?
        """;

        String updateStock = """
            UPDATE products
            SET stock = stock - ?
            WHERE product_id = ?
        """;

        try (Connection conn = DatabaseConection.getConnection()) {

            conn.setAutoCommit(false);

            int orderId;

            // 1️⃣ GET / CREATE ORDER
            PreparedStatement psPending =
                    conn.prepareStatement(getPendingOrder);
            psPending.setInt(1, userId);
            ResultSet rsPending = psPending.executeQuery();

            if (rsPending.next()) {
                orderId = rsPending.getInt("order_id");
            } else {
                PreparedStatement psNew =
                        conn.prepareStatement(insertOrder,
                                Statement.RETURN_GENERATED_KEYS);
                psNew.setInt(1, userId);
                psNew.executeUpdate();

                ResultSet keys = psNew.getGeneratedKeys();
                keys.next();
                orderId = keys.getInt(1);
            }

            // 2️⃣ PRODUCT DATA
            PreparedStatement psProd =
                    conn.prepareStatement(getProduct);
            psProd.setInt(1, productId);
            ResultSet rsProd = psProd.executeQuery();

            if (!rsProd.next()) {
                conn.rollback();
                return false;
            }

            int price = rsProd.getInt("price");
            int stock = rsProd.getInt("stock");

            if (stock < qty) {
                conn.rollback();
                return false;
            }

            int subtotal = price * qty;

            // 3️⃣ CHECK ITEM
            PreparedStatement psCheck =
                    conn.prepareStatement(checkItem);
            psCheck.setInt(1, orderId);
            psCheck.setInt(2, productId);
            ResultSet rsItem = psCheck.executeQuery();

            if (rsItem.next()) {
                PreparedStatement psUpdate =
                        conn.prepareStatement(updateItem);
                psUpdate.setInt(1, qty);
                psUpdate.setInt(2, subtotal);
                psUpdate.setInt(3, orderId);
                psUpdate.setInt(4, productId);
                psUpdate.executeUpdate();
            } else {
                PreparedStatement psInsert =
                        conn.prepareStatement(insertItem);
                psInsert.setInt(1, orderId);
                psInsert.setInt(2, productId);
                psInsert.setInt(3, qty);
                psInsert.setInt(4, price);
                psInsert.setInt(5, subtotal);
                psInsert.executeUpdate();
            }

            // 4️⃣ UPDATE ORDER TOTAL
            PreparedStatement psTotal =
                    conn.prepareStatement(updateOrderTotal);
            psTotal.setInt(1, subtotal);
            psTotal.setInt(2, orderId);
            psTotal.executeUpdate();

            // 5️⃣ UPDATE STOCK
            PreparedStatement psStock =
                    conn.prepareStatement(updateStock);
            psStock.setInt(1, qty);
            psStock.setInt(2, productId);
            psStock.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===============================
    // CLOSE ORDER
    // ===============================
    public static void closeOrder(int orderId) {
    String sql = """
        UPDATE orders 
        SET status = 'paid'
        WHERE order_id = ?
    """;

    try (Connection c = DatabaseConection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static void cancelOrder(int orderId) {
    String sql = "UPDATE orders SET status = 'canceled' WHERE order_id = ?";

    try (Connection conn = DatabaseConection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ps.executeUpdate();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

public static List<Order> getOrderHistory(int userId) {

    String sql = """
        SELECT * FROM orders
        WHERE user_id = ?
        AND status != 'pending'
        ORDER BY created_at DESC
    """;

    List<Order> list = new ArrayList<>();

    try (Connection c = DatabaseConection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Order o = new Order();
            o.setId(rs.getInt("order_id"));
            o.setCustomerId(rs.getInt("user_id"));
            o.setTotalPrice(rs.getDouble("total_price"));
            o.setStatus(rs.getString("status"));
            o.setCreatedAt(rs.getTimestamp("created_at"));
            list.add(o);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

public static List<Order> getOrdersForKaryawan() {
    List<Order> list = new ArrayList<>();

    String sql = """
        SELECT order_id, user_id, total_price, status, created_at
        FROM orders
        WHERE status != 'completed'
        ORDER BY created_at DESC
    """;

    try (Connection c = DatabaseConection.getConnection();
         Statement s = c.createStatement();
         ResultSet r = s.executeQuery(sql)) {

        while (r.next()) {
            Order o = new Order();
            o.setId(r.getInt("order_id"));
            o.setCustomerId(r.getInt("user_id"));
            o.setTotalPrice(r.getDouble("total_price"));
            o.setStatus(r.getString("status"));
            o.setCreatedAt(r.getTimestamp("created_at"));

            list.add(o);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}
public static void completeOrder(int orderId) {
    String sql = "DELETE FROM orders WHERE order_id = ?";

    try (Connection c = DatabaseConection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ps.executeUpdate();

    } catch (SQLException e) {
        e.printStackTrace();
    }
}



}
