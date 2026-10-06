package com.dayzer0.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A catalog row. The category column is deliberately NOT indexed. */
@Entity
@Table(name = "products")
public class Product {

    @Id
    private Long id;
    private String name;
    private String category;
    private Integer pricePaise;
    private Double rating;
    private Boolean inStock;

    protected Product() {
    }

    public Product(Long id, String name, String category, Integer pricePaise, Double rating, Boolean inStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.pricePaise = pricePaise;
        this.rating = rating;
        this.inStock = inStock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Integer getPricePaise() {
        return pricePaise;
    }

    public Double getRating() {
        return rating;
    }

    public Boolean getInStock() {
        return inStock;
    }
}
