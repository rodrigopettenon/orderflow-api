package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(@RequestBody OrderDto orderDto) {
        return createObjectReturn(orderService.saveOrder(orderDto));
    }
}
