package com.rodrigopettenon.orderflow.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "tb_item_orders")
public class ItemOrderModel implements Serializable {

    private static final long serialVersionUID = 6192001757459910307L;

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)", length = 36, nullable = false, unique = true)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderModel order;
    // Relacionamento N:1 (Muitos itens para um pedido)
    // Carregamento Lazy = só busca o cliente quando o getOrder() for chamado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductModel product;
    // Relacionamento N:1 (Muitos itens para um para um produto)
    // Carregamento Lazy = só busca o cliente quando o getProduct() for chamado

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;


    public ItemOrderModel() {
        super();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public OrderModel getOrder() {
        return order;
    }

    public void setOrder(OrderModel order) {
        this.order = order;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
