package com.rodrigopettenon.cadastro_e_consulta.dtos;

import java.io.Serializable;
import java.util.List;

public class ProductPageDto implements Serializable {

    private static final long serialVersionUID = -1153191500000498702L;

    private List<ProductDto> products;

    private Long total;

    public List<ProductDto> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDto> products) {
        this.products = products;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
