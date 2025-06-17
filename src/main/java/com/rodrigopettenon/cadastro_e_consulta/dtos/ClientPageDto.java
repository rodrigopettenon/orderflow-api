package com.rodrigopettenon.cadastro_e_consulta.dtos;

import java.io.Serializable;
import java.util.List;

public class ClientPageDto implements Serializable {

    private static final long serialVersionUID = 7474230723819346266L;

    private Long total;
    private List<ClientDto> clients;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<ClientDto> getClients() {
        return clients;
    }

    public void setClients(List<ClientDto> clients) {
        this.clients = clients;
    }
}
