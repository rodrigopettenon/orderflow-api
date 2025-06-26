package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ItemOrderDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.ItemOrderModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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
}
