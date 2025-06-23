package com.rodrigopettenon.cadastro_e_consulta.services;

import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.OrderPageDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.models.ClientModel;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderModel;
import com.rodrigopettenon.cadastro_e_consulta.models.OrderStatus;
import com.rodrigopettenon.cadastro_e_consulta.repositories.ClientRepository;
import com.rodrigopettenon.cadastro_e_consulta.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.rodrigopettenon.cadastro_e_consulta.utils.LogUtil.*;
import static com.rodrigopettenon.cadastro_e_consulta.utils.StringsValidation.removeAllSpaces;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class OrderService{

    private static final List<String> ALLOWED_ORDER_BY = Arrays.asList("id", "client_id", "order_date", "status");
    private static final List<String> ALLOWED_DIRECTION = Arrays.asList("asc", "desc");

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

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

    public OrderPageDto findFilteredOrders(UUID id, Long clientId, LocalDateTime dateTimeStart,
                                           LocalDateTime dateTimeEnd, String status, Integer page,
                                           Integer linesPerPage, String direction, String orderBy) {
        logFindFilteredOrdersStart();

        Integer fixedPage = fixPageFilter(page);
        Integer fixedLinesPerPage = fixLinesPerPage(linesPerPage);
        String fixedDirection = fixDirectionFilter(direction);
        String fixedOrderBy = fixOrderByFilter(orderBy);
        validateFilterOrderId(id);
        validateFilterClientId(clientId);
        validateFilterOrderDateTimeStart(dateTimeStart, dateTimeEnd);
        validateFilterOrderDateTimeEnd(dateTimeEnd, dateTimeStart);
        validateFilterOrderStatus(status);

        return orderRepository.findFilteredOrders(id, clientId, dateTimeStart, dateTimeEnd, status, fixedPage, fixedLinesPerPage, fixedDirection, fixedOrderBy);
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
            if (!isBlank(status)) {
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
        if (!isNull(id)) {
            notExistsById(id);
        }
     }

     private void validateFilterClientId(Long clientId) {
        logFilterOrderClientIdValidation(clientId);
        if(!isNull(clientId) && !clientRepository.existsClientById(clientId)) {
            throw new ClientErrorException("O id do cliente informado não está cadastrado.");
        }
     }

    private void validateFilterOrderDateTimeStart(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd) {
        logFilterOrderDateTimeStartValidation(dateTimeStart);
        if (!isNull(dateTimeStart) && isNull(dateTimeEnd)) {
            throw new ClientErrorException("Não é permitido preencher apenas data/hora ínicio.");
        }
        if (!isNull(dateTimeStart) && !isNull(dateTimeEnd)) {
            if (dateTimeStart.isAfter(LocalDateTime.now())) {
                throw new ClientErrorException("O filtro data/hora de ínicio não pode ser uma data futura.");
            }
            if (dateTimeStart.isAfter(dateTimeEnd)) {
                throw new ClientErrorException("O filtro data/hora de ínicio não pode ser posterior ao data/hora final.");
            }
        }
    }

    private void validateFilterOrderDateTimeEnd(LocalDateTime dateTimeEnd, LocalDateTime dateTimeStart) {
        logFilterOrderDateTimeEndValidation(dateTimeEnd);
        if (isNull(dateTimeStart) && !isNull(dateTimeEnd)) {
            throw new ClientErrorException("Não é permitido preencher apenas data/hora final.");
        }
        if (!isNull(dateTimeEnd) && !isNull(dateTimeStart)) {
            if (dateTimeEnd.isAfter(LocalDateTime.now())) {
                throw new ClientErrorException("O filtro data/hora final não pode ser uma data futura.");
            }
            if (dateTimeEnd.isBefore(dateTimeStart)) {
                throw new ClientErrorException("O filtro data/hora final não pode ser anterior ao data/hora inicio.");
            }
        }
    }

    private void validateFilterOrderStatus(String status) {
        logFilterOrderStatusValidation(status);
        try {
            if (!isBlank(status)) {
                String sanitizedStatus = removeAllSpaces(status.toUpperCase());
                OrderStatus.valueOf(sanitizedStatus);
            }
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

    private Integer fixLinesPerPage(Integer linesPerPage) {
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

    private String fixOrderByFilter(String orderBy) {
        if (!ALLOWED_ORDER_BY.contains(orderBy)) {
            return "order_date";
        }
        return orderBy;
    }
}
