package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ItemOrderDto;
import com.rodrigopettenon.cadastro_e_consulta.services.ItemOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item-orders")
public class ItemOrderController extends BaseController{

    @Autowired
    private ItemOrderService itemOrderService;

    @PostMapping("/save")
    public ResponseEntity<?> saveItemOrder(@RequestBody ItemOrderDto itemOrderDto) {
        return createObjectReturn(itemOrderService.saveItemOrder(itemOrderDto));
    }

}
