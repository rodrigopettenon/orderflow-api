package com.rodrigopettenon.cadastro_e_consulta.dtos;

import java.io.Serializable;
import java.util.List;

public class OrderPageDto implements Serializable {

    private static final long serialVersionUID = 1541392814940774157L;

    private Long total;
    private List<OrderDto> orders;

    public List<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
