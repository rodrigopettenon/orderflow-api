package com.rodrigopettenon.cadastro_e_consulta.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class OrderDto implements Serializable {

    private static final long serialVersionUID = -7732423783020372412L;

    private UUID id;
    private Long clientId;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime orderDate;
    private String status;

    public OrderDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
