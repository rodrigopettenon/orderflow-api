package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ItemOrderDto;
import com.rodrigopettenon.cadastro_e_consulta.services.ItemOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/item-orders")
public class ItemOrderController extends BaseController{

    @Autowired
    private ItemOrderService itemOrderService;

    @PostMapping("/save")
    public ResponseEntity<?> saveItemOrder(@RequestBody ItemOrderDto itemOrderDto) {
        return createObjectReturn(itemOrderService.saveItemOrder(itemOrderDto));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> findFilteredItemOrders(@RequestParam(required = false) UUID id,
                                                    @RequestParam(required = false) UUID orderId,
                                                    @RequestParam(required = false) UUID productId,
                                                    @RequestParam(required = false) Integer minQuantity,
                                                    @RequestParam(required = false) Integer maxQuantity,
                                                    @RequestParam(defaultValue = "0") Integer page,
                                                    @RequestParam(name = "linesPerPage", defaultValue = "10") Integer linesPerPage,
                                                    @RequestParam(name = "direction", defaultValue = "asc") String direction,
                                                    @RequestParam(name = "orderBy", defaultValue = "order_id") String orderBy) {

        return createObjectReturn(itemOrderService.findFilteredItemOrders(id, orderId, productId, minQuantity, maxQuantity, page, linesPerPage, direction, orderBy));
    }

}
