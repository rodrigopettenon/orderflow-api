package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.OrderDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
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


}