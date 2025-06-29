package com.rodrigopettenon.orderflow.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_products")
public class ProductModel implements Serializable {

    private static final long serialVersionUID = 4451475447515617336L;

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "sku", length = 8, nullable = false, unique = true)
    private String sku;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "expiration_date", nullable = false)
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expiration;

    public ProductModel() {
        super();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }
}
