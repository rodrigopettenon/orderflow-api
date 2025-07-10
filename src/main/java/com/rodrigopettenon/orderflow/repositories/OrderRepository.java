package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.*;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ClientModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static com.rodrigopettenon.orderflow.utils.LogUtil.*;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
                logOrderNotFoundById(id);
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

    public OrderModel findOrderModelById(UUID id) {
        try {
            String sql = (" SELECT id, client_id, order_date, status FROM tb_orders WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<Object[]> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                throw new ClientErrorException("Pedido não encontrado com o id informado.");
            }

            Object[] result = resultList.get(0);

            ClientModel clientModelFound = findClientModelByOrderId(id);

            OrderModel orderModelFound = new OrderModel();
            orderModelFound.setId(UUID.fromString((String) result[0]));
            orderModelFound.setClient(clientModelFound);
            orderModelFound.setOrderDate(((Timestamp) result[2]).toLocalDateTime());
            orderModelFound.setStatus(OrderStatus.valueOf(((String) result[3])));
            return orderModelFound;
        } catch (Exception e) {
            logUnexpectedErrorOnFindOrderModelById(id, e);
            throw new ClientErrorException("Erro ao buscar o pedido pelo id.");
        }
    }

    private ClientModel findClientModelByOrderId(UUID id) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT c.id, c.name, c.email, c.cpf, c.birth_date ");
            sql.append(" FROM tb_orders o JOIN tb_clients c ");
            sql.append(" ON o.client_id = c.id ");
            sql.append(" WHERE o.id = :id ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("id", id.toString());

            List<Object[]> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                logClientNotFoundByOrderId(id);
                throw new ClientErrorException("Não foi encontrado nenhum cliente vinculado ao pedido com o id informado.");
            }

            Object[] result = resultList.get(0);
            ClientModel clientModelFound = new ClientModel();
            clientModelFound.setId(((Number) result[0]).longValue());
            clientModelFound.setName((String) result[1]);
            clientModelFound.setEmail((String) result[2]);
            clientModelFound.setCpf((String) result[3]);
            clientModelFound.setBirth(((Date) result[4]).toLocalDate());

            return clientModelFound;
        } catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar cliente pelo id do pedido.");
        }
    }

    public Boolean existsOrderById(UUID id) {
        try {
            logCheckExistenceOfOrderByIdStart(id);
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

    public Boolean existsOrderByClientId(Long clientId) {
        try {
            logCheckExistenceOfOrderByClientIdStart(clientId);
            String sql = (" SELECT 1 FROM tb_orders WHERE client_id = :clientId LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("clientId", clientId);

            List<?> resultList = query.getResultList();

            return !resultList.isEmpty();
        } catch (Exception e) {
            logUnexpectedErrorCheckingExistenceOfOrderByClientId(clientId, e);
            throw new ClientErrorException("Erro ao verificar existencia do pedido pelo id do cliente.");
        }
    }

    public GlobalPageDto<OrderDto> findFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                            LocalDateTime dateTimeEnd, String status, Integer page,
                                            Integer linesPerPage, String direction, String orderBy) {

            Long total = queryCountFilteredOrders(id, clientId, dateTimeStart, dateTimeEnd, status);

            List<OrderDto> results = queryFindFilteredOrders(id, clientId, dateTimeStart,
                    dateTimeEnd, status, page, linesPerPage, direction, orderBy);

            GlobalPageDto<OrderDto> orderPageDto = new GlobalPageDto<>();
            orderPageDto.setTotal(total);
            orderPageDto.setItems(results);

            return orderPageDto;
    }

    private List<OrderDto> queryFindFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                                   LocalDateTime dateTimeEnd, String status, Integer page,
                                                   Integer linesPerPage, String direction, String orderBy) {
        try{
            logQueryFindFilteredOrdersStart();

            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT id, client_id, order_date, status FROM tb_orders WHERE 1=1");
            if (nonNull(id)) {
                sql.append(" AND id = :id ");
                parameters.put("id", id.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND client_id = :client_id ");
                parameters.put("client_id", clientId);
            }
            if (nonNull(dateTimeStart) && nonNull(dateTimeEnd)) {
                sql.append(" AND order_date >= :dateTimeStart AND order_date < :dateTimeEnd ");
                parameters.put("dateTimeStart", dateTimeStart);
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (isNotBlank(status)) {
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

    private Long queryCountFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                          LocalDateTime dateTimeEnd, String status) {
        try {
            logQueryCountFilteredOrdersStart();

            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT COUNT(*) FROM tb_orders WHERE 1=1 ");

            if (nonNull(id)) {
                sql.append(" AND id = :id ");
                parameters.put("id", id.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND client_id = :client_id ");
                parameters.put("client_id", clientId);
            }
            if (nonNull(dateTimeStart) && nonNull(dateTimeEnd)) {
                sql.append(" AND order_date >= :dateTimeStart AND order_date < :dateTimeEnd ");
                parameters.put("dateTimeStart", dateTimeStart);
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (isNotBlank(status)) {
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

    public GlobalPageDto<GlobalFullDetailsDto> findFilteredOrdersDetails(UUID orderId, Long clientId, LocalDateTime dateTimeStart,
                                                                         LocalDateTime dateTimeEnd, Integer minQuantity, Integer maxQuantity,
                                                                         String status, Integer page, Integer linesPerPage, String direction, String orderBy) {

        Long total = queryCountFilteredOrdersDetails(orderId, clientId, dateTimeStart, dateTimeEnd, minQuantity,
                maxQuantity, status);

        List<GlobalFullDetailsDto> ordersDetailsList = queryFindFilteredOrdersDetails(orderId, clientId, dateTimeStart, dateTimeEnd, minQuantity,
                maxQuantity, status, page, linesPerPage, direction, orderBy);

        GlobalPageDto<GlobalFullDetailsDto> ordersDetailsPage = new GlobalPageDto<>();
        ordersDetailsPage.setTotal(total);
        ordersDetailsPage.setItems(ordersDetailsList);

        return ordersDetailsPage;
    }

    private Long queryCountFilteredOrdersDetails(UUID orderId, Long clientId, LocalDateTime dateTimeStart,
                                                 LocalDateTime dateTimeEnd, Integer minQuantity, Integer maxQuantity, String status) {
        try{
            logQueryCountFilteredOrdersDetailsStart();

            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT COUNT(*) ");
            sql.append(" FROM tb_orders o JOIN tb_item_orders i JOIN tb_clients c ");
            sql.append(" ON i.order_id = o.id AND o.client_id = c.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(orderId)) {
                sql.append(" AND o.id = :orderId ");
                parameters.put("orderId", orderId.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }
            if (isNotBlank(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }
            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(minQuantity)) {
                sql.append(" AND i.quantity >= :minQuantity ");
                parameters.put("minQuantity", minQuantity);
            }
            if (nonNull(maxQuantity)) {
                sql.append(" AND i.quantity <= :maxQuantity ");
                parameters.put("maxQuantity", maxQuantity);
            }

            Query query = em.createNativeQuery(sql.toString());

            setQueryParameters(parameters, query);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();

        } catch (Exception e) {
            logUnexpectedErrorOnCountFilteredOrdersDetails(e);
            throw new ClientErrorException("Erro ao contar pedidos com detalhes filtrados.");
        }
    }

    private List<GlobalFullDetailsDto> queryFindFilteredOrdersDetails(UUID orderId, Long clientId, LocalDateTime dateTimeStart,
                                                                      LocalDateTime dateTimeEnd, Integer minQuantity, Integer maxQuantity,
                                                                      String status, Integer page, Integer linesPerPage, String direction, String orderBy) {
        try {
            logQueryFindFilteredOrdersDetailsStart();

            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT o.id, o.order_date, o.status, ");
            sql.append(" c.id, c.name, c.email, ");
            sql.append(" i.quantity, ROUND(i.price * i.quantity, 2) total_price ");
            sql.append(" FROM tb_orders o JOIN tb_item_orders i JOIN tb_clients c ");
            sql.append(" ON i.order_id = o.id AND o.client_id = c.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(orderId)) {
                sql.append(" AND o.id = :orderId ");
                parameters.put("orderId", orderId.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }
            if (isNotBlank(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }
            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(minQuantity)) {
                sql.append(" AND i.quantity >= :minQuantity ");
                parameters.put("minQuantity", minQuantity);
            }
            if (nonNull(maxQuantity)) {
                sql.append(" AND i.quantity <= :maxQuantity ");
                parameters.put("maxQuantity", maxQuantity);
            }

            sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            setQueryParameters(parameters, query);

            List<Object[]> resultList = query.getResultList();
            List<GlobalFullDetailsDto> orderDetailsList = new ArrayList<>();

            for (Object[] result : resultList) {
                GlobalFullDetailsDto orderDetails = new GlobalFullDetailsDto();

                OrderDto orderDto = new OrderDto();
                orderDto.setId(UUID.fromString((String) result[0]));
                orderDto.setOrderDate(((Timestamp) result[1]).toLocalDateTime());
                orderDto.setStatus((String) result[2]);

                orderDetails.setOrder(orderDto);

                ClientDto clientDto = new ClientDto();
                clientDto.setId(((Number) result[3]).longValue());
                clientDto.setName((String) result[4]);
                clientDto.setEmail((String) result[5]);

                orderDetails.setClient(clientDto);

                ItemOrderDto itemOrderDto = new ItemOrderDto();
                itemOrderDto.setQuantity(((Number) result[6]).intValue());
                itemOrderDto.setTotalPrice(((Number) result[7]).doubleValue());

                orderDetails.setItemOrder(itemOrderDto);

                orderDetailsList.add(orderDetails);
            }

            return orderDetailsList;
        } catch (Exception e) {
            logUnexpectedErrorOnFindFilteredOrdersDetails(e);
            throw new ClientErrorException("Erro ao buscar detalhes dos pedidos filtrados.");
        }
    }

    public GlobalPageDto<RelevantOrderDataDto> findFilteredRelevantOrderData(Long clientId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                                             String status, Integer page, Integer linesPerPage, String direction, String orderBy) {

        Long total = queryCountFilteredRelevantOrderData(clientId, dateTimeStart, dateTimeEnd, status);

        List<RelevantOrderDataDto> results = queryFindFilteredRelevantOrderData(clientId, dateTimeStart, dateTimeEnd, status, page, linesPerPage, direction, orderBy);

        GlobalPageDto<RelevantOrderDataDto> relevantOrderDataPage = new GlobalPageDto<>();
        relevantOrderDataPage.setTotal(total);
        relevantOrderDataPage.setItems(results);

        return relevantOrderDataPage;

    }

    private Long queryCountFilteredRelevantOrderData(Long clientId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, String status) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT COUNT(*) ");
            sql.append(" FROM tb_orders o ");
            sql.append(" JOIN tb_clients c ON o.client_id = c.id ");
            sql.append(" JOIN tb_item_orders i ON i.order_id = o.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }
            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }

            Query query = em.createNativeQuery(sql.toString());

            setQueryParameters(parameters, query);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();
        }catch (Exception e) {
            throw new ClientErrorException("Erro ao contar pedidos com dados relevantes filtrados.");
        }
     }

    private List<RelevantOrderDataDto> queryFindFilteredRelevantOrderData(Long clientId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                                          String status, Integer page, Integer linesPerPage, String direction, String orderBy) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT o.id, c.name, o.order_date, o.status, i.product_id, i.quantity, i.price ");
            sql.append(" FROM tb_orders o ");
            sql.append(" JOIN tb_clients c ON o.client_id = c.id ");
            sql.append(" JOIN tb_item_orders i ON i.order_id = o.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }
            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }

            sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            setQueryParameters(parameters, query);

            List<Object[]> resultList = query.getResultList();
            List<RelevantOrderDataDto> orderDataDtoList = new ArrayList<>();

            for (Object[] result : resultList) {
                RelevantOrderDataDto relevantOrderDataDto = new RelevantOrderDataDto();

                relevantOrderDataDto.setOrderId(UUID.fromString((String) result[0]));
                relevantOrderDataDto.setClientName((String) result[1]);
                relevantOrderDataDto.setOrderDate(((Timestamp) result[2]).toLocalDateTime());
                relevantOrderDataDto.setStatus((String) result[3]);
                relevantOrderDataDto.setProductId(UUID.fromString((String) result[4]));

                BigDecimal quantity = BigDecimal.valueOf(((Number) result[5]).intValue());
                BigDecimal unitPrice = BigDecimal.valueOf(((Number) result[6]).doubleValue());

                BigDecimal totalAmount = unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_EVEN);

                relevantOrderDataDto.setQuantity(quantity.intValue());
                relevantOrderDataDto.setTotalAmount(totalAmount);

                orderDataDtoList.add(relevantOrderDataDto);
            }

            return orderDataDtoList;
        }catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar pedidos com dados relevantes filtrados.");
        }
    }

    public GlobalPageDto<ClientSalesReportDto> findFilteredClientSalesReport(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, Integer minOrder,
                                                                             Integer maxOrder, String status, Integer page,
                                                                             Integer linesPerPage, String direction, String orderBy) {
        Long total = queryCountFilteredClientSalesReport(dateTimeStart, dateTimeEnd, minOrder,
                maxOrder, status);

        List<ClientSalesReportDto> results = queryFindFilteredClientSalesReport(dateTimeStart, dateTimeEnd, minOrder,
                maxOrder, status, page, linesPerPage, direction, orderBy);

        GlobalPageDto<ClientSalesReportDto> clientSalesReportPage = new GlobalPageDto<>();
        clientSalesReportPage.setTotal(total);
        clientSalesReportPage.setItems(results);

        return clientSalesReportPage;
    }

    private List<ClientSalesReportDto> queryFindFilteredClientSalesReport(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd, Integer minOrder,
                                                                          Integer maxOrder, String status, Integer page,
                                                                          Integer linesPerPage, String direction, String orderBy) {
        try{
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT c.id, c.name, COUNT(o.id), SUM(i.price * i.quantity) ");
            sql.append(" FROM tb_orders o ");
            sql.append(" JOIN tb_item_orders i ON i.order_id = o.id ");
            sql.append(" JOIN tb_clients c ON c.id = o.client_id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }

            sql.append(" GROUP BY c.id ");
            sql.append(" HAVING 1=1 ");

            if (nonNull(minOrder)) {
                sql.append(" AND COUNT(o.id) >= :minOrder ");
                parameters.put("minOrder", minOrder);
            }
            if (nonNull(maxOrder)) {
                sql.append(" AND COUNT(o.id) <= :maxOrder ");
                parameters.put("maxOrder", maxOrder);
            }

            sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            setQueryParameters(parameters, query);

            List<Object[]> resultList = query.getResultList();
            List<ClientSalesReportDto> salesReportList = new ArrayList<>();

            for (Object[] result : resultList) {
                ClientSalesReportDto clientSalesReportDto = new ClientSalesReportDto();

                clientSalesReportDto.setClientId(((Number) result[0]).longValue());
                clientSalesReportDto.setClientName((String) result[1]);
                clientSalesReportDto.setTotalOrders(((Number) result[2]).intValue());

                BigDecimal totalAmount = BigDecimal.valueOf(((Number) result[3]).doubleValue())
                        .setScale(2, RoundingMode.HALF_EVEN);

                clientSalesReportDto.setTotalAmount(totalAmount);

                salesReportList.add(clientSalesReportDto);
            }

            return salesReportList;

        }catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar relatório de vendas filtrado.");
        }
    }

    private Long queryCountFilteredClientSalesReport(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                     Integer minOrder, Integer maxOrder, String status) {
        try{
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT COUNT(*) FROM ( ");
            sql.append(" SELECT c.id ");
            sql.append(" FROM tb_orders o ");
            sql.append(" JOIN tb_item_orders i ON i.order_id = o.id ");
            sql.append(" JOIN tb_clients c ON c.id = o.client_id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(dateTimeStart)) {
                sql.append(" AND o.order_date >= :dateTimeStart ");
                parameters.put("dateTimeStart", dateTimeStart);
            }
            if (nonNull(dateTimeEnd)) {
                sql.append(" AND o.order_date <= :dateTimeEnd ");
                parameters.put("dateTimeEnd", dateTimeEnd);
            }
            if (nonNull(status)) {
                sql.append(" AND o.status = :status ");
                parameters.put("status", status);
            }

            sql.append(" GROUP BY c.id ");
            sql.append(" HAVING 1=1 ");

            if (nonNull(minOrder)) {
                sql.append(" AND COUNT(o.id) >= :minOrder ");
                parameters.put("minOrder", minOrder);
            }
            if (nonNull(maxOrder)) {
                sql.append(" AND COUNT(o.id) <= :maxOrder ");
                parameters.put("maxOrder", maxOrder);
            }

            sql.append(" ) subquery ");

            Query query = em.createNativeQuery(sql.toString());

            setQueryParameters(parameters, query);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao contar quantos registros existem no relatório de vendas filtrado.");
        }
    }

    private void setQueryParameters(Map<String, Object> parameters, Query query) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }
}
