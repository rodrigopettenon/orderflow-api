package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<?> saveOrder(@RequestBody OrderDto orderDto) {
        return createObjectReturn(orderService.saveOrder(orderDto));
    }

    @GetMapping("/id")
    public ResponseEntity<?> findById(@RequestParam UUID id) {
        return createObjectReturn(orderService.findById(id));
    }

}
