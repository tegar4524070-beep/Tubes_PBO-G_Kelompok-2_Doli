package com.Tubes_PBO.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.Tubes_PBO.models.OrderItem;

public class OrderItemDAO {

    public static List<OrderItem> getItemsByOrderId(int orderId) {

    List<OrderItem> list = new ArrayList<>();

    String sql = """
        SELECT oi.product_id,
               oi.quantity,
               oi.price_each,
               oi.subtotal,
               p.product_name,
               p.image_url
        FROM order_items oi
        JOIN products p ON oi.product_id = p.product_id
        WHERE oi.order_id = ?
    """;

    try (Connection c = DatabaseConection.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {

        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            OrderItem item = new OrderItem();
            item.setProductId(rs.getInt("product_id"));
            item.setProductName(rs.getString("product_name"));
            item.setQuantity(rs.getInt("quantity"));
            item.setPriceEach(rs.getInt("price_each")); // 🔥 FIX
            item.setSubtotal(rs.getInt("subtotal"));
            item.setImageUrl(rs.getString("image_url"));

            list.add(item);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}

}
