package com.rodrigopettenon.cadastro_e_consulta.services;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ItemOrderDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.ItemOrderModel;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderModel;
import com.rodrigopettenon.cadastro_e_consulta.models.ProductModel;
import com.rodrigopettenon.cadastro_e_consulta.repositories.ItemOrderRepository;
import com.rodrigopettenon.cadastro_e_consulta.repositories.OrderRepository;
import com.rodrigopettenon.cadastro_e_consulta.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ItemOrderService {

    @Autowired
    private ItemOrderRepository itemOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ItemOrderDto saveItemOrder(ItemOrderDto itemOrderDto) {

        validateOrderId(itemOrderDto.getOrderId());
        validateProductId(itemOrderDto.getProductId());
        validateQuantity(itemOrderDto.getQuantity());

        OrderModel orderModel = orderRepository.findOrderModelById(itemOrderDto.getOrderId());
        ProductModel productModel = productRepository.findProductModelById(itemOrderDto.getProductId());

        ItemOrderModel itemOrderModel = new ItemOrderModel();
        itemOrderModel.setOrder(orderModel);
        itemOrderModel.setProduct(productModel);
        itemOrderModel.setQuantity(itemOrderDto.getQuantity());
        itemOrderModel.setPrice(productModel.getPrice());

        return itemOrderRepository.saveItemOrder(itemOrderModel);
    }

    private void validateOrderId(UUID orderId) {
        if (isNull(orderId)) {
            throw new ClientErrorException("O id do pedido é obrigatório.");
        }
        if (!orderRepository.existsOrderById(orderId)) {
            throw new ClientErrorException("O id do pedido informado não está cadastrado.");
        }
    }

    private void validateProductId(UUID productId) {
        if(isNull(productId)) {
            throw new ClientErrorException("O id do produto é obrigatório.");
        }
        if (!productRepository.existsProductById(productId)) {
            throw new ClientErrorException("O id do produto informado não está cadastrado.");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (isNull(quantity)) {
            throw new ClientErrorException("A quantidade do item do pedido é obrigatória.");
        }
        if (quantity <= 0 ) {
            throw new ClientErrorException("A quantidade do item do pedido deve ser maior que 0.");
        }
    }

}
