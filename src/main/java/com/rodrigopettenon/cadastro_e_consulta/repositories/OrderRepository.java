package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
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

    public OrderDto findOrderById(UUID id) {
        try {
            String sql = (" SELECT id, client_id, order_date, status FROM tb_order WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<Object[]> resultList = query.getResultList();
            Object[] result = resultList.get(0);

            OrderDto orderDto = new OrderDto();
            orderDto.setId(UUID.fromString(((String) result[0])));
            orderDto.setClientId(((Number) result[1]).longValue());
            orderDto.setOrderDate(((Timestamp) result[2]).toLocalDateTime());
            orderDto.setStatus((String) result[3]);

            return orderDto;

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar pedido pelo id");
        }
    }

    public Boolean existsOrderById(UUID id) {
        try {
            String sql = (" SELECT 1 FROM tb_order WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<?> resultList = query.getResultList();

            return !resultList.isEmpty();
        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existencia pelo id: " + id);
        }
    }
}
