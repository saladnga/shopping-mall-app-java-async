package com.common.dto.item;

public class ItemBrowseRequest {

    private final String category;
    private final Double minPrice;
    private final Double maxPrice;
    private final Integer minStock;

    public ItemBrowseRequest(String category, Double minPrice, Double maxPrice, Integer minStock) {
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minStock = minStock;
    }

    public ItemBrowseRequest() {
        this(null, null, null, null);
    }

    public String getCategory() {
        return category;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public Integer getMinStock() {
        return minStock;
    }

    @Override
    public String toString() {
        return "ItemBrowseRequest{" +
                "category='" + category + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", minStock=" + minStock +
                '}';
    }
}