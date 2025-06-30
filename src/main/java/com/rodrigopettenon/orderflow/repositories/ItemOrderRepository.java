package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.*;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ItemOrderModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;

import static java.util.Objects.nonNull;

@Repository
public class ItemOrderRepository {
    
    @PersistenceContext
    private EntityManager em;

    public ItemOrderDto saveItemOrder(ItemOrderModel itemOrderModel) {
        try {
            UUID id = UUID.randomUUID();

            StringBuilder sql = new StringBuilder();

            sql.append(" INSERT INTO tb_item_orders (id, order_id, product_id, quantity, price) ");
            sql.append(" VALUES (:id, :order_id, :product_id, :quantity, :price) ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("id", id.toString())
                    .setParameter("order_id", itemOrderModel.getOrder().getId().toString())
                    .setParameter("product_id", itemOrderModel.getProduct().getId().toString())
                    .setParameter("quantity", itemOrderModel.getQuantity())
                    .setParameter("price", itemOrderModel.getPrice());

            query.executeUpdate();

            ItemOrderDto itemOrderDto = new ItemOrderDto();

            itemOrderDto.setId(id);
            itemOrderDto.setOrderId(itemOrderModel.getOrder().getId());
            itemOrderDto.setProductId(itemOrderModel.getProduct().getId());
            itemOrderDto.setQuantity(itemOrderModel.getQuantity());
            itemOrderDto.setPrice(itemOrderModel.getPrice());

            return itemOrderDto;

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao salvar o item do pedido no banco de dados.");
        }
    }

    public Boolean existsItemOrderById(UUID id) {
        try {
            String sql = " SELECT 1 FROM tb_item_orders WHERE id = :id LIMIT 1 ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<?> resultList = query.getResultList();

            return !resultList.isEmpty();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existência do item do pedido pelo id.");
        }
    }

    public Boolean existsItemOrderByOrderId(UUID orderId) {
        try {
             String sql = (" SELECT 1 FROM tb_item_orders WHERE order_id = :orderId LIMIT 1 ");

             Query query = em.createNativeQuery(sql)
                     .setParameter("orderId", orderId.toString());

             List<?> resultList = query.getResultList();

             return !resultList.isEmpty();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existência do item do pedido pelo id do pedido.");
        }
    }

    public Boolean existsItemOrderByProductId(UUID productId) {
        try{
            String sql = (" SELECT 1 FROM tb_item_orders WHERE product_id = :productId LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("productId", productId.toString());

            List<?> resultList = query.getResultList();

            return !resultList.isEmpty();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existência do item do pedido pelo id do produto.");
        }
    }

    public Boolean existsItemOrderByClientId(Long clientId) {
        try{
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT 1 FROM tb_item_orders i JOIN tb_orders o JOIN tb_clients c ");
            sql.append(" ON i.order_id = o.id AND o.client_id = c.id ");
            sql.append(" WHERE c.id = :clientId LIMIT 1 ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("clientId", clientId);

            List<?> resultList = query.getResultList();

            return !resultList.isEmpty();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existência do item do pedido pelo id do cliente.");
        }
    }


    public GlobalPageDto<ItemOrderDto> findFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                                              Integer maxQuantity, Integer page, Integer linesPerPage,
                                                              String direction, String orderBy) {

        Long total = queryCountFilteredItemOrders(id, orderId, productId, minQuantity, maxQuantity);
        List<ItemOrderDto> items = queryFindFilteredItemOrders(id, orderId, productId, minQuantity,
                maxQuantity, page, linesPerPage, direction, orderBy);

        GlobalPageDto<ItemOrderDto> itemOrdersPage = new GlobalPageDto<>();
        itemOrdersPage.setTotal(total);
        itemOrdersPage.setItems(items);

        return itemOrdersPage;

    }

    private List<ItemOrderDto> queryFindFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                                           Integer maxQuantity, Integer page, Integer linesPerPage,
                                                           String direction, String orderBy) {
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT id, order_id, product_id, quantity, price FROM tb_item_orders WHERE 1=1 ");

        if (nonNull(id)) {
            sql.append(" AND id = :id ");
            parameters.put("id", id.toString());
        }
        if (nonNull(orderId)) {
            sql.append(" AND order_id = :order_id ");
            parameters.put("order_id", orderId.toString());
        }
        if (nonNull(productId)) {
            sql.append(" AND product_id = :product_id ");
            parameters.put("product_id", productId.toString());
        }
        if (nonNull(minQuantity)) {
            sql.append(" AND quantity >= :minQuantity ");
            parameters.put("minQuantity", minQuantity);
        }
        if (nonNull((maxQuantity))) {
            sql.append(" AND quantity <= :maxQuantity ");
            parameters.put("maxQuantity", maxQuantity);
        }

        sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
        sql.append(" LIMIT :limit OFFSET :offset");

        Query query = em.createNativeQuery(sql.toString())
                .setParameter("limit", linesPerPage)
                .setParameter("offset", page * linesPerPage);

        setQueryParameters(query, parameters);

        List<Object[]> resultList = query.getResultList();
        List<ItemOrderDto> itemOrdersList = new ArrayList<>();

        for (Object[] result : resultList) {
            ItemOrderDto itemOrderDto = new ItemOrderDto();
            itemOrderDto.setId(UUID.fromString((String) result[0]));
            itemOrderDto.setOrderId(UUID.fromString((String) result[1]));
            itemOrderDto.setProductId(UUID.fromString((String) result[2]));
            itemOrderDto.setQuantity(((Number) result[3]).intValue());
            itemOrderDto.setPrice(((Number) result[4]).doubleValue());

            itemOrdersList.add(itemOrderDto);
        }

        return itemOrdersList;
    }

    private Long queryCountFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                              Integer maxQuantity) {
        try {
            Map<String, Object> parameters = new HashMap<>();

            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT COUNT(*) FROM tb_item_orders WHERE 1=1 ");

            if (nonNull(id)) {
                sql.append(" AND id = :id ");
                parameters.put("id", id.toString());
            }
            if (nonNull(orderId)) {
                sql.append(" AND order_id = :order_id ");
                parameters.put("order_id", orderId.toString());
            }
            if (nonNull(productId)) {
                sql.append(" AND product_id = :product_id ");
                parameters.put("product_id", productId.toString());
            }
            if (nonNull(minQuantity)) {
                sql.append(" AND quantity >= :minQuantity ");
                parameters.put("minQuantity", minQuantity);
            }
            if (!nonNull((maxQuantity))) {
                sql.append(" AND quantity <= :maxQuantity ");
                parameters.put("maxQuantity", maxQuantity);
            }

            Query query = em.createNativeQuery(sql.toString());

            setQueryParameters(query, parameters);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();
        } catch (Exception e) {
            throw new ClientErrorException("Erro ao contar itens dos pedidos filtrados.");
        }

    }

    private void setQueryParameters(Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }

    public GlobalPageDto<GlobalFullDetailsDto> findFullDetailsItemOrders(UUID itemOrderId, UUID productId, UUID orderId,
                                                                         Long clientId, Integer page, Integer linePerPage,
                                                                         String direction, String orderBy) {

        Long total = queryCountFullFilteredItemOrderDetails(itemOrderId, productId, orderId, clientId);

        List<GlobalFullDetailsDto> itemOrderFullDetailsList = queryFindFullDetailsItemOrders(itemOrderId, productId, orderId,
                clientId, page, linePerPage, direction, orderBy);

        GlobalPageDto<GlobalFullDetailsDto> itemOrderFullDetailsPage = new GlobalPageDto<>();
        itemOrderFullDetailsPage.setTotal(total);
        itemOrderFullDetailsPage.setItems(itemOrderFullDetailsList);

        return itemOrderFullDetailsPage;
    }

    private List<GlobalFullDetailsDto> queryFindFullDetailsItemOrders(UUID itemOrderId, UUID productId, UUID orderId,
                                                                      Long clientId, Integer page, Integer linePerPage,
                                                                      String direction, String orderBy) {
        try{
            Map<String, Object> parameters = new HashMap<>();

            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT i.id, i.quantity, i.price, ");
            sql.append(" p.id, p.name, p.price, ");
            sql.append(" o.id, o.order_date, o.status, ");
            sql.append(" c.id, c.name, c.email ");
            sql.append(" FROM tb_item_orders i ");
            sql.append(" JOIN tb_products p ON i.product_id = p.id ");
            sql.append(" JOIN tb_orders o ON i.order_id = o.id ");
            sql.append(" JOIN tb_clients c ON o.client_id = c.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(itemOrderId)) {
                sql.append(" AND i.id = :itemOrderId ");
                parameters.put("itemOrderId", itemOrderId.toString());
            }
            if (nonNull(productId)) {
                sql.append(" AND p.id = :productId ");
                parameters.put("productId", productId.toString());
            }
            if (nonNull(orderId)) {
                sql.append(" AND o.id = :orderId ");
                parameters.put("orderId", orderId.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }
            sql.append(" ORDER BY ").append(orderBy).append(" ").append(direction).append(" ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linePerPage)
                    .setParameter("offset", page * linePerPage);

            setQueryParameters(query, parameters);

            List<Object[]> results = query.getResultList();
            List<GlobalFullDetailsDto> fullDetailsList = new ArrayList<>();

            for (Object[] result : results) {
                GlobalFullDetailsDto itemOrderFullDetailsDto = new GlobalFullDetailsDto();

                ItemOrderDto itemOrderDto = new ItemOrderDto();
                itemOrderDto.setId(UUID.fromString((String) result[0]));
                itemOrderDto.setQuantity(((Number) result[1]).intValue());
                itemOrderDto.setPrice(((Number) result[2]).doubleValue());

                itemOrderFullDetailsDto.setItemOrder(itemOrderDto);

                ProductDto productDto = new ProductDto();
                productDto.setId(UUID.fromString((String) result[3]));
                productDto.setName((String) result[4]);
                productDto.setPrice(((Number) result[5]).doubleValue());

                itemOrderFullDetailsDto.setProduct(productDto);

                OrderDto orderDto = new OrderDto();
                orderDto.setId(UUID.fromString((String) result[6]));
                orderDto.setOrderDate(((Timestamp) result[7]).toLocalDateTime());
                orderDto.setStatus((String) result[8]);

                itemOrderFullDetailsDto.setOrder(orderDto);

                ClientDto clientDto = new ClientDto();
                clientDto.setId(((Number) result[9]).longValue());
                clientDto.setName((String) result[10]);
                clientDto.setEmail((String) result[11]);

                itemOrderFullDetailsDto.setClient(clientDto);

                fullDetailsList.add(itemOrderFullDetailsDto);
            }

            return fullDetailsList;

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar item dos pedidos filtrados com todos detalhes.");
        }
    }

    private Long queryCountFullFilteredItemOrderDetails(UUID itemOrderId, UUID productId, UUID orderId,
                                                        Long clientId) {
        try{
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT COUNT(*) FROM tb_item_orders i ");
            sql.append(" JOIN tb_products p ON i.product_id = p.id ");
            sql.append(" JOIN tb_orders o ON i.order_id = o.id ");
            sql.append(" JOIN tb_clients c ON o.client_id = c.id ");
            sql.append(" WHERE 1=1 ");

            if (nonNull(itemOrderId)) {
                sql.append(" AND i.id = :itemOrderId ");
                parameters.put("itemOrderId", itemOrderId.toString());
            }
            if (nonNull(productId)) {
                sql.append(" AND p.id = :productId ");
                parameters.put("productId", productId.toString());
            }
            if (nonNull(orderId)) {
                sql.append(" AND o.id = :orderId ");
                parameters.put("orderId", orderId.toString());
            }
            if (nonNull(clientId)) {
                sql.append(" AND c.id = :clientId ");
                parameters.put("clientId", clientId);
            }

            Query query = em.createNativeQuery(sql.toString());

            setQueryParameters(query, parameters);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao contar todos items dos pedidos filtrados com todos detalhes.");
        }
    }
 }
