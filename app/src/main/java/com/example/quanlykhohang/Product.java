package com.example.quanlykhohang;

public class Product {
    private int id;
    private String name;
    private int price;
    private int quantity;
    private String category;
    private int active; // 1 = đang kinh doanh, 0 = ngừng kinh doanh
    private String description;

    public Product() {}

    public Product(int id, String name, int price, int quantity,
                   String category, int active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    // Constructor không có id (dùng khi thêm mới)
    public Product(String name, int price, int quantity,
                   String category, int active, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getCategory() { return category; }
    public int getActive() { return active; }
    public String getDescription() { return description; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setCategory(String category) { this.category = category; }
    public void setActive(int active) { this.active = active; }
    public void setDescription(String description) { this.description = description; }

    public boolean isLowStock() {
        return quantity < 5;
    }

    public boolean isActive() {
        return active == 1;
    }

    public long getStockValue() {
        return (long) price * quantity;
    }
}
