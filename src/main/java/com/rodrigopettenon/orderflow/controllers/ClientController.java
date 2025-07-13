package com.rodrigopettenon.orderflow.controllers;


import com.rodrigopettenon.orderflow.dtos.ClientDto;
import com.rodrigopettenon.orderflow.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/clients")
public class ClientController extends BaseController{

    @Autowired
    private ClientService clientService;


    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ClientDto clientDto){
        clientService.save(clientDto);
        return createObjectReturn(clientDto);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> findByEmail(@PathVariable(name = "email") String email) {
        return createObjectReturn(clientService.findByEmail(email));
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllClients(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "5") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        return createObjectReturn(clientService.findAllClients(page, linesPerPage, direction, orderBy));
    }

    @GetMapping("/filter")
    public ResponseEntity<?> findFilteredClients(
            @RequestParam(required = false) String  name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) LocalDate birthStart,
            @RequestParam(required = false) LocalDate birthEnd,
            @RequestParam(defaultValue = "0")Integer page,
            @RequestParam(value = "linesPerPage", defaultValue = "10") Integer linesPerPage,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        return createObjectReturn(clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd, page, linesPerPage, direction, orderBy));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<?> findByCPF(@PathVariable(name = "cpf") String cpf) {
        return createObjectReturn(clientService.findByCpf(cpf));
    }

    @PutMapping("/update/{cpf}")
    public ResponseEntity<?> updateByCpf(@PathVariable(name = "cpf") String cpf, @RequestBody ClientDto clientDto) {
        return createObjectReturn(clientService.updateByCpf(cpf, clientDto));
    }

    @DeleteMapping("/delete/{cpf}")
    public ResponseEntity<?> deleteByCpf(@PathVariable(name = "cpf") String cpf) {
        clientService.deleteByCpf(cpf);
        return createObjectReturn("Cliente deletado com sucesso");
    }


}
