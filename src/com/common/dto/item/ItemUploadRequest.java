package com.common.dto.item;

public class ItemUploadRequest {

    private final String name;
    private final String description;
    private final Double price;
    private final Integer stock;
    private final String category;

    public ItemUploadRequest(
            String name,
            String description,
            Double price,
            Integer stock,
            String category) {

        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    public ItemUploadRequest(String name, String description, Double price, Integer stock) {
        this(name, description, price, stock, null);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "ItemUploadRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                '}';
    }
}