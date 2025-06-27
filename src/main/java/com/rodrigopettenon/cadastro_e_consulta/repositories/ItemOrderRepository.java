package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.GlobalPageDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.ItemOrderDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.ItemOrderModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Objects.isNull;

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
            throw new ClientErrorException("Erro ao verificar existência do item do pedido.");
        }
    }

    public GlobalPageDto<ItemOrderDto> findFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                                              Integer maxQuantity, Integer page, Integer linesPerPage,
                                                              String direction, String orderBy) {

        List<ItemOrderDto> items = queryFindFilteredItemOrders(id, orderId, productId, minQuantity,
                maxQuantity, page, linesPerPage, direction, orderBy);

        GlobalPageDto<ItemOrderDto> itemOrdersPage = new GlobalPageDto<>();
        itemOrdersPage.setItems(items);

        return itemOrdersPage;

    }

    private List<ItemOrderDto> queryFindFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                                           Integer maxQuantity, Integer page, Integer linesPerPage,
                                                           String direction, String orderBy) {
        Map<String, Object> parameters = new HashMap<>();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT id, order_id, product_id, quantity, price FROM tb_item_orders WHERE 1=1 ");

        if (!isNull(id)) {
            sql.append(" AND id = :id ");
            parameters.put("id", id.toString());
        }
        if (!isNull(orderId)) {
            sql.append(" AND order_id = :order_id ");
            parameters.put("order_id", orderId.toString());
        }
        if (!isNull(productId)) {
            sql.append(" AND product_id = :product_id ");
            parameters.put("product_id", productId.toString());
        }
        if (!isNull(minQuantity)) {
            sql.append(" AND quantity >= :minQuantity ");
            parameters.put("minQuantity", minQuantity);
        }
        if (!isNull((maxQuantity))) {
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

    private void setQueryParameters(Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }
}
