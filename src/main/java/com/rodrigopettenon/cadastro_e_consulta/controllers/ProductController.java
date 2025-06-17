package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ProductDto;
import com.rodrigopettenon.cadastro_e_consulta.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/products")
public class ProductController extends BaseController{

    @Autowired
    private ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<?> saveProduct(@RequestBody ProductDto productDto) {
        productService.saveProduct(productDto);
        return createObjectReturn(productDto);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllProducts(
            @RequestParam(defaultValue = "0")Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {
        return createObjectReturn(productService.findAllProducts(page, linesPerPage, direction, orderBy));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> findFilteredProducts(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "sku", defaultValue = "") String sku,
            @RequestParam(value = "minPrice", defaultValue = "") Double minPrice,
            @RequestParam(value = "maxPrice", defaultValue = "")Double maxPrice,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "asc")String direction,
            @RequestParam(value = "orderBy", defaultValue = "name")String orderBy) {
        return createObjectReturn(productService.findFilteredProducts(name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy));
    }

    @GetMapping("/sku")
    public ResponseEntity<?> findBySku(@RequestParam String sku) {
        return createObjectReturn(productService.findBySku(sku));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateBySku(@RequestParam String sku, @RequestBody ProductDto productDto) {
        return createObjectReturn(productService.updateBySku(sku, productDto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteBySku(@RequestParam String sku) {
        productService.deleteBySku(sku);
        return createObjectReturn("Produto deletado com sucesso.");
    }
}
