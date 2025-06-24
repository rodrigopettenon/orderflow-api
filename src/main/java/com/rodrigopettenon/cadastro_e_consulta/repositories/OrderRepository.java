package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderPageDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderModel;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.rodrigopettenon.cadastro_e_consulta.utils.LogUtil.*;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Repository
public class OrderRepository {

    @PersistenceContext
    private EntityManager em;

    public OrderDto saveOrder(OrderModel orderModel) {
        try {
            UUID id = UUID.randomUUID();

            StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO tb_orders (id, client_id, order_date, status) ");
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

            logSaveOrderSuccessfully(id, newOrder.getClientId(), newOrder.getOrderDate(), newOrder.getStatus());
            return newOrder;
        }catch (Exception e) {
            logUnexpectedErrorOnSaveOrder(e);
            throw new ClientErrorException("Erro ao cadastrar pedido");
        }
    }

    public void updateStatusById(UUID id, OrderStatus newStatus) {
        try {
            String sql = (" UPDATE tb_orders SET status = :newStatus WHERE id = :id ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("newStatus", newStatus)
                    .setParameter("id", id.toString());

            query.executeUpdate();
        } catch (Exception e) {
            logUnexpectedErrorOnUpdateOrderStatusById(id, e);
            throw new ClientErrorException("Erro ao atualizar o status do pedido pelo id.");
        }
    }

    public OrderDto findOrderById(UUID id) {
        try {
            String sql = (" SELECT id, client_id, order_date, status FROM tb_orders WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<Object[]> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                throw new ClientErrorException("Pedido não encontrado com o id informado.");
            }

            Object[] result = resultList.get(0);

            OrderDto orderDto = new OrderDto();
            orderDto.setId(UUID.fromString(((String) result[0])));
            orderDto.setClientId(((Number) result[1]).longValue());
            orderDto.setOrderDate(((Timestamp) result[2]).toLocalDateTime());
            orderDto.setStatus((String) result[3]);

            logFindOrderByIdSuccessfully(id);
            return orderDto;
        } catch (Exception e) {
            logUnexpectedErrorOnFindOrderById(id, e);
            throw new ClientErrorException("Erro ao buscar pedido pelo id");
        }
    }

    public Boolean existsOrderById(UUID id) {
        try {
            String sql = (" SELECT 1 FROM tb_orders WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<?> resultList = query.getResultList();

            logCheckingExistenceOfOrderByIdSuccessfully(id);
            return !resultList.isEmpty();
        } catch (Exception e) {
            logUnexpectedErrorCheckingExistenceOfOrderById(id, e);
            throw new ClientErrorException("Erro ao verificar existencia pelo id: " + id);
        }
    }

    public OrderPageDto findFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                       LocalDateTime dateTimeEnd, String status, Integer page,
                                       Integer linesPerPage, String direction, String orderBy) {

            Long total = queryCountTotalFiltered(id, clientId, dateTimeStart, dateTimeEnd, status);

            List<OrderDto> results = queryFindFilteredOrders(id, clientId, dateTimeStart,
                    dateTimeEnd, status, page, linesPerPage, direction, orderBy);

            OrderPageDto orderPageDto = new OrderPageDto();
            orderPageDto.setTotal(total);
            orderPageDto.setOrders(results);

            return orderPageDto;
    }

    private List<OrderDto> queryFindFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                                   LocalDateTime dateTimeEnd, String status, Integer page,
                                                   Integer linesPerPage, String direction, String orderBy) {
        try{
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT id, client_id, order_date, status FROM tb_orders WHERE 1=1");
            if (!isNull(id)) {
                sql.append(" AND id = :id ");
                parameters.put("id", id.toString());
            }
            if (!isNull(clientId)) {
                sql.append(" AND client_id = :client_id ");
                parameters.put("client_id", clientId);
            }
            if (!isNull(dateTimeStart) && !isNull(dateTimeEnd)) {
                sql.append(" AND order_date >= :dateTimeStart AND order_date < :dateTimeEnd ");
                parameters.put("dateTimeStart", dateTimeStart);
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (!isBlank(status)) {
                sql.append(" AND status = :status ");
                parameters.put("status", status);
            }
            sql.append(" ORDER BY " + orderBy + " " + direction + " ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            setQueryParameters(parameters, query);

            List<Object[]> resultList = query.getResultList();
            List<OrderDto> results = new ArrayList<>();

            for (Object[] result : resultList) {
                OrderDto orderDto = new OrderDto();
                orderDto.setId(UUID.fromString((String) result[0]));
                orderDto.setClientId(((Number) result[1]).longValue());
                orderDto.setOrderDate(((Timestamp) result[2]).toLocalDateTime());
                orderDto.setStatus((String) result[3]);

                results.add(orderDto);
            }

            logFindFilteredOrdersSuccessfully();
            return results;
        } catch (Exception e) {
            logUnexpectedErrorOnFindFilteredOrders(e);
            throw new ClientErrorException("Erro ao buscar pedidos filtrados.");
        }

    }

    private Long queryCountTotalFiltered(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                             LocalDateTime dateTimeEnd, String status) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT COUNT(*) FROM tb_orders WHERE 1=1 ");

            if (!isNull(id)) {
                sql.append(" AND id = :id ");
                parameters.put("id", id.toString());
            }
            if (!isNull(clientId)) {
                sql.append(" AND client_id = :client_id ");
                parameters.put("client_id", clientId);
            }
            if (!isNull(dateTimeStart) && !isNull(dateTimeEnd)) {
                sql.append(" AND order_date >= :dateTimeStart AND order_date < :dateTimeEnd ");
                parameters.put("dateTimeStart", dateTimeStart);
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (!isBlank(status)) {
                sql.append(" AND status = :status ");
                parameters.put("status", status);
            }

            Query query = em.createNativeQuery(sql.toString());
            setQueryParameters(parameters, query);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            logCountFilteredOrdersSuccessfully();
            return total.longValue();
        } catch (Exception e) {
            logUnexpectedErrorOnCountFilteredOrders(e);
            throw new ClientErrorException("Erro ao contar total de pedidos filtrados.");
        }
    }

    private void setQueryParameters(Map<String, Object> parameters, Query query) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }

}
