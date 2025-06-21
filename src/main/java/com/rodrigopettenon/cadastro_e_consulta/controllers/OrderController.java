package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping("/filter")
    public ResponseEntity<?> findFilteredOrders(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) LocalDateTime dateTimeStart,
            @RequestParam(required = false) LocalDateTime dateTimeEnd,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(name = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(name = "direction", defaultValue = "asc")String direction,
            @RequestParam(name = "orderBy", defaultValue = "order_date") String orderBy) {
        return createObjectReturn(orderService.findFilteredOrders(id, clientId, dateTimeStart, dateTimeEnd,
                status, page, linesPerPage, direction, orderBy));
    }


    @GetMapping("/id")
    public ResponseEntity<?> findById(@RequestParam UUID id) {
        return createObjectReturn(orderService.findById(id));
    }

}
