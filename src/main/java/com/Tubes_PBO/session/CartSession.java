package com.Tubes_PBO.session;

import java.util.HashMap;
import java.util.Map;

import com.Tubes_PBO.models.Product;

public class CartSession {

    private static final Map<Integer, Integer> cart = new HashMap<>();

    public static void addProduct(Product p) {
        cart.put(p.getProductId(), cart.getOrDefault(p.getProductId(), 0) + 1);
    }

    public static Map<Integer, Integer> getCart() {
        return cart;
    }

    public static void clear() {
        cart.clear();
    }
}
