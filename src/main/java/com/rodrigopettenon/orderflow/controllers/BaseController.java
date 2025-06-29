package com.rodrigopettenon.orderflow.controllers;

import com.rodrigopettenon.orderflow.dtos.StandardObjectReturn;
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
