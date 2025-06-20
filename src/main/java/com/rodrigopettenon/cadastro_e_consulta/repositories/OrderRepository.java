package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public OrderDto saveOrder(OrderModel orderModel) {
        try {
            UUID id = UUID.randomUUID();

            StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO tb_order (id, client_id, order_date, status) ");
            sql.append(" VALUES (:id, :client_id, :order_date, :status) ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("id", id.toString())
                    .setParameter("client_id", orderModel.getClient().getId())
                    .setParameter("order_date", orderModel.getOrderDate())
                    .setParameter("status", orderModel.getStatus().toString());

            query.executeUpdate();

            OrderDto newOrder = new OrderDto();
            newOrder.setId(id);
            newOrder.setClientId(orderModel.getClient().getId());
            newOrder.setOrderDate(orderModel.getOrderDate());
            newOrder.setStatus(orderModel.getStatus().toString());

            return newOrder;

        }catch (Exception e) {
            throw new ClientErrorException("Erro ao cadastrar pedido");
        }
    }
}
