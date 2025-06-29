package com.rodrigopettenon.orderflow.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemOrderFullDetailsDto implements Serializable {

    private static final long serialVersionUID = -1494348510536341346L;

    private ItemOrderDto itemOrder;
    private OrderDto order;
    private ClientDto client;
    private ProductDto product;

    public ItemOrderDto getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(ItemOrderDto itemOrder) {
        this.itemOrder = itemOrder;
    }

    public OrderDto getOrder() {
        return order;
    }

    public void setOrder(OrderDto order) {
        this.order = order;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }
}
