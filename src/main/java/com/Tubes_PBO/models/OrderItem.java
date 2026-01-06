package com.Tubes_PBO.models;

public class OrderItem {

    private int productId;
    private String productName;
    private int quantity;
    private int priceEach;
    private int subtotal;
    private String imageUrl;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPriceEach() { return priceEach; }
    public void setPriceEach(int priceEach) { this.priceEach = priceEach; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
