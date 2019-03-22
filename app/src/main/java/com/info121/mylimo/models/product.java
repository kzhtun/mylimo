package com.info121.mylimo.models;

/**
 * Created by KZHTUN on 1/5/2018.
 */

public class product {

    /**
     * Id : 1
     * Name : Tomato Soup
     * Category : Groceries
     * Price : 1
     */

    private int Id;
    private String Name;
    private String Category;
    private double Price;

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double Price) {
        this.Price = Price;
    }
}
