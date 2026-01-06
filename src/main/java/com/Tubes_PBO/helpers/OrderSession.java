package com.Tubes_PBO.helpers;

import com.Tubes_PBO.models.Order;

public class OrderSession {
    private static Order currentOrder;

    public static Order getCurrentOrder() {
        return currentOrder;
    }

    public static void setCurrentOrder(Order order) {
        currentOrder = order;
    }

    public static void clear() {
        currentOrder = null;
    }
}

