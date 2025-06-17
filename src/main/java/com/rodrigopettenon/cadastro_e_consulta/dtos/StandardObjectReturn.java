package com.rodrigopettenon.cadastro_e_consulta.dtos;

import java.io.Serializable;
import java.time.Instant;

public class StandardObjectReturn implements Serializable {

    private static final long serialVersionUID = 9091767880286143428L;

    private Instant timestamp;
    private Integer status;
    private String message;
    private Object object;

    public StandardObjectReturn() {
    }

    public StandardObjectReturn(Instant timestamp, Integer status, String message, Object object) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.object = object;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
