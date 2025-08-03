package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.*;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ClientModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.OrderStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryTest.class);

    @InjectMocks
    private OrderRepository orderRepository;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    //Método saveOrder
    @Test
    @DisplayName("Should save a new order successfully and return the OrderDto")
    void shouldSaveOrderSuccessfully() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime orderDate = LocalDateTime.of(2024, 5, 1, 14, 0);
        OrderStatus status = OrderStatus.COMPLETED;

        ClientModel client = new ClientModel();
        client.setId(clientId);

        OrderModel orderModel = new OrderModel();
        orderModel.setClient(client);
        orderModel.setOrderDate(orderDate);
        orderModel.setStatus(status);

        // Mock para criação da query e execução
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("client_id"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("order_date"), eq(orderDate))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status.toString()))).thenReturn(query);

        when(query.executeUpdate()).thenReturn(1); // Quantos clientes foram salvos

        // Act
        OrderDto result = orderRepository.saveOrder(orderModel);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(clientId, result.getClientId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(status.toString(), result.getStatus());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("client_id", clientId);
        verify(query).setParameter("order_date", orderDate);
        verify(query).setParameter("status", status.toString());
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs while saving order")
    void shouldThrowClientErrorExceptionWhenErrorOccursOnSave() {
        // Arrange
        Long clientId = 1L;
        LocalDateTime orderDate = LocalDateTime.of(2024, 5, 1, 14, 0);
        OrderStatus status = OrderStatus.PENDING;

        ClientModel client = new ClientModel();
        client.setId(clientId);

        OrderModel orderModel = new OrderModel();
        orderModel.setClient(client);
        orderModel.setOrderDate(orderDate);
        orderModel.setStatus(status);

        // Mock da criação da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("client_id"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("order_date"), eq(orderDate))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status.toString()))).thenReturn(query);

        // Simula erro ao tentar executar a query
        when(query.executeUpdate()).thenThrow(new RuntimeException("Erro no banco"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.saveOrder(orderModel);
        });

        assertEquals("Erro ao cadastrar pedido", exception.getMessage());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("client_id", clientId);
        verify(query).setParameter("order_date", orderDate);
        verify(query).setParameter("status", status.toString());
        verify(query).executeUpdate();
    }

    //Método updateStatusById()
    @Test
    @DisplayName("Should update order status successfully by id")
    void shouldUpdateOrderStatusSuccessfullyById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderStatus newStatus = OrderStatus.CANCELLED;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("newStatus"), eq(newStatus))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        orderRepository.updateStatusById(orderId, newStatus);

        // Assert
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("newStatus", newStatus);
        verify(query).setParameter("id", orderId.toString());
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs while updating order status by id")
    void shouldThrowClientErrorExceptionWhenErrorOccursOnUpdateStatusById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        OrderStatus newStatus = OrderStatus.PENDING;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("newStatus"), eq(newStatus))).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.executeUpdate()).thenThrow(new RuntimeException("Erro no banco"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.updateStatusById(orderId, newStatus);
        });

        assertEquals("Erro ao atualizar o status do pedido pelo id.", exception.getMessage());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("newStatus", newStatus);
        verify(query).setParameter("id", orderId.toString());
        verify(query).executeUpdate();
    }

    // Método findOrderById
    @Test
    @DisplayName("Should find order by id and return OrderDto successfully")
    void shouldFindOrderByIdSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime orderDate = LocalDateTime.of(2024, 5, 1, 14, 0);
        String status = OrderStatus.COMPLETED.toString();

        Object[] resultRow = {
                orderId.toString(),
                clientId,
                Timestamp.valueOf(orderDate),
                status
        };

        List<Object[]> resultList = new ArrayList<>();
        resultList.add(resultRow);

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);

        // Act
        OrderDto result = orderRepository.findOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(clientId, result.getClientId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(status, result.getStatus());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when order is not found by id")
    void shouldThrowClientErrorExceptionWhenOrderNotFoundById() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of()); // lista vazia

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findOrderById(orderId);
        });

        assertEquals("Pedido não encontrado com o id informado.", exception.getMessage());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs while finding order by id")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnFindById() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Erro inesperado"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findOrderById(orderId);
        });

        assertEquals("Erro ao buscar pedido pelo id", exception.getMessage());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    // Método existsOrderById
    @Test
    @DisplayName("Should return true when order exists by ID")
    void shouldReturnTrueWhenOrderExistsById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", orderId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1)); // Simula registro existente

        // Act
        Boolean result = orderRepository.existsOrderById(orderId);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database fails")
    void shouldThrowWhenDatabaseFails() {
        UUID orderId = UUID.randomUUID();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(ClientErrorException.class, () ->
                orderRepository.existsOrderById(orderId)
        );
    }

    @Test
    @DisplayName("Should return false when order does not exist by ID")
    void shouldReturnFalseWhenOrderDoesNotExistById() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", orderId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of()); // Lista vazia

        // Act
        Boolean result = orderRepository.existsOrderById(orderId);

        // Assert
        assertFalse(result);
    }

    // Método findOrderModelById
    @Test
    @DisplayName("Should return OrderModel with ClientModel when order exists")
    void shouldReturnOrderModelWithClientWhenOrderExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        LocalDateTime orderDate = LocalDateTime.now();
        OrderStatus status = OrderStatus.COMPLETED;
        Long clientId = 1L;

        // Mock da query principal (findOrderModelById)
        Object[] orderResult = new Object[]{
                orderId.toString(),  // id (String)
                clientId,           // client_id (Long)
                Timestamp.valueOf(orderDate), // order_date (Timestamp)
                status.toString()   // status (String)
        };

        List<Object[]> expectedListResult = new ArrayList<>();
        expectedListResult.add(orderResult);

        when(em.createNativeQuery(contains("FROM tb_orders")))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenReturn(expectedListResult); // Retorna List<Object[]>

        // Mock do ClientModel (simulando o método interno)
        ClientModel mockClient = new ClientModel();
        mockClient.setId(clientId);
        mockClient.setName("João Silva");
        mockClient.setEmail("joão@gmail.com");
        mockClient.setCpf("43070376002");
        mockClient.setBirth(LocalDate.of(1990, 4 ,23));

        // Spy para mockar apenas o método interno
        OrderRepository spyOrderRepository = Mockito.spy(orderRepository);

        doReturn(mockClient)
                .when(spyOrderRepository)
                .findClientModelByOrderId(eq(orderId));

        // Act
        OrderModel result = spyOrderRepository.findOrderModelById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(orderDate, result.getOrderDate());
        assertEquals(status, result.getStatus());
        assertEquals("João Silva", result.getClient().getName());
    }

    @Test
    @DisplayName("Should throw ClientErrorException when order is not found by id to find order model by id")
    void shouldThrowClientErrorExceptionWhenOrderNotFoundByIdToFindOrderModelById() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Mock da query principal (findOrderModelById)
        when(em.createNativeQuery(contains("FROM tb_orders")))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenReturn(new ArrayList<>()); // Retorna uma lista vazia

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findOrderModelById(orderId);
        });

        // Verifica se a mensagem da exceção é a esperada
        assertEquals("Pedido não encontrado com o id informado.", exception.getMessage());

        // Verificações extras para garantir que os métodos foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs while finding order by id")
    void shouldThrowClientErrorExceptionWhenErrorOccursWhileFindingOrderById() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Mock da query principal (findOrderModelById)
        when(em.createNativeQuery(contains("FROM tb_orders")))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenThrow(new RuntimeException("Erro inesperado no banco")); // Simula erro inesperado

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findOrderModelById(orderId);
        });

        // Verifica se a mensagem da exceção é a esperada
        assertEquals("Erro ao buscar o pedido pelo id.", exception.getMessage());

        // Verificações extras para garantir que os métodos foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    // Método findClientModelByOrderId
    @Test
    @DisplayName("Should return ClientModel when client exists for given order id")
    void shouldReturnClientModelWhenClientExistsForOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        String clientName = "João Silva";
        String clientEmail = "joao.silva@example.com";
        String clientCpf = "43070376002";
        LocalDate clientBirthDate = LocalDate.of(1990, 4, 23);

        // Mock do resultado da consulta SQL para o cliente
        Object[] clientResult = new Object[]{
                clientId,            // c.id (Long)
                clientName,          // c.name (String)
                clientEmail,         // c.email (String)
                clientCpf,           // c.cpf (String)
                java.sql.Date.valueOf(clientBirthDate) // c.birth_date (Date)
        };

        List<Object[]> expectedResultList = new ArrayList<>();
        expectedResultList.add(clientResult); // Adiciona o resultado mockado à lista

        // Mock da query principal (findClientModelByOrderId)
        when(em.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenReturn(expectedResultList); // Retorna a lista com o cliente mockado

        // Act
        ClientModel result = orderRepository.findClientModelByOrderId(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals(clientName, result.getName());
        assertEquals(clientEmail, result.getEmail());
        assertEquals(clientCpf, result.getCpf());
        assertEquals(clientBirthDate, result.getBirth());

        // Verificações extras para garantir que os métodos foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when no client is found for given order id")
    void shouldThrowClientErrorExceptionWhenNoClientIsFoundForOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Mock da query principal (findClientModelByOrderId)
        when(em.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenReturn(new ArrayList<>()); // Retorna uma lista vazia, simulando que não encontrou o cliente

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findClientModelByOrderId(orderId);
        });

        // Verifica a mensagem de erro esperada
        assertEquals("Não foi encontrado nenhum cliente vinculado ao pedido com o id informado.", exception.getMessage());

        // Verificações adicionais para garantir que a query foi chamada corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs while finding client by order id")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccurs() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Mock da query principal (findClientModelByOrderId)
        when(em.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("id", orderId.toString()))
                .thenReturn(query);
        when(query.getResultList())
                .thenThrow(new RuntimeException("Simulated database failure")); // Simula uma falha inesperada

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findClientModelByOrderId(orderId);
        });

        // Verifica a mensagem de erro esperada
        assertEquals("Erro ao buscar cliente pelo id do pedido.", exception.getMessage());

        // Verificações adicionais para garantir que a query foi chamada corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).getResultList();
    }

    // Método existsOrderByClientId()
    @Test
    @DisplayName("Should return true when order exists by client ID")
    void shouldReturnTrueWhenOrderExistsByClientId() {
        // Arrange
        Long clientId = 1L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1)); // Simula registro existente

        // Act
        Boolean result = orderRepository.existsOrderByClientId(clientId);

        // Assert
        assertTrue(result);

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("clientId", clientId);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when no order exists for client ID")
    void shouldReturnFalseWhenNoOrderExistsForClientId() {
        // Arrange
        Long clientId = 1L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of()); // Lista vazia

        // Act
        Boolean result = orderRepository.existsOrderByClientId(clientId);

        // Assert
        assertFalse(result);

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("clientId", clientId);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database fails while checking order existence by client ID")
    void shouldThrowClientErrorExceptionWhenDatabaseFailsOnExistsByClientId() {
        // Arrange
        Long clientId = 1L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId)).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.existsOrderByClientId(clientId);
        });

        assertEquals("Erro ao verificar existencia do pedido pelo id do cliente.", exception.getMessage());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("clientId", clientId);
        verify(query).getResultList();
    }

    // Método queryFindFilteredOrders
    @Test
    @DisplayName("Should return filtered orders details with all parameters successfully")
    void shouldReturnFilteredOrdersDetailsWithAllParameters() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime dateTimeStart = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime dateTimeEnd = LocalDateTime.of(2023, 12, 31, 23, 59);
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        String status = "COMPLETED";
        int page = 0;
        int linesPerPage = 10;
        String direction = "ASC";
        String orderBy = "o.order_date";

        // Mock do resultado da query (1 registro)
        Object[] mockRow = new Object[]{
                orderId.toString(),                  // o.id (String)
                Timestamp.valueOf(dateTimeStart),    // o.order_date (Timestamp)
                status,                              // o.status (String)
                clientId,                           // c.id (Long)
                "João Silva",                       // c.name (String)
                "joao@email.com",                   // c.email (String)
                5,                                  // i.quantity (Number)
                99.95                               // total_price (Number)
        };
        List<Object[]> mockQueryResult = new ArrayList<>();
        mockQueryResult.add(mockRow);

        // Configuração dos mocks
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("orderId"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("clientId"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(dateTimeStart))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(dateTimeEnd))).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), eq(minQuantity))).thenReturn(query);
        when(query.setParameter(eq("maxQuantity"), eq(maxQuantity))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status))).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockQueryResult);

        // Act
        List<GlobalFullDetailsDto> result = orderRepository.queryFindFilteredOrdersDetails(
                orderId, clientId, dateTimeStart, dateTimeEnd, minQuantity,
                maxQuantity, status, page, linesPerPage, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        GlobalFullDetailsDto dto = result.get(0);

        // Verifica Order
        assertEquals(orderId, dto.getOrder().getId());
        assertEquals(dateTimeStart, dto.getOrder().getOrderDate());
        assertEquals(status, dto.getOrder().getStatus());

        // Verifica Client
        assertEquals(clientId, dto.getClient().getId());
        assertEquals("João Silva", dto.getClient().getName());
        assertEquals("joao@email.com", dto.getClient().getEmail());

        // Verifica ItemOrder
        assertEquals(5, dto.getItemOrder().getQuantity());
        assertEquals(99.95, dto.getItemOrder().getTotalPrice());

        // Verifica parâmetros da query
        verify(query).setParameter("orderId", orderId.toString());
        verify(query).setParameter("clientId", clientId);
        verify(query).setParameter("dateTimeStart", dateTimeStart);
        verify(query).setParameter("limit", linesPerPage);
    }

    @Test
    @DisplayName("Should throw ClientErrorException when query fails to find filtered orders details")
    void shouldThrowClientErrorExceptionWhenQueryFails() {
        // Arrange
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.queryFindFilteredOrdersDetails(
                    null, null, null, null, null, null, null,
                    0, 10, "ASC", "o.order_date"
            );
        });

        assertEquals("Erro ao buscar detalhes dos pedidos filtrados.", exception.getMessage());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should ignore null parameters when building filtered orders details query")
    void shouldIgnoreNullParametersWhenBuildingQuery() {
        // Arrange
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(10))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(0))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<GlobalFullDetailsDto> result = orderRepository.queryFindFilteredOrdersDetails(
                null, null, null, null, null, null, null,
                0, 10, "ASC", "o.order_date"
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(query, never()).setParameter(eq("orderId"), any());
        verify(query, never()).setParameter(eq("clientId"), any());
        verify(query, never()).setParameter(eq("dateTimeStart"), any());
        verify(query).setParameter("limit", 10);
    }

    // Método queryCountFilteredOrdersDetails
    @Test
    @DisplayName("Should return count of filtered orders details with all parameters")
    void shouldReturnCountOfFilteredOrdersDetails() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime dateTimeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime dateTimeEnd = LocalDateTime.now();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        String status = "COMPLETED";

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("orderId"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("clientId"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(dateTimeStart))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(dateTimeEnd))).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), eq(minQuantity))).thenReturn(query);
        when(query.setParameter(eq("maxQuantity"), eq(maxQuantity))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L);

        // Act
        Long result = orderRepository.queryCountFilteredOrdersDetails(
                orderId, clientId, dateTimeStart, dateTimeEnd, minQuantity, maxQuantity, status
        );

        // Assert
        assertEquals(5L, result);

        verify(query).setParameter("orderId", orderId.toString());
        verify(query).setParameter("clientId", clientId);
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should return count ignoring null parameters")
    void shouldReturnCountIgnoringNullParameters() {
        // Arrange
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L);

        // Act
        Long result = orderRepository.queryCountFilteredOrdersDetails(
                null, null, null, null, null, null, null
        );

        // Assert
        assertEquals(3L, result);

        verify(query, never()).setParameter(eq("orderId"), any());
        verify(query, never()).setParameter(eq("clientId"), any());
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when count query fails")
    void shouldThrowClientErrorExceptionWhenCountQueryFails() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        String status = "COMPLETED";

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("orderId"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("clientId"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(startDate))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(endDate))).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), eq(minQuantity))).thenReturn(query);
        when(query.setParameter(eq("maxQuantity"), eq(maxQuantity))).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Simulated Exception"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.findFilteredOrdersDetails(
                    orderId, clientId, startDate, endDate,
                    minQuantity, maxQuantity, status,
                    0, 10, "ASC", "o.order_date");
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao contar pedidos com detalhes filtrados.", exception.getMessage());

        // Verificações
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("orderId"), eq(orderId.toString()));
        verify(query).setParameter(eq("clientId"), eq(clientId));
        verify(query).setParameter(eq("status"), eq(status));
        verify(query).setParameter(eq("dateTimeStart"), eq(startDate));
        verify(query).setParameter(eq("dateTimeEnd"), eq(endDate));
        verify(query).setParameter(eq("minQuantity"), eq(minQuantity));
        verify(query).setParameter(eq("maxQuantity"), eq(maxQuantity));
        verify(query).getSingleResult();
    }

    // Método findFilteredOrdersDetails
    @Test
    @DisplayName("Should return paginated order details with valid parameters")
    void shouldReturnPaginatedOrderDetailsWithValidParameters() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        String status = "COMPLETED";
        int page = 0;
        int size = 10;
        String direction = "ASC";
        String orderBy = "o.order_date";

        // Cria um spy do repositório para mockar os métodos internos
        OrderRepository spyRepo = Mockito.spy(orderRepository);

        // Mock da contagem total
        doReturn(2L)
                .when(spyRepo)
                .queryCountFilteredOrdersDetails(
                        eq(orderId), eq(clientId), eq(startDate), eq(endDate),
                        eq(minQuantity), eq(maxQuantity), eq(status)
                );

        // Mock dos resultados detalhados
        List<GlobalFullDetailsDto> mockDetails = new ArrayList<>();
        GlobalFullDetailsDto detail = new GlobalFullDetailsDto();

        // Preenche os dados do pedido
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setOrderDate(startDate);
        orderDto.setStatus(status);
        detail.setOrder(orderDto);

        // Preenche os dados do cliente
        ClientDto clientDto = new ClientDto();
        clientDto.setId(clientId);
        clientDto.setName("Cliente Teste");
        clientDto.setEmail("cliente@teste.com");
        detail.setClient(clientDto);

        // Preenche os dados do item
        ItemOrderDto itemDto = new ItemOrderDto();
        itemDto.setQuantity(2);
        itemDto.setTotalPrice(199.90);
        detail.setItemOrder(itemDto);

        mockDetails.add(detail);

        doReturn(mockDetails)
                .when(spyRepo)
                .queryFindFilteredOrdersDetails(
                        eq(orderId), eq(clientId), eq(startDate), eq(endDate),
                        eq(minQuantity), eq(maxQuantity), eq(status),
                        eq(page), eq(size), eq(direction), eq(orderBy)
                );

        // Act
        GlobalPageDto<GlobalFullDetailsDto> result = spyRepo.findFilteredOrdersDetails(
                orderId, clientId, startDate, endDate, minQuantity, maxQuantity,
                status, page, size, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // Verifica o total retornado pelo count
        assertEquals(1, result.getItems().size()); // Verifica a quantidade de itens

        // Verifica os dados do primeiro item
        GlobalFullDetailsDto firstItem = result.getItems().get(0);
        assertEquals(orderId, firstItem.getOrder().getId());
        assertEquals(clientId, firstItem.getClient().getId());
        assertEquals(199.90, firstItem.getItemOrder().getTotalPrice());

        // Verifica se os métodos internos foram chamados corretamente
        verify(spyRepo).queryCountFilteredOrdersDetails(
                eq(orderId), eq(clientId), eq(startDate), eq(endDate),
                eq(minQuantity), eq(maxQuantity), eq(status)
        );

        verify(spyRepo).queryFindFilteredOrdersDetails(
                eq(orderId), eq(clientId), eq(startDate), eq(endDate),
                eq(minQuantity), eq(maxQuantity), eq(status),
                eq(page), eq(size), eq(direction), eq(orderBy)
        );
    }


    // Método queryFindFilteredOrders
    @Test
    @DisplayName("Should return filtered orders successfully")
    void shouldReturnFilteredOrdersSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        String status = "COMPLETED";
        int page = 0;
        int size = 10;
        String direction = "ASC";
        String orderBy = "order_date";

        // Mock dos resultados
        Object[] resultRow = {
                orderId.toString(),       // id
                clientId,                // client_id
                Timestamp.valueOf(startDate), // order_date
                status                   // status
        };
        List<Object[]> queryResults = new ArrayList<>();
        queryResults.add(resultRow);

        // Configuração dos mocks
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("client_id"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(startDate))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(endDate))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status))).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(size))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * size))).thenReturn(query);
        when(query.getResultList()).thenReturn(queryResults);

        // Act
        List<OrderDto> result = orderRepository.queryFindFilteredOrders(
                orderId, clientId, startDate, endDate,
                status, page, size, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        OrderDto firstOrder = result.get(0);
        assertEquals(orderId, firstOrder.getId());
        assertEquals(clientId, firstOrder.getClientId());
        assertEquals(startDate, firstOrder.getOrderDate());
        assertEquals(status, firstOrder.getStatus());

        // Verificações
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("id", orderId.toString());
        verify(query).setParameter("client_id", clientId);
        verify(query).setParameter("dateTimeStart", startDate);
        verify(query).setParameter("limit", size);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when query execution fails to find filtered orders")
    void shouldThrowClientErrorExceptionWhenQueryFailsToFindFilteredOrders() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        String status = "COMPLETED";
        int page = 0;
        int size = 10;
        String direction = "ASC";
        String orderBy = "order_date";

        // Configuração dos mocks
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.queryFindFilteredOrders(
                    orderId, clientId, startDate, endDate,
                    status, page, size, direction, orderBy
            );
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao buscar pedidos filtrados.", exception.getMessage());

        // Verificações básicas
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("limit"), eq(size));
        verify(query).setParameter(eq("offset"), eq(page * size));
        verify(query).getResultList();
    }

    // Método queryCountFilteredOrders
    @Test
    @DisplayName("Should return count of filtered orders successfully")
    void shouldReturnCountOfFilteredOrdersSuccessfully() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        String status = "COMPLETED";

        // Mock do resultado
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("client_id"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(startDate))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(endDate))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L); // Simula 5 registros

        // Act
        Long result = orderRepository.queryCountFilteredOrders(
                orderId, clientId, startDate, endDate, status
        );

        // Assert
        assertEquals(5L, result);

        // Verificações
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_orders"));
        verify(query).setParameter("id", orderId.toString());
        verify(query).setParameter("client_id", clientId);
        verify(query).setParameter("dateTimeStart", startDate);
        verify(query).setParameter("status", status);
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when count query fails to count filtered orders")
    void shouldThrowClientErrorExceptionWhenCountQueryFailsToCountFilteredOrders() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now();
        String status = "COMPLETED";

        // Configuração do mock para simular erro
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("client_id"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("dateTimeStart"), eq(startDate))).thenReturn(query);
        when(query.setParameter(eq("dateTimeEnd"), eq(endDate))).thenReturn(query);
        when(query.setParameter(eq("status"), eq(status))).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Database timeout"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            orderRepository.queryCountFilteredOrders(
                    orderId, clientId, startDate, endDate, status
            );
        });

        // Verificações
        assertEquals("Erro ao contar total de pedidos filtrados.", exception.getMessage());
        verify(query).getSingleResult();
        verify(query, times(5)).setParameter(anyString(), any()); // Verifica os 5 parâmetros
        verify(em).createNativeQuery(contains("SELECT COUNT(*)"));
    }
}