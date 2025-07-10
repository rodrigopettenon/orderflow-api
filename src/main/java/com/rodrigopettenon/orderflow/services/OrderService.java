package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.*;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ClientModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.OrderStatus;
import com.rodrigopettenon.orderflow.repositories.ClientRepository;
import com.rodrigopettenon.orderflow.repositories.ItemOrderRepository;
import com.rodrigopettenon.orderflow.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.rodrigopettenon.orderflow.utils.LogUtil.*;
import static com.rodrigopettenon.orderflow.utils.StringsValidation.removeAllSpaces;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderService{

    private static final List<String> ALLOWED_ORDER_BY = Arrays.asList("id", "client_id", "order_date", "status");
    private static final List<String> ALLOWED_DIRECTION = Arrays.asList("asc", "desc");
    private static final Map<String, String> ORDER_BY_COLUMN_MAP_FILTER = Map.of(
            "order_date", "o.order_date",
            "order_status", "o.status",
            "client_id", "c.id",
            "item_quantity", "i.quantity",
            "item_price", "i.price",
            "client_name", "c.name");
    private static final Map<String, String> ORDER_BY_COLUMN_MAP_SALES_REPORT = Map.of(
            "client_id", "c.id",
            "client_name", "c.name",
            "total_orders", "COUNT(o.id)",
            "total_amount", "SUM(i.price * i.quantity)");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ItemOrderRepository itemOrderRepository;

    @Autowired
    private ItemOrderService itemOrderService;

    @Transactional
    public OrderDto saveOrder(OrderDto orderDto) {
        logSaveOrderStart();
        validateClientId(orderDto.getClientId());
        OrderStatus validatedOrderStatus = validateOrderStatus(orderDto.getStatus());
        orderDto.setOrderDate(LocalDateTime.now());

        ClientModel client = clientRepository.findClientModelById(orderDto.getClientId());

        OrderModel orderModel = new OrderModel();
        orderModel.setClient(client);
        orderModel.setOrderDate(orderDto.getOrderDate());
        orderModel.setStatus(validatedOrderStatus);

        return orderRepository.saveOrder(orderModel);
    }

    public GlobalPageDto<OrderDto> findFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                            LocalDateTime dateTimeEnd, String status, Integer page,
                                            Integer linesPerPage, String direction, String orderBy) {
        logFindFilteredOrdersStart();

        Integer fixedPage = fixPageFilter(page);
        Integer fixedLinesPerPage = fixLinesPerPageFilter(linesPerPage);
        String fixedDirection = fixDirectionFilter(direction);
        String fixedOrderBy = fixOrderByFilter(orderBy);
        validateFilterOrderId(id);
        validateFilterClientId(clientId);
        validateFilterOrderDateTimeStartAndDateTimeEnd(dateTimeStart, dateTimeEnd);
        String validatedStatus = validateFilterOrderStatus(status);

        return orderRepository.findFilteredOrders(id, clientId, dateTimeStart, dateTimeEnd, validatedStatus, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    public OrderDto findById(UUID id) {
        logFindOrderByIdStart(id);
        validateOrderId(id);

        return orderRepository.findOrderById(id);
    }

    @Transactional
    public void updateOrderStatusById(UUID id, String status) {
        validateOrderId(id);
        OrderStatus newStatus = validateOrderStatus(status);
        validateNewStatusForUpdate(newStatus);

        OrderDto orderDto = orderRepository.findOrderById(id);
        String currentStatus = orderDto.getStatus().toUpperCase();

        validateCurrentStatusForUpdate(currentStatus);

        orderRepository.updateStatusById(id, newStatus);
        logUpdateOrderStatusByIdSuccessfully(id);
    }

    public GlobalPageDto<GlobalFullDetailsDto> findFilteredOrdersDetails(UUID orderId, Long clientId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                                         Integer minQuantity, Integer maxQuantity, String status, Integer page,
                                                                         Integer linesPerPage, String direction, String orderBy) {
        logFindFilteredOrderDetailsStart();

        validateFilterOrderIdDetails(orderId);
        validateFilterClientIdDetails(clientId);
        validateFilterClientId(clientId);
        validateFilteredDateTimeStartAndDateTimeEndDetails(dateTimeStart, dateTimeEnd);
        itemOrderService.validateFilteredMinQuantityAndMaxQuantity(minQuantity, maxQuantity);
        String validatedStatus = validateFilterOrderStatus(status);
        Integer fixedPage = fixPageFilter(page);
        Integer fixedLinesPerPage = fixLinesPerPageFilter(linesPerPage);
        String fixedDirection = fixDirectionFilter(direction);
        String fixedOrderBy = fixOrderByFilteredDetails(orderBy);

        return orderRepository.findFilteredOrdersDetails(orderId, clientId, dateTimeStart, dateTimeEnd,
                minQuantity, maxQuantity, validatedStatus, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    public GlobalPageDto<RelevantOrderDataDto> findFilteredRelevantOrderData(Long clientId, LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                                             String status, Integer page, Integer linesPerPage, String direction, String orderBy) {
        logFindFilteredRelevantOrderDataStart();

        validateFilterClientId(clientId);
        validateFilteredDateTimeStartAndDateTimeEndDetails(dateTimeStart, dateTimeEnd);
        String validatedStatus = validateFilterOrderStatus(status);

        Integer fixedPage = fixPageFilter(page);
        Integer fixedLinesPerPage = fixLinesPerPageFilter(linesPerPage);
        String fixedDirection = fixDirectionFilter(direction);
        String fixedOrderBy = fixOrderByFilteredDetails(orderBy);

        return orderRepository.findFilteredRelevantOrderData(clientId, dateTimeStart, dateTimeEnd,
                validatedStatus, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    public GlobalPageDto<ClientSalesReportDto> findFilteredClientSalesReport(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd,
                                                                             Integer minOrder, Integer maxOrder, String status, Integer page,
                                                                             Integer linesPerPage, String direction, String orderBy) {
        logFindOrderClientSalesReportStart();

        validateFilteredDateTimeStartAndDateTimeEndDetails(dateTimeStart, dateTimeEnd);
        validateMinOrderAndMaxOrderFilter(minOrder, maxOrder);
        String validatedStatus = validateFilterOrderStatus(status);

        Integer fixedPage = fixPageFilter(page);
        Integer fixedLinesPerPage = fixLinesPerPageFilter(linesPerPage);
        String fixedDirection = fixDirectionFilter(direction);
        String fixedOrderBy = fixOrderByFilteredSalesReport(orderBy);


        return orderRepository.findFilteredClientSalesReport(dateTimeStart, dateTimeEnd, minOrder, maxOrder,
                validatedStatus, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    private void validateMinOrderAndMaxOrderFilter(Integer minOrder, Integer maxOrder) {
        logMinOrderFilterValidation(minOrder);
        logMaxOrderFilterValidation(maxOrder);

        if (nonNull(minOrder) && minOrder <= 0) {
            throw new ClientErrorException("O filtro mínimo de pedidos deve ser maior que 0.");
        }
        if (nonNull(maxOrder) && maxOrder <=0) {
            throw new ClientErrorException("O filtro máximo de pedidos deve ser maior que 0.");
        }
        if (nonNull(maxOrder) && nonNull(minOrder) && minOrder > maxOrder) {
            throw new ClientErrorException("O filtro mínimo de pedidos não pode ser maior que o máximo de pedidos informado.");
        }
    }

    private void validateFilteredDateTimeStartAndDateTimeEndDetails(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        logFilterOrderDateTimeStartValidation(dateTimeStart);
        logFilterOrderDateTimeEndValidation(dateTimeEnd);

        if (nonNull(dateTimeStart) && dateTimeStart.isAfter(LocalDateTime.now())) {
            throw new ClientErrorException("O filtro data/hora de ínicio não pode estar no futuro.");
        }
        if (nonNull(dateTimeStart) && !isNull(dateTimeEnd)) {
            if (dateTimeEnd.isBefore(dateTimeStart)) {
                throw new ClientErrorException("O filtro data/hora de ínicio não pode ser após a data final.");
            }
        }
        if (nonNull(dateTimeEnd) && dateTimeEnd.isAfter(LocalDateTime.now())) {
            throw new ClientErrorException("O filtro data/hora final não pode estar no futuro.");
        }

    }

    private void validateCurrentStatusForUpdate(String currentStatus) {
        logOrderCurrentStatusValidation(currentStatus);
        if (!"PENDING".equalsIgnoreCase(currentStatus)){
            throw new ClientErrorException("Não é possivel atualizar o status do pedido pois ele está: " + currentStatus);
        }
    }

    private void validateNewStatusForUpdate(OrderStatus newStatus) {
        logOrderNewStatusValidation(newStatus);
        if (newStatus.toString().equals("PENDING")) {
            throw new ClientErrorException("O status do pedido já está PENDENTE.");
        }
    }

    private void validateClientId(Long clientId) {
        logOrderClientIdValidation(clientId);
        if (isNull(clientId)) {
            throw new ClientErrorException("O id do cliente é obrigatório.");
        }
        if (!clientRepository.existsClientById(clientId)) {
            throw new ClientErrorException("Não existe cliente cadastrado com o id informado: " + clientId);
        }
    }

    private OrderStatus validateOrderStatus(String status) {
        logOrderStatusValidation(status);
        try{
            if (isNotBlank(status)) {
                String sanitizedStatus = removeAllSpaces(status.toUpperCase());
                return OrderStatus.valueOf(sanitizedStatus);
            }
            else {
                throw new ClientErrorException("O status é obrigatório.");
            }
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException("O status informado é inválido: " + status);
        }
    }

    private void validateOrderId(UUID id) {
        logOrderIdValidation(id);
        if (isNull(id)) {
            throw new ClientErrorException("O id do pedido é obrigatório.");
        }
        if (!orderRepository.existsOrderById(id)){
            throw new ClientErrorException("O id do pedido não está cadastrado.");
        }
    }

    private void notExistsById(UUID id) {
        if (!orderRepository.existsOrderById(id)) {
            throw new ClientErrorException("O id do pedido não está cadastrado.");
        }
     }

     private void validateFilterOrderId(UUID id) {
        logFilterOrderIdValidation(id);
        if (nonNull(id)) {
            notExistsById(id);
        }
     }

     private void validateFilterClientId(Long clientId) {
        logFilterOrderClientIdValidation(clientId);
        if (nonNull(clientId) && !clientRepository.existsClientById(clientId)) {
            throw new ClientErrorException("O id do cliente informado não está cadastrado.");
        }
     }

     private void validateFilterOrderIdDetails(UUID orderId) {
        validateFilterOrderId(orderId);
        if (nonNull(orderId) && !itemOrderRepository.existsItemOrderByOrderId(orderId)) {
            throw new ClientErrorException("Nenhum item de pedido vinculado ao id do pedido informado.");
        }
     }

     private void validateFilterClientIdDetails(Long clientId) {
        validateFilterClientId(clientId);
        if (nonNull(clientId) && !itemOrderRepository.existsItemOrderByClientId(clientId)) {
            throw new ClientErrorException("Nenhum item de pedido vinculado ao id do cliente informado.");
        }
     }

    private void validateFilterOrderDateTimeStartAndDateTimeEnd(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        logFilterOrderDateTimeStartValidation(dateTimeStart);
        logFilterOrderDateTimeEndValidation(dateTimeEnd);
        if (nonNull(dateTimeStart) && isNull(dateTimeEnd)) {
            throw new ClientErrorException("Não é permitido preencher apenas data/hora ínicio.");
        }
        if (isNull(dateTimeStart) && nonNull(dateTimeEnd)) {
            throw new ClientErrorException("Não é permitido preencher apenas data/hora final.");
        }
        if (nonNull(dateTimeStart) && nonNull(dateTimeEnd)) {
            if (dateTimeStart.isAfter(LocalDateTime.now())) {
                throw new ClientErrorException("O filtro data/hora de ínicio não pode ser uma data futura.");
            }
            if (dateTimeStart.isAfter(dateTimeEnd)) {
                throw new ClientErrorException("O filtro data/hora de ínicio não pode ser posterior ao data/hora final.");
            }
            if (dateTimeEnd.isAfter(LocalDateTime.now())) {
                throw new ClientErrorException("O filtro data/hora final não pode ser uma data futura.");
            }
        }
    }

    private String validateFilterOrderStatus(String status) {
        logFilterOrderStatusValidation(status);
        try {
            if (isNotBlank(status)) {
                String sanitizedStatus = removeAllSpaces(status.toUpperCase());
                OrderStatus.valueOf(sanitizedStatus);
                return sanitizedStatus.toUpperCase();
            }
            return null;
        } catch (IllegalArgumentException e) {
            throw new ClientErrorException("O status informado é inválido: " + status);
        }
    }

    private Integer fixPageFilter(Integer page) {
        if (isNull(page) || page < 0) {
            return 0;
        }
        return page;
    }

    private Integer fixLinesPerPageFilter(Integer linesPerPage) {
        if (isNull(linesPerPage) || linesPerPage <= 0) {
            return 10;
        }
        return linesPerPage;
    }

    private String fixDirectionFilter(String direction) {
        if (!ALLOWED_DIRECTION.contains(direction)) {
            return "asc";
        }
        return direction;
    }

    private String fixOrderByFilteredDetails(String orderBy) {
        return ORDER_BY_COLUMN_MAP_FILTER.getOrDefault(orderBy, "o.order_date");
    }

    private String fixOrderByFilteredSalesReport(String orderBy) {
        return ORDER_BY_COLUMN_MAP_SALES_REPORT.getOrDefault(orderBy, "COUNT(o.id)");
    }

    private String fixOrderByFilter(String orderBy) {
        if (!ALLOWED_ORDER_BY.contains(orderBy)) {
            return "order_date";
        }
        return orderBy;
    }
}
