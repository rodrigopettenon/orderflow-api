package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ItemOrderDto;
import com.rodrigopettenon.orderflow.dtos.ItemOrderFullDetailsDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ItemOrderModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.ProductModel;
import com.rodrigopettenon.orderflow.repositories.ClientRepository;
import com.rodrigopettenon.orderflow.repositories.ItemOrderRepository;
import com.rodrigopettenon.orderflow.repositories.OrderRepository;
import com.rodrigopettenon.orderflow.repositories.ProductRepository;
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
    private static final List<String> ALLOWED_ORDER_BY_FULL_DETAILS = Arrays.asList("i.quantity", "i.price", "p.price", "p.name", "o.order_date", "c.name");

    @Autowired
    private ItemOrderRepository itemOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

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

    public GlobalPageDto<ItemOrderFullDetailsDto> findFullDetailsItemOrders(UUID itemOrderId, UUID productId, UUID orderId,
                                                                            Long clientId, Integer page, Integer linesPerPage,
                                                                            String direction, String orderBy) {
        validateFilteredItemOrdersId(itemOrderId);
        validateFilteredFullDetailsProductId(productId);
        validateFilteredFullDetailsOrderId(orderId);
        validateFilteredFullDetailsClientId(clientId);
        Integer fixedPage = fixPage(page);
        Integer fixedLinesPerPage = fixLinesPerPage(linesPerPage);
        String fixedDirection = fixDirection(direction);
        String fixedOrderBy = fixOrderByFullDetails(orderBy);

        return itemOrderRepository.findFullDetailsItemOrders(itemOrderId, productId, orderId, clientId, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    private String fixOrderByFullDetails(String orderBy) {
        if (isBlank(orderBy) || !ALLOWED_ORDER_BY_FULL_DETAILS.contains(orderBy.toLowerCase())) {
            return "i.quantity";
        }
        return orderBy;
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

    private void validateFilteredFullDetailsOrderId(UUID orderId) {
        validateFilteredOrderId(orderId);
        if (!isNull(orderId)) {
            if (!itemOrderRepository.existsItemOrderByOrderId(orderId)) {
                throw new ClientErrorException("Nenhum item de pedido cadastrado com o ID do pedido informado.");
            }
        }
    }

    private void validateFilteredFullDetailsProductId(UUID productId) {
        validateFilteredProductId(productId);
        if (!isNull(productId)) {
            if (!itemOrderRepository.existsItemOrderByProductId(productId)) {
                throw new ClientErrorException("Nenhum item de pedido cadastrado com o ID do produto informado.");
            }
        }
    }

    private void validateFilteredFullDetailsClientId(Long clientId) {
        if (!isNull(clientId)) {
            if (!clientRepository.existsClientById(clientId)) {
                throw new ClientErrorException("O ID do cliente informado não está cadastrado.");
            }
            if (!orderRepository.existsOrderByClientId(clientId)) {
                throw new ClientErrorException("Nenhum pedido cadastrado com o ID do cliente informado.");
            }
            if (!itemOrderRepository.existsItemOrderByClientId(clientId)) {
                throw new ClientErrorException("Nenhum item de pedido cadastrado com ID do cliente informado.");
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
