package com.rodrigopettenon.orderflow.dtos;

import java.io.Serializable;
import java.util.List;

public class GlobalPageDto<T> implements Serializable {

    private static final long serialVersionUID = 1541392814940774157L;

    private Long total;
    private List<T> items;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
