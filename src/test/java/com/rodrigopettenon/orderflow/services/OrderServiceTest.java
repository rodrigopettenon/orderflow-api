package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.*;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ClientModel;
import com.rodrigopettenon.orderflow.models.OrderStatus;
import com.rodrigopettenon.orderflow.repositories.ClientRepository;
import com.rodrigopettenon.orderflow.repositories.ItemOrderRepository;
import com.rodrigopettenon.orderflow.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ItemOrderRepository itemOrderRepository;

    @Mock
    private ItemOrderService itemOrderService;

    private OrderDto orderDto;
    private ClientModel clientModel;

    @BeforeEach
    void setUp() {
        orderDto = new OrderDto();
        orderDto.setClientId(1L);
        orderDto.setStatus("PENDING");

        clientModel = new ClientModel();
        clientModel.setId(1L);
    }

    // Método saveOrder
    @Test
    @DisplayName("Should successfully save a new order")
    void shouldSaveOrderWithValidData() {
        // Arrange
        when(clientRepository.existsClientById(orderDto.getClientId())).thenReturn(true);
        when(clientRepository.findClientModelById(orderDto.getClientId())).thenReturn(clientModel);

        OrderDto savedOrder = new OrderDto();
        savedOrder.setId(UUID.randomUUID());
        savedOrder.setClientId(orderDto.getClientId());
        savedOrder.setStatus(orderDto.getStatus());
        savedOrder.setOrderDate(LocalDateTime.now());

        when(orderRepository.saveOrder(any())).thenReturn(savedOrder);

        // Act
        OrderDto result = orderService.saveOrder(orderDto);

        // Assert
        assertNotNull(result);
        assertEquals(orderDto.getClientId(), result.getClientId());
        assertEquals(orderDto.getStatus(), result.getStatus());
        assertNotNull(result.getOrderDate());

        verify(clientRepository).existsClientById(orderDto.getClientId());
        verify(clientRepository).findClientModelById(orderDto.getClientId());
        verify(orderRepository).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when clientId is null")
    void shouldThrowExceptionWhenClientIdIsNull() {
        // Arrange
        OrderDto dto = new OrderDto();
        dto.setClientId(null);
        dto.setStatus("PENDING");

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.saveOrder(dto);
        });

        assertEquals("O id do cliente é obrigatório.", exception.getMessage());

        verify(clientRepository, never()).existsClientById(any());
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when client does not exist")
    void shouldThrowExceptionWhenClientDoesNotExist() {
        // Arrange
        when(clientRepository.existsClientById(orderDto.getClientId())).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.saveOrder(orderDto);
        });

        assertEquals("Não existe cliente cadastrado com o id informado: " + orderDto.getClientId(), exception.getMessage());

        verify(clientRepository).existsClientById(orderDto.getClientId());
        verify(clientRepository, never()).findClientModelById(any());
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when status is null")
    void shouldThrowExceptionWhenStatusIsNull() {
        // Arrange
        OrderDto dto = new OrderDto();
        dto.setClientId(1L);
        dto.setStatus(null);

        // Mock para cliente existir e o fluxo passar para validação do status
        when(clientRepository.existsClientById(dto.getClientId())).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.saveOrder(dto);
        });

        assertEquals("O status é obrigatório.", exception.getMessage());

        // Verifica que o método existsClientById foi chamado
        verify(clientRepository).existsClientById(dto.getClientId());

        // Verifica que o método saveOrder não foi chamado
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when status is blank")
    void shouldThrowExceptionWhenStatusIsBlank() {
        // Arrange
        OrderDto dto = new OrderDto();
        dto.setClientId(1L);
        dto.setStatus("   ");

        // Mock para cliente existir e o fluxo passar para validação do status
        when(clientRepository.existsClientById(dto.getClientId())).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.saveOrder(dto);
        });

        assertEquals("O status é obrigatório.", exception.getMessage());

        verify(clientRepository).existsClientById(dto.getClientId());

        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when status is invalid")
    void shouldThrowExceptionWhenStatusIsInvalid() {
        // Arrange
        OrderDto dto = new OrderDto();
        dto.setClientId(1L);
        dto.setStatus("INVALID_STATUS");

        // Mock para cliente existir e o fluxo passar para validação do status
        when(clientRepository.existsClientById(dto.getClientId())).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.saveOrder(dto);
        });

        assertEquals("O status informado é inválido: INVALID_STATUS", exception.getMessage());

        verify(clientRepository).existsClientById(dto.getClientId());
        verify(orderRepository, never()).saveOrder(any());
    }

    @Test
    @DisplayName("Should set current date when saving order")
    void shouldSetCurrentDateWhenSavingOrder() {
        // Arrange
        when(clientRepository.existsClientById(orderDto.getClientId())).thenReturn(true);
        when(clientRepository.findClientModelById(orderDto.getClientId())).thenReturn(clientModel);

        LocalDateTime testStartTime = LocalDateTime.now();

        OrderDto savedOrder = new OrderDto();
        savedOrder.setOrderDate(LocalDateTime.now());
        when(orderRepository.saveOrder(any())).thenReturn(savedOrder);

        // Act
        OrderDto result = orderService.saveOrder(orderDto);

        // Assert
        assertNotNull(result.getOrderDate());
        assertFalse(result.getOrderDate().isBefore(testStartTime));
        assertFalse(result.getOrderDate().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should accept status with different cases")
    void shouldAcceptStatusWithDifferentCases() {
        // Arrange
        orderDto.setStatus("peNdinG"); // Status com case misto

        when(clientRepository.existsClientById(orderDto.getClientId())).thenReturn(true);
        when(clientRepository.findClientModelById(orderDto.getClientId())).thenReturn(clientModel);

        OrderDto savedOrder = new OrderDto();
        savedOrder.setStatus("PENDING"); // Status normalizado
        when(orderRepository.saveOrder(any())).thenReturn(savedOrder);

        // Act
        OrderDto result = orderService.saveOrder(orderDto);

        // Assert
        assertEquals("PENDING", result.getStatus());
    }

    // Método findById
    @Test
    @DisplayName("Should return order when id is valid")
    void shouldReturnOrderWhenIdIsValid() {
        // Arrange
        UUID id = UUID.randomUUID();
        OrderDto expectedOrder = new OrderDto();
        expectedOrder.setId(id);

        when(orderRepository.existsOrderById(id)).thenReturn(true);
        when(orderRepository.findOrderById(id)).thenReturn(expectedOrder);

        // Act
        OrderDto result = orderService.findById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());

        verify(orderRepository).existsOrderById(id);
        verify(orderRepository).findOrderById(id);
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void shouldThrowExceptionWhenIdIsNull() {
        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.findById(null);
        });

        assertEquals("O id do pedido é obrigatório.", exception.getMessage());
        verify(orderRepository, never()).findOrderById(any());
    }

    @Test
    @DisplayName("Should throw exception when order id does not exist")
    void shouldThrowExceptionWhenOrderIdDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.existsOrderById(id)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.findById(id);
        });

        assertEquals("O id do pedido não está cadastrado.", exception.getMessage());

        verify(orderRepository).existsOrderById(id);
        verify(orderRepository, never()).findOrderById(any());
    }

    // Método updateOrderStatusById
    @Test
    @DisplayName("Should update order status successfully when id and status are valid")
    void shouldUpdateOrderStatusSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        String newStatus = "COMPLETED";

        OrderDto existingOrder = new OrderDto();
        existingOrder.setId(id);
        existingOrder.setStatus("PENDING"); // status atual

        when(orderRepository.existsOrderById(id)).thenReturn(true);
        when(orderRepository.findOrderById(id)).thenReturn(existingOrder);

        // Act
        assertDoesNotThrow(() -> orderService.updateOrderStatusById(id, newStatus));

        // Assert
        verify(orderRepository).updateStatusById(id, OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Should throw exception when id is null to update")
    void shouldThrowExceptionWhenIdIsNullToUpdate() {
        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(null, "COMPLETED");
        });

        assertEquals("O id do pedido é obrigatório.", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when order id does not exist to update")
    void shouldThrowExceptionWhenOrderIdDoesNotExistToUpdate() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.existsOrderById(id)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(id, "COMPLETED");
        });

        assertEquals("O id do pedido não está cadastrado.", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when new status is null to update")
    void shouldThrowExceptionWhenNewStatusIsNull() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.existsOrderById(id)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(id, null);
        });

        assertEquals("O status é obrigatório.", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when new status is invalid to update")
    void shouldThrowExceptionWhenNewStatusIsInvalid() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.existsOrderById(id)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(id, "INVALID_STATUS");
        });

        assertEquals("O status informado é inválido: INVALID_STATUS", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when new status is PENDING")
    void shouldThrowExceptionWhenNewStatusIsPending() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(orderRepository.existsOrderById(id)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(id, "PENDING");
        });

        assertEquals("O status do pedido já está PENDENTE.", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    @Test
    @DisplayName("Should throw exception when current status is not PENDING")
    void shouldThrowExceptionWhenCurrentStatusIsNotPending() {
        // Arrange
        UUID id = UUID.randomUUID();
        String newStatus = "COMPLETED";

        OrderDto existingOrder = new OrderDto();
        existingOrder.setId(id);
        existingOrder.setStatus("COMPLETED"); // status atual diferente

        when(orderRepository.existsOrderById(id)).thenReturn(true);
        when(orderRepository.findOrderById(id)).thenReturn(existingOrder);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderService.updateOrderStatusById(id, newStatus);
        });

        assertEquals("Não é possivel atualizar o status do pedido pois ele está: COMPLETED", exception.getMessage());
        verify(orderRepository, never()).updateStatusById(any(), any());
    }

    // Método findFilteredOrders
    @Test
    @DisplayName("Should return filtered orders successfully")
    void shouldReturnFilteredOrdersSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String status = "PENDING";
        int page = 1;
        int lines = 20;
        String direction = "desc";
        String orderBy = "client_id";

        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        GlobalPageDto<OrderDto> expected = new GlobalPageDto<>();
        expected.setTotal(1L);
        expected.setItems(List.of(new OrderDto()));

        when(orderRepository.findFilteredOrders(orderId, clientId, start, end, status, page, lines, direction, orderBy))
                .thenReturn(expected);

        // Act
        GlobalPageDto<OrderDto> result = orderService.findFilteredOrders(orderId, clientId, start, end, status, page, lines, direction, orderBy);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should apply default values when inputs are invalid")
    void shouldApplyDefaultValuesForNullOrInvalidInputs() {
        // Arrange
        UUID orderId = null;
        Long clientId = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        String status = null;
        Integer page = -5;
        Integer lines = 0;
        String direction = "invalid";
        String orderBy = "unknown_field";

        GlobalPageDto<OrderDto> expected = new GlobalPageDto<>();
        expected.setItems(List.of());
        expected.setTotal(0L);
        when(orderRepository.findFilteredOrders(null, null, null, null, null, 0, 10, "asc", "order_date"))
                .thenReturn(expected);

        // Act
        GlobalPageDto<OrderDto> result = orderService.findFilteredOrders(orderId, clientId, start, end, status, page, lines, direction, orderBy);

        // Assert
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when client ID does not exist")
    void shouldThrowExceptionWhenClientIdNotExists() {
        // Arrange
        Long clientId = 99L;
        when(clientRepository.existsClientById(clientId)).thenReturn(false);

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(null, clientId, null, null, null, null, null, null, null)
        );

        assertEquals("O id do cliente informado não está cadastrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when order ID does not exist")
    void shouldThrowExceptionWhenOrderIdNotExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsOrderById(orderId)).thenReturn(false);

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(orderId, null, null, null, null, null, null, null, null)
        );

        assertEquals("O id do pedido não está cadastrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        // Arrange
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.minusDays(1);

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(null, null, start, end, null, null, null, null, null)
        );

        assertEquals("O filtro data/hora de ínicio não pode ser posterior ao data/hora final.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future")
    void shouldThrowExceptionWhenStartDateIsInFuture() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(null, null, start, null, null, null, null, null, null)
        );

        assertEquals("O filtro data/hora de ínicio não pode ser uma data futura.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future to find filtered orders")
    void shouldThrowExceptionWhenEndDateIsInFutureToFindFilteredOrders() {
        // Arrange
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(null, null, null, end, null, null, null, null, null)
        );

        assertEquals("O filtro data/hora final não pode ser uma data futura.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is invalid")
    void shouldThrowExceptionWhenStatusIsInvalidToFindFilteredOrders() {
        // Arrange
        String status = "FOO";

        // Act + Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrders(null, null, null, null, status, null, null, null, null)
        );

        assertEquals("O status informado é inválido: FOO", ex.getMessage());
    }

    // Método findFilteredOrdersDetails
    @Test
    @DisplayName("Should return filtered order details successfully")
    void shouldReturnFilteredOrderDetailsSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();
        Integer minQuantity = 1;
        Integer maxQuantity = 5;
        String status = "PENDING";
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "asc";
        String orderBy = "order_date";

        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByOrderId(orderId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByClientId(clientId)).thenReturn(true);
        when(clientRepository.existsClientById(clientId)).thenReturn(true);
        doNothing().when(itemOrderService).validateFilteredMinQuantityAndMaxQuantity(minQuantity, maxQuantity);

        GlobalPageDto<GlobalFullDetailsDto> expectedPage = new GlobalPageDto<>();
        expectedPage.setTotal(1L);
        expectedPage.setItems(List.of(new GlobalFullDetailsDto()));

        when(orderRepository.findFilteredOrdersDetails(
                eq(orderId), eq(clientId), eq(start), eq(end), eq(minQuantity), eq(maxQuantity),
                eq("PENDING"), eq(page), eq(linesPerPage), eq(direction), anyString()))
                .thenReturn(expectedPage);

        // Act
        GlobalPageDto<GlobalFullDetailsDto> result = orderService.findFilteredOrdersDetails(
                orderId, clientId, start, end, minQuantity, maxQuantity, status, page, linesPerPage, direction, orderBy);

        // Assert
        assertEquals(expectedPage, result);

        // Verifica se os mocks foram chamados
        verify(itemOrderRepository).existsItemOrderByOrderId(orderId);
        verify(itemOrderRepository).existsItemOrderByClientId(clientId);
        verify(clientRepository, times(2)).existsClientById(clientId);
        verify(itemOrderService).validateFilteredMinQuantityAndMaxQuantity(minQuantity, maxQuantity);
        verify(orderRepository).findFilteredOrdersDetails(
                eq(orderId), eq(clientId), eq(start), eq(end), eq(minQuantity), eq(maxQuantity),
                eq("PENDING"), eq(page), eq(linesPerPage), eq(direction), anyString());
    }

    @Test
    @DisplayName("Should throw exception when orderId does not exist")
    void shouldThrowExceptionWhenOrderIdDoesNotExistToFindFilteredOrdersDetails() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsOrderById(orderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(orderId, null, null, null, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("O id do pedido não está cadastrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when no item order linked to orderId")
    void shouldThrowExceptionWhenNoItemOrderLinkedToOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByOrderId(orderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(orderId, null, null, null, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("Nenhum item de pedido vinculado ao id do pedido informado.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when clientId is not found")
    void shouldThrowExceptionWhenClientIdIsNotFound() {
        // Arrange
        Long clientId = 1L;

        when(clientRepository.existsClientById(clientId)).thenReturn(false);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, clientId, null, null, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("O id do cliente informado não está cadastrado.", ex.getMessage());

        verify(clientRepository).existsClientById(clientId);
    }

    @Test
    @DisplayName("Should throw exception when no item order linked to clientId")
    void shouldThrowExceptionWhenNoItemOrderLinkedToClientId() {
        // Arrange
        Long clientId = 1L;
        when(clientRepository.existsClientById(clientId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByClientId(clientId)).thenReturn(false);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, clientId, null, null, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("Nenhum item de pedido vinculado ao id do cliente informado.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future")
    void shouldThrowExceptionWhenStartDateIsInTheFuture() {
        // Arrange
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, null, futureDate, null, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora de ínicio não pode estar no futuro.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date to find filtered orders details")
    void shouldThrowExceptionWhenStartDateIsAfterEndDateToFindFilteredOrdersDetails() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = start.minusDays(2);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, null, start, end, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora de ínicio não pode ser após a data final.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future")
    void shouldThrowExceptionWhenEndDateIsInTheFuture() {
        // Arrange
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, null, null, end, null, null, null, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora final não pode estar no futuro.", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is invalid to find filtered orders details")
    void shouldThrowExceptionWhenStatusIsInvalidToFindFilteredOrdersDetails() {
        // Act & Assert
        ClientErrorException ex = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredOrdersDetails(null, null, null, null, null, null, "INVALID", 0, 10, "asc", "order_date"));

        assertEquals("O status informado é inválido: INVALID", ex.getMessage());
    }

    // método findFilteredRelevantOrderData
    @Test
    @DisplayName("Should return relevant order data successfully")
    void shouldReturnRelevantOrderDataSuccessfully() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now();
        String status = "COMPLETED"; // Corrigido: status válido
        int page = 0;
        int lines = 10;
        String direction = "asc";
        String orderBy = "order_date";

        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        GlobalPageDto<RelevantOrderDataDto> expected = new GlobalPageDto<>();
        expected.setTotal(1L);
        expected.setItems(List.of(new RelevantOrderDataDto()));

        when(orderRepository.findFilteredRelevantOrderData(
                eq(clientId), eq(start), eq(end), eq(status), eq(page), eq(lines), eq(direction), anyString()))
                .thenReturn(expected);

        // Act
        GlobalPageDto<RelevantOrderDataDto> result = orderService.findFilteredRelevantOrderData(
                clientId, start, end, status, page, lines, direction, orderBy);

        // Assert
        assertEquals(expected, result);
        verify(clientRepository).existsClientById(clientId);
        verify(orderRepository).findFilteredRelevantOrderData(
                eq(clientId), eq(start), eq(end), eq(status), eq(page), eq(lines), eq(direction), anyString());
    }

    @Test
    @DisplayName("Should throw exception when clientId does not exist")
    void shouldThrowExceptionWhenClientIdDoesNotExist() {
        // Arrange
        Long clientId = 999L;
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String status = "PENDING";

        when(clientRepository.existsClientById(clientId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredRelevantOrderData(clientId, start, end, status, 0, 10, "asc", "order_date"));

        assertEquals("O id do cliente informado não está cadastrado.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future to find filtered relevant order data")
    void shouldThrowExceptionWhenStartDateIsInFutureToFindFilteredRelevantOrderData() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        String status = "COMPLETED";

        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredRelevantOrderData(clientId, start, end, status, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora de ínicio não pode estar no futuro.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future")
    void shouldThrowExceptionWhenEndDateIsInFuture() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        String status = "CANCELLED";

        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredRelevantOrderData(clientId, start, end, status, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora final não pode estar no futuro.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when end date is before start date")
    void shouldThrowExceptionWhenEndDateBeforeStartDate() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(5);
        String status = "PENDING";

        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredRelevantOrderData(clientId, start, end, status, 0, 10, "asc", "order_date"));

        assertEquals("O filtro data/hora de ínicio não pode ser após a data final.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is invalid to find filtered relevant order data")
    void shouldThrowExceptionWhenStatusIsInvalidToFindFilteredRelevantOrderData() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        String invalidStatus = "SHIPPED"; // inválido

        when(clientRepository.existsClientById(clientId)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredRelevantOrderData(clientId, start, end, invalidStatus, 0, 10, "asc", "order_date"));

        assertEquals("O status informado é inválido: SHIPPED", exception.getMessage());
    }

    // Método findFilteredClientSalesReport
    @Test
    @DisplayName("Should return client sales report successfully")
    void shouldReturnClientSalesReportSuccessfully() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(30);
        LocalDateTime end = LocalDateTime.now();
        Integer minOrder = 1;
        Integer maxOrder = 10;
        String status = "PENDING";  // status válido conforme enum
        int page = 0;
        int lines = 10;
        String direction = "desc";
        String orderBy = "client_name";

        GlobalPageDto<ClientSalesReportDto> expected = new GlobalPageDto<>();
        expected.setTotal(2L);
        expected.setItems(List.of(new ClientSalesReportDto(), new ClientSalesReportDto()));

        // Como não tem validação de cliente aqui, não mockamos clientRepository
        when(orderRepository.findFilteredClientSalesReport(
                eq(start), eq(end), eq(minOrder), eq(maxOrder), eq(status.toUpperCase()),
                eq(page), eq(lines), eq(direction), anyString()))
                .thenReturn(expected);

        // Act
        GlobalPageDto<ClientSalesReportDto> result = orderService.findFilteredClientSalesReport(
                start, end, minOrder, maxOrder, status, page, lines, direction, orderBy);

        // Assert
        assertEquals(expected, result);
        verify(orderRepository).findFilteredClientSalesReport(
                eq(start), eq(end), eq(minOrder), eq(maxOrder), eq(status.toUpperCase()),
                eq(page), eq(lines), eq(direction), anyString());
    }

    @Test
    @DisplayName("Should return client sales report with all null filters applying defaults")
    void shouldReturnClientSalesReportWithNullFilters() {
        // Arrange
        LocalDateTime start = null;
        LocalDateTime end = null;
        Integer minOrder = null;
        Integer maxOrder = null;
        String status = null;
        Integer page = null;
        Integer linesPerPage = null;
        String direction = null;
        String orderBy = null;

        GlobalPageDto<ClientSalesReportDto> expected = new GlobalPageDto<>();
        expected.setTotal(0L);
        expected.setItems(new ArrayList<>());

        when(orderRepository.findFilteredClientSalesReport(
                eq(start), eq(end), eq(minOrder), eq(maxOrder), eq(null),
                eq(0), eq(10), eq("asc"), eq("COUNT(o.id)")))
                .thenReturn(expected);

        // Act
        GlobalPageDto<ClientSalesReportDto> result = orderService.findFilteredClientSalesReport(
                start, end, minOrder, maxOrder, status, page, linesPerPage, direction, orderBy);

        // Assert
        assertEquals(expected, result);
        verify(orderRepository).findFilteredClientSalesReport(
                eq(start), eq(end), eq(minOrder), eq(maxOrder), eq(null),
                eq(0), eq(10), eq("asc"), eq("COUNT(o.id)"));
    }

    @Test
    @DisplayName("Should throw exception when start date is in the future")
    void shouldThrowExceptionWhenStartDateInFuture() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        Integer minOrder = 1;
        Integer maxOrder = 10;
        String status = "PENDING";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, null, minOrder, maxOrder, status, 0, 10, "asc", "client_name")
        );

        assertEquals("O filtro data/hora de ínicio não pode estar no futuro.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when end date is in the future")
    void shouldThrowExceptionWhenEndDateInFuture() {
        // Arrange
        LocalDateTime endFuture = LocalDateTime.of(3000, 10, 12,4, 10); // data futura
        Integer minOrder = 1;
        Integer maxOrder = 10;
        String status = "COMPLETED";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(null, endFuture, minOrder, maxOrder, status, 0, 10, "asc", "client_id")
        );

        assertEquals("O filtro data/hora final não pode estar no futuro.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when start date is after end date")
    void shouldThrowExceptionWhenStartAfterEnd() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().minusDays(5);
        Integer minOrder = 1;
        Integer maxOrder = 10;
        String status = "CANCELLED";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, end, minOrder, maxOrder, status, 0, 10, "asc", "total_orders")
        );

        assertEquals("O filtro data/hora de ínicio não pode ser após a data final.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when minOrder is less than or equal to 0")
    void shouldThrowExceptionWhenMinOrderIsInvalid() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        Integer minOrder = 0;
        Integer maxOrder = 5;
        String status = "PENDING";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, end, minOrder, maxOrder, status, 0, 10, "asc", "total_amount")
        );

        assertEquals("O filtro mínimo de pedidos deve ser maior que 0.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when maxOrder is less than or equal to 0")
    void shouldThrowExceptionWhenMaxOrderIsInvalid() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        Integer minOrder = 1;
        Integer maxOrder = 0;
        String status = "COMPLETED";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, end, minOrder, maxOrder, status, 0, 10, "asc", "client_id")
        );

        assertEquals("O filtro máximo de pedidos deve ser maior que 0.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when minOrder is greater than maxOrder")
    void shouldThrowExceptionWhenMinOrderGreaterThanMaxOrder() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        Integer minOrder = 10;
        Integer maxOrder = 5;
        String status = "COMPLETED";

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, end, minOrder, maxOrder, status, 0, 10, "asc", "client_name")
        );

        assertEquals("O filtro mínimo de pedidos não pode ser maior que o máximo de pedidos informado.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when status is invalid to find filtered client sales report")
    void shouldThrowExceptionWhenStatusIsInvalidToFindFilteredClientSalesReport() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        Integer minOrder = 1;
        Integer maxOrder = 10;
        String status = "SHIPPED"; // inválido

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                orderService.findFilteredClientSalesReport(start, end, minOrder, maxOrder, status, 0, 10, "asc", "total_orders")
        );

        assertEquals("O status informado é inválido: SHIPPED", exception.getMessage());
    }
}

