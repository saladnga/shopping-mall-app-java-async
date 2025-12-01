package com.common.dto.item;

public class ItemSearchRequest {

    private final String keyword; // required: search text
    private final String category; // optional
    private final Double minPrice; // optional
    private final Double maxPrice; // optional
    private final String sortOrder; // optional: "PRICE_ASC", "PRICE_DESC", "POPULARITY", etc.

    public ItemSearchRequest(
            String keyword,
            String category,
            Double minPrice,
            Double maxPrice,
            String sortOrder) {

        this.keyword = keyword;
        this.category = category;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sortOrder = sortOrder;
    }

    public ItemSearchRequest(String keyword) {
        this(keyword, null, null, null, null);
    }

    public String getKeyword() {
        return keyword;
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

    public String getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return "ItemSearchRequest{" +
                "keyword='" + keyword + '\'' +
                ", category='" + category + '\'' +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}