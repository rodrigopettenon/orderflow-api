package com.rodrigopettenon.cadastro_e_consulta.services;

import com.rodrigopettenon.cadastro_e_consulta.dtos.GlobalPageDto;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ItemOrderService {

    private static final List<String> ALLOWED_DIRECTION = Arrays.asList("asc", "desc");
    private static final List<String> ALLOWED_ORDER_BY = Arrays.asList("id", "order_id", "product_id", "quantity", "price");

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

    public GlobalPageDto<ItemOrderDto> findFilteredItemOrders(UUID id, UUID orderId, UUID productId, Integer minQuantity,
                                                              Integer maxQuantity, Integer page, Integer linesPerPage,
                                                              String direction, String orderBy) {
        validateFilteredItemOrdersId(id);
        validateFilteredOrderId(orderId);
        validateFilteredProductId(productId);
        validateFilteredMinQuantity(minQuantity, maxQuantity);
        validateFilteredMaxQuantity(maxQuantity, minQuantity);
        Integer fixedPage = fixPage(page);
        Integer fixedLinesPerPage = fixLinesPerPage(linesPerPage);
        String fixedDirection = fixDirection(direction);
        String fixedOrderBy = fixOrderBy(orderBy);


        return itemOrderRepository.findFilteredItemOrders(id, orderId, productId, minQuantity, maxQuantity,
                fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    private String fixOrderBy(String orderBy) {
        if (isBlank(orderBy) || !ALLOWED_ORDER_BY.contains(orderBy.toLowerCase())) {
            return "order_id";
        }
        return orderBy;
    }

    private String fixDirection(String direction) {
        if (isBlank(direction) || !ALLOWED_DIRECTION.contains(direction.toLowerCase())) {
            return "asc";
        }
        return direction;
    }

    private Integer fixPage(Integer page) {
        if (isNull(page) || page < 0) {
            return 0;
        }
        return page;
    }

    private Integer fixLinesPerPage(Integer linesPerPage) {
        if (isNull(linesPerPage) || linesPerPage <= 0) {
            return 10;
        }
        return linesPerPage;
    }

    private void validateFilteredMaxQuantity(Integer maxQuantity, Integer minQuantity) {
        if (!isNull(maxQuantity)) {
            if (maxQuantity <= 0) {
                throw new ClientErrorException("A quantidade máxima do item do pedido deve ser maior que 0.");
            }
            if (!isNull(minQuantity) && maxQuantity < minQuantity) {
                throw new ClientErrorException("A quantidade máxima não pode ser menor que a quantidade mínima.");
            }
        }
    }

    private void validateFilteredMinQuantity(Integer minQuantity, Integer maxQuantity) {
        if (!isNull(minQuantity)) {
            if (minQuantity <= 0) {
                throw new ClientErrorException("A quantidade mínima do item do pedido deve ser maior que 0.");
            }
            if (!isNull(maxQuantity) && minQuantity > maxQuantity) {
                throw new ClientErrorException("A quantidade mínima não pode ser maior que a quantidade máxima.");
            }
        }
    }

    private void validateFilteredItemOrdersId(UUID id) {
        if (!isNull(id)) {
            if (!itemOrderRepository.existsItemOrderById(id)) {
                throw new ClientErrorException("O ID do item do pedido informado não está cadastrado.");
            }
        }
    }

    private void validateFilteredOrderId(UUID orderId) {
        if (!isNull(orderId)) {
            if (!orderRepository.existsOrderById(orderId)) {
                throw new ClientErrorException("O ID do pedido informado não está cadastrado.");
            }
        }
    }

    private void validateFilteredProductId(UUID productId) {
        if (!isNull(productId)) {
            if (!productRepository.existsProductById(productId)) {
                throw new ClientErrorException("O ID do produto informado não está cadastrado.");
            }
        }
    }

    private void validateOrderId(UUID orderId) {
        if (isNull(orderId)) {
            throw new ClientErrorException("O ID do pedido é obrigatório.");
        }
        if (!orderRepository.existsOrderById(orderId)) {
            throw new ClientErrorException("O ID do pedido informado não está cadastrado.");
        }
    }

    private void validateProductId(UUID productId) {
        if(isNull(productId)) {
            throw new ClientErrorException("O ID do produto é obrigatório.");
        }
        if (!productRepository.existsProductById(productId)) {
            throw new ClientErrorException("O ID do produto informado não está cadastrado.");
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
