package com.rodrigopettenon.cadastro_e_consulta.controllers;

import com.rodrigopettenon.cadastro_e_consulta.dtos.StandardObjectReturn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BaseController {

    protected ResponseEntity<StandardObjectReturn> createObjectReturn(Object object) {
        return ResponseEntity.ok().body(new StandardObjectReturn(Instant.now(), HttpStatus.CREATED.value() , null, object));
    }
}
