package com.common.dto.item;

public class ItemEditRequest {

    private final int itemId;

    private final String newName;
    private final String newDescription;
    private final Double newPrice;
    private final Integer newStock;
    private final String newCategory;

    public ItemEditRequest(
            int itemId,
            String newName,
            String newDescription,
            Double newPrice,
            Integer newStock,
            String newCategory) {

        this.itemId = itemId;
        this.newName = newName;
        this.newDescription = newDescription;
        this.newPrice = newPrice;
        this.newStock = newStock;
        this.newCategory = newCategory;
    }

    public int getItemId() {
        return itemId;
    }

    public String getNewName() {
        return newName;
    }

    public String getNewDescription() {
        return newDescription;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public Integer getNewStock() {
        return newStock;
    }

    public String getNewCategory() {
        return newCategory;
    }

    @Override
    public String toString() {
        return "ItemEditRequest{" +
                "itemId=" + itemId +
                ", newName='" + newName + '\'' +
                ", newDescription='" + newDescription + '\'' +
                ", newPrice=" + newPrice +
                ", newStock=" + newStock +
                ", newCategory='" + newCategory + '\'' +
                '}';
    }
}