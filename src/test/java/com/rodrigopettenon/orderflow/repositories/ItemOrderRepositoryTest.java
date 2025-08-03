package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.GlobalFullDetailsDto;
import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ItemOrderDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ItemOrderModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.ProductModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemOrderRepositoryTest {

    @InjectMocks
    private ItemOrderRepository itemOrderRepository;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    // Método saveItemOrder
    @Test
    @DisplayName("Should successfully save an item order")
    void shouldSaveItemOrderSuccessfully() {
        // Arrange
        ItemOrderModel itemOrder = new ItemOrderModel();
        itemOrder.setQuantity(2);
        itemOrder.setPrice(10.99);

        OrderModel order = new OrderModel();
        order.setId(UUID.randomUUID());
        itemOrder.setOrder(order);

        ProductModel product = new ProductModel();
        product.setId(UUID.randomUUID());
        itemOrder.setProduct(product);

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("order_id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("product_id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("quantity"), anyInt())).thenReturn(query);
        when(query.setParameter(eq("price"), any(Double.class))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        // Act
        ItemOrderDto result = itemOrderRepository.saveItemOrder(itemOrder);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(order.getId(), result.getOrderId());
        assertEquals(product.getId(), result.getProductId());
        assertEquals(2, result.getQuantity());
        assertEquals(10.99, result.getPrice());

        verify(em).createNativeQuery(contains("INSERT INTO tb_item_orders"));
        verify(query).setParameter("id", result.getId().toString());
        verify(query).setParameter("order_id", order.getId().toString());
        verify(query).setParameter("product_id", product.getId().toString());
        verify(query).setParameter("quantity", 2);
        verify(query).setParameter("price", 10.99);
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw exception when save fails")
    void shouldThrowExceptionWhenSaveFails() {
        // Arrange
        ItemOrderModel itemOrder = new ItemOrderModel();
        itemOrder.setOrder(new OrderModel());
        itemOrder.setProduct(new ProductModel());

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.saveItemOrder(itemOrder);
        });

        assertEquals("Erro ao salvar o item do pedido no banco de dados.", exception.getMessage());
        verify(query, never()).executeUpdate();
    }

    // Método existsItemOrderById()
    @Test
    @DisplayName("Should return true when item order exists")
    void shouldReturnTrueWhenItemOrderExists() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", id.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1));

        // Act
        boolean result = itemOrderRepository.existsItemOrderById(id);

        // Assert
        assertTrue(result);
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders"));
        verify(query).setParameter("id", id.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when no item exists for order")
    void shouldReturnFalseWhenNoItemExistsForOrder() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("orderId", orderId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Act
        boolean result = itemOrderRepository.existsItemOrderByOrderId(orderId);

        // Assert
        assertFalse(result);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when query execution fails")
    void shouldThrowWhenQueryExecutionFails() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", itemOrderId.toString())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Timeout"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.existsItemOrderById(itemOrderId);
        });

        assertEquals("Erro ao verificar existência do item do pedido pelo id.", exception.getMessage());

        // Verifica se a execução chegou até o getResultList()
        verify(query).getResultList();
    }


    // Método existsItemOrderByOrderId()
    @Test
    @DisplayName("Should return true when item order exists for given order ID")
    void shouldReturnTrueWhenItemOrderExistsForOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("orderId", orderId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1)); // Simula que encontrou registro

        // Act
        boolean result = itemOrderRepository.existsItemOrderByOrderId(orderId);

        // Assert
        assertTrue(result);

        // Verifications
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders WHERE order_id = :orderId LIMIT 1"));
        verify(query).setParameter("orderId", orderId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when no item exists for given order ID")
    void shouldReturnFalseWhenNoItemExistsForOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("orderId", orderId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>()); // Lista vazia = não encontrado

        // Act
        Boolean result = itemOrderRepository.existsItemOrderByOrderId(orderId);

        // Assert
        assertFalse(result);
        verify(query).getResultList(); // Ainda verifica que chegou até o final do fluxo
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database fails to check item order existence by order ID")
    void shouldThrowExceptionWhenDatabaseFailsToCheckItemOrderExistence() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("orderId", orderId.toString()))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.existsItemOrderByOrderId(orderId);
        });

        assertEquals("Erro ao verificar existência do item do pedido pelo id do pedido.", exception.getMessage());

        // Verifications
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders"));
        verify(query).setParameter("orderId", orderId.toString());
        verify(query, never()).getResultList();
    }

    // Método existsItemOrderByProductId()
    @Test
    @DisplayName("Should return true when item orders exist for the given product ID")
    void shouldReturnTrueWhenItemOrdersExistForProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("productId", productId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1)); // Simula que encontrou registros

        // Act
        boolean result = itemOrderRepository.existsItemOrderByProductId(productId);

        // Assert
        assertTrue(result);

        // Verifications
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders WHERE product_id = :productId LIMIT 1"));
        verify(query).setParameter("productId", productId.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when no item orders exist for the given product ID")
    void shouldReturnFalseWhenNoItemOrdersExistForProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("productId", productId.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>()); // Simula nenhum resultado

        // Act
        boolean result = itemOrderRepository.existsItemOrderByProductId(productId);

        // Assert
        assertFalse(result);
        verify(query).getResultList(); // Confirma que verificou no banco
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database fails to check item order existence by product ID")
    void shouldThrowExceptionWhenDatabaseFailsToCheckExistenceByProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("productId", productId.toString()))
                .thenThrow(new RuntimeException("Connection timeout"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.existsItemOrderByProductId(productId);
        });

        assertEquals("Erro ao verificar existência do item do pedido pelo id do produto.", exception.getMessage());

        // Verifications
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders WHERE product_id = :productId LIMIT 1"));
        verify(query).setParameter("productId", productId.toString());
        verify(query, never()).getResultList(); // Garante que o fluxo foi interrompido
    }

    // Método existsItemOrderByClientId
    @Test
    @DisplayName("Should return true when item orders exist for the given client ID")
    void shouldReturnTrueWhenItemOrdersExistForClientId() {
        // Arrange
        Long clientId = 1L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(1)); // Simula que encontrou registros

        // Act
        boolean result = itemOrderRepository.existsItemOrderByClientId(clientId);

        // Assert
        assertTrue(result);

        // Verifications
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_item_orders i JOIN tb_orders o JOIN tb_clients c"));
        verify(em).createNativeQuery(contains("ON i.order_id = o.id AND o.client_id = c.id"));
        verify(em).createNativeQuery(contains("WHERE c.id = :clientId LIMIT 1"));
        verify(query).setParameter("clientId", clientId);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when no item orders exist for the given client ID")
    void shouldReturnFalseWhenNoItemOrdersExistForClientId() {
        // Arrange
        Long clientId = 1L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Act
        boolean result = itemOrderRepository.existsItemOrderByClientId(clientId);

        // Assert
        assertFalse(result);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database fails to check item order existence by client ID")
    void shouldThrowExceptionWhenDatabaseFailsToCheckExistenceByClientId() {
        // Arrange
        Long clientId = 1L;
        String simulatedError = "Connection pool exhausted";

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("clientId", clientId))
                .thenThrow(new RuntimeException(simulatedError));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.existsItemOrderByClientId(clientId);
        });

        assertEquals("Erro ao verificar existência do item do pedido pelo id do cliente.", exception.getMessage());

        // Verifications
        verify(em).createNativeQuery(contains("JOIN tb_orders o JOIN tb_clients c"));
        verify(query).setParameter("clientId", clientId);
        verify(query, never()).getResultList(); // Garante que não chegou a executar a consulta completa
    }

    // Método queryFindFilteredItemOrders
    @Test
    @DisplayName("Should return filtered item orders list with valid parameters")
    void shouldReturnFilteredItemOrdersListWithValidParameters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        Integer page = 0;
        Integer linesPerPage = 5;
        String direction = "asc";
        String orderBy = "quantity";

        Number quantity = (Number) 5;
        Number price = (Number) 29.99;


        // Simula um resultado do banco de dados (uma linha retornada)
        Object[] expectedResultDto = new Object[]{
                id.toString(),          // id (String)
                orderId.toString(),     // order_id (String)
                productId.toString(),   // product_id (String)
                quantity,     // quantity (Number) esperado pelo retorno da query
                price        // price (Number) esperado pelo retorno da query
        };

        List<Object[]> expectedResultList = new ArrayList<>();
        expectedResultList.add(expectedResultDto);

        // Cria a NativeQuery e Configura os parâmetros esperados na query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(id.toString()))).thenReturn(query);
        when(query.setParameter(eq("order_id"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("product_id"), eq(productId.toString()))).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), eq(minQuantity))).thenReturn(query);
        when(query.setParameter(eq("maxQuantity"), eq(maxQuantity))).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(expectedResultList);

        List<ItemOrderDto> convertedExpectedResultList = new ArrayList<>();

        for (Object[] result : expectedResultList) {
            ItemOrderDto itemOrderDto = new ItemOrderDto();

            itemOrderDto.setId(UUID.fromString((String) result[0]));
            itemOrderDto.setOrderId(UUID.fromString((String) result[1]));
            itemOrderDto.setProductId(UUID.fromString((String) result[2]));
            itemOrderDto.setQuantity(((Number) result[3]).intValue());
            itemOrderDto.setPrice(((Number) result[4]).doubleValue());

            convertedExpectedResultList.add(itemOrderDto);
        }

        // Act
        List<ItemOrderDto> returnedResultList = itemOrderRepository.queryFindFilteredItemOrders(
                id, orderId, productId, minQuantity, maxQuantity,
                page, linesPerPage, direction, orderBy);

        // Assert
        assertNotNull(returnedResultList);
        assertEquals(convertedExpectedResultList.size(), returnedResultList.size()); // Apenas 1 item foi mockado

        ItemOrderDto returnedResult = returnedResultList.get(0);
        ItemOrderDto expectedResult = convertedExpectedResultList.get(0);
        assertEquals(expectedResult.getId(), returnedResult.getId());
        assertEquals(expectedResult.getOrderId(), returnedResult.getOrderId());
        assertEquals(expectedResult.getProductId(), returnedResult.getProductId());
        assertEquals(expectedResult.getQuantity(), returnedResult.getQuantity());
        assertEquals(expectedResult.getPrice(), returnedResult.getPrice());

        // Verifica se a query foi montada corretamente
        verify(em).createNativeQuery(contains("SELECT id, order_id, product_id, quantity, price FROM tb_item_orders"));
        verify(query).setParameter(eq("id"), eq(id.toString()));
        verify(query).setParameter(eq("order_id"), eq(orderId.toString()));
        verify(query).setParameter(eq("product_id"), eq(productId.toString()));
        verify(query).setParameter(eq("minQuantity"), eq(minQuantity));
        verify(query).setParameter(eq("maxQuantity"), eq(maxQuantity));
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).setParameter(eq("offset"), eq(page * linesPerPage));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should filter only by orderId and minQuantity when other params are null")
    void shouldFilterOnlyByOrderIdAndMinQuantity() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        int minQuantity = 2;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("order_id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), anyInt())).thenReturn(query);
        when(query.setParameter(eq("limit"), anyInt())).thenReturn(query);
        when(query.setParameter(eq("offset"), anyInt())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        // Act
        itemOrderRepository.queryFindFilteredItemOrders(
                null, orderId, null, minQuantity, null,
                0, 10, "asc", "quantity");

        // Assert
        verify(query, never()).setParameter(eq("id"), any()); // Não deve ser chamado
        verify(query, never()).setParameter(eq("product_id"), any());
        verify(query, never()).setParameter(eq("maxQuantity"), any());
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database query fails")
    void shouldThrowClientErrorExceptionWhenQueryFails() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        Integer page = 0;
        Integer linesPerPage = 5;
        String direction = "asc";
        String orderBy = "quantity";

        // Configura o mock para simular um erro na execução da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.queryFindFilteredItemOrders(
                    id, orderId, productId, minQuantity, maxQuantity,
                    page, linesPerPage, direction, orderBy);
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao buscar itens de produtos filtrados.", exception.getMessage());

        // Verifica se a query foi chamada (e falhou)
        verify(em).createNativeQuery(contains("SELECT id, order_id, product_id, quantity, price FROM tb_item_orders"));
        verify(query).setParameter(eq("id"), eq(id.toString()));
        verify(query).setParameter(eq("order_id"), eq(orderId.toString()));
        verify(query).setParameter(eq("product_id"), eq(productId.toString()));
        verify(query).setParameter(eq("minQuantity"), eq(minQuantity));
        verify(query).setParameter(eq("maxQuantity"), eq(maxQuantity));
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).setParameter(eq("offset"), eq(page * linesPerPage));
        verify(query).getResultList(); // O erro ocorre aqui
    }

    @Test
    @DisplayName("Should throw ClientErrorException when setting query parameters fails")
    void shouldThrowExceptionWhenSetParameterFails() {
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), any())).thenThrow(new RuntimeException("Invalid parameter"));

        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.queryFindFilteredItemOrders(
                    UUID.randomUUID(), null, null, null, null,
                    0, 10, "asc", "quantity");
        });

        assertEquals("Erro ao buscar itens de produtos filtrados.", exception.getMessage());
    }

    // Método queryCountFilteredItemOrders
    @Test
    @DisplayName("Should return count of filtered item orders with valid parameters")
    void shouldReturnCountOfFilteredItemOrdersWithValidParameters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;

        // Mock da Query e resultado esperado (COUNT = 5)
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Configura os parâmetros esperados na query
        when(query.setParameter(eq("id"), eq(id.toString()))).thenReturn(query);
        when(query.setParameter(eq("order_id"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("product_id"), eq(productId.toString()))).thenReturn(query);
        when(query.setParameter(eq("minQuantity"), eq(minQuantity))).thenReturn(query);
        when(query.setParameter(eq("maxQuantity"), eq(maxQuantity))).thenReturn(query);

        when(query.getSingleResult()).thenReturn(5L); // Simula 5 registros no banco

        // Act
        Long result = itemOrderRepository.queryCountFilteredItemOrders(
                id, orderId, productId, minQuantity, maxQuantity);

        // Assert
        assertEquals(5L, result); // Verifica se o COUNT retornado é 5

        // Verifica se a query foi montada corretamente
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_item_orders"));
        verify(query).setParameter(eq("id"),eq(id.toString()));
        verify(query).setParameter(eq("order_id"), eq(orderId.toString()));
        verify(query).setParameter(eq("product_id"), eq(productId.toString()));
        verify(query).setParameter(eq("minQuantity"), eq(minQuantity));
        verify(query).setParameter(eq("maxQuantity"), eq(maxQuantity));
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should return count when only orderId and minQuantity are provided")
    void shouldReturnCountWhenOnlyOrderIdAndMinQuantityAreProvided() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Integer minQuantity = 2;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L); // COUNT = 3

        // Act
        Long result = itemOrderRepository.queryCountFilteredItemOrders(
                null, orderId, null, minQuantity, null);

        // Assert
        assertEquals(3L, result);

        // Verifica se apenas os parâmetros fornecidos são usados
        verify(query).setParameter("order_id", orderId.toString());
        verify(query).setParameter("minQuantity", minQuantity);
        verify(query, never()).setParameter(eq("id"), any());
        verify(query, never()).setParameter(eq("product_id"), any());
        verify(query, never()).setParameter(eq("maxQuantity"), any());
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database query fails to count filtered item orders")
    void shouldThrowClientErrorExceptionWhenQueryFailsToCountFilteredItemOrders() {
        // Arrange
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.queryCountFilteredItemOrders(
                    UUID.randomUUID(), null, null, 1, 10);
        });

        assertEquals("Erro ao contar itens dos pedidos filtrados.", exception.getMessage());

        // Verifica se a query foi chamada antes do erro
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_item_orders"));
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should return total count when all filters are null")
    void shouldReturnTotalCountWhenAllFiltersAreNull() {
        // Arrange
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(10L); // COUNT total = 10

        // Act
        Long result = itemOrderRepository.queryCountFilteredItemOrders(
                null, null, null, null, null);

        // Assert
        assertEquals(10L, result);

        // Verifica se a query NÃO adicionou condições desnecessárias
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_item_orders WHERE 1=1"));
        verify(query, never()).setParameter(anyString(), any()); // Nenhum parâmetro foi setado
    }

    // Método findFilteredItemOrders
    @Test
    @DisplayName("Should return paginated filtered item orders with valid parameters")
    void shouldReturnPaginatedFilteredItemOrdersWithValidParameters() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        Integer page = 0;
        Integer linesPerPage = 5;
        String direction = "asc";
        String orderBy = "quantity";

        ItemOrderRepository spyItemOrderRepository = Mockito.spy(itemOrderRepository);

        // Mock da contagem total (queryCountFilteredItemOrders)
        doReturn(2L)
                .when(spyItemOrderRepository)
                .queryCountFilteredItemOrders(
                eq(id), eq(orderId), eq(productId),
                        eq(minQuantity), eq(maxQuantity)
        ); // Total de registros = 2

        // Mock da lista de itens (2 itens)
        List<ItemOrderDto> mockItems = new ArrayList<>();

        ItemOrderDto itemOrder1 = new ItemOrderDto();
        itemOrder1.setId(UUID.randomUUID());
        itemOrder1.setOrderId(orderId);
        itemOrder1.setProductId(productId);
        itemOrder1.setQuantity(2);
        itemOrder1.setPrice(15.99);

        ItemOrderDto itemOrder2 = new ItemOrderDto();
        itemOrder2.setId(UUID.randomUUID());
        itemOrder2.setOrderId(orderId);
        itemOrder2.setProductId(productId);
        itemOrder2.setQuantity(3);
        itemOrder2.setPrice(20.99);

        mockItems.add(itemOrder1);
        mockItems.add(itemOrder2);

        doReturn(mockItems)
                .when(spyItemOrderRepository)
                .queryFindFilteredItemOrders(
                        eq(id), eq(orderId), eq(productId),
                        eq(minQuantity), eq(maxQuantity),
                        eq(page), eq(linesPerPage),
                        eq(direction), eq(orderBy)
        );

        // Act
        GlobalPageDto<ItemOrderDto> result = spyItemOrderRepository.findFilteredItemOrders(
                id, orderId, productId, minQuantity, maxQuantity,
                page, linesPerPage, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // Total de registros
        assertEquals(2, result.getItems().size()); // Itens retornados

        // Verifica os dados dos itens
        ItemOrderDto firstItem = result.getItems().get(0);
        assertEquals(orderId, firstItem.getOrderId());
        assertEquals(2, firstItem.getQuantity());
        assertEquals(15.99, firstItem.getPrice());

        // Verifica se as queries foram chamadas com os parâmetros corretos
        verify(spyItemOrderRepository).queryCountFilteredItemOrders(
                eq(id), eq(orderId), eq(productId), eq(minQuantity), eq(maxQuantity)
        );
        verify(spyItemOrderRepository).queryFindFilteredItemOrders(
                eq(id), eq(orderId), eq(productId), eq(minQuantity), eq(maxQuantity),
                eq(page), eq(linesPerPage), eq(direction), eq(orderBy)
        );
    }

    // Método queryFindFullDetailsItemOrders
    @Test
    @DisplayName("Should return full details item orders with all joins successfully")
    void shouldReturnFullDetailsItemOrdersWithAllJoins() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "asc";
        String orderBy = "p.name";

        // Mock do resultado da query (1 registro)
        Object[] mockRow = new Object[]{
                itemOrderId.toString(),  // i.id (String)
                2,                      // i.quantity (Number)
                15.99,                  // i.price (Number)
                productId.toString(),   // p.id (String)
                "Notebook",             // p.name (String)
                4999.99,                // p.price (Number)
                orderId.toString(),     // o.id (String)
                Timestamp.valueOf("2023-01-01 00:00:00"), // o.order_date (Timestamp)
                "CONFIRMED",            // o.status (String)
                clientId,               // c.id (Long)
                "João Silva",           // c.name (String)
                "joao@email.com"        // c.email (String)
        };
        List<Object[]> mockQueryResult = new ArrayList<>();
        mockQueryResult.add(mockRow);

        // Configura o mock da Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("itemOrderId"), eq(itemOrderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("productId"), eq(productId.toString()))).thenReturn(query);
        when(query.setParameter(eq("orderId"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("clientId"), eq(clientId))).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockQueryResult);

        // Act
        List<GlobalFullDetailsDto> result = itemOrderRepository.queryFindFullDetailsItemOrders(
                itemOrderId, productId, orderId, clientId,
                page, linesPerPage, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        GlobalFullDetailsDto dto = result.get(0);

        // Verifica ItemOrder
        assertEquals(itemOrderId, dto.getItemOrder().getId());
        assertEquals(2, dto.getItemOrder().getQuantity());
        assertEquals(15.99, dto.getItemOrder().getPrice());

        // Verifica Product
        assertEquals(productId, dto.getProduct().getId());
        assertEquals("Notebook", dto.getProduct().getName());
        assertEquals(4999.99, dto.getProduct().getPrice());

        // Verifica Order
        assertEquals(orderId, dto.getOrder().getId());
        assertEquals(LocalDateTime.of(2023, 1, 1, 0, 0), dto.getOrder().getOrderDate());
        assertEquals("CONFIRMED", dto.getOrder().getStatus());

        // Verifica Client
        assertEquals(clientId, dto.getClient().getId());
        assertEquals("João Silva", dto.getClient().getName());
        assertEquals("joao@email.com", dto.getClient().getEmail());

        // Verifica se a query foi chamada corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("itemOrderId", itemOrderId.toString());
        verify(query).setParameter("limit", linesPerPage);
        verify(query).setParameter("offset", page * linesPerPage);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when query execution fails")
    void shouldThrowClientErrorExceptionWhenQueryFailsToFindFullDetailsItemOrders() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "asc";
        String orderBy = "p.name";

        // Configura o mock para simular um erro na execução da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.queryFindFullDetailsItemOrders(
                    itemOrderId, productId, orderId, clientId,
                    page, linesPerPage, direction, orderBy
            );
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao buscar item dos pedidos filtrados com todos detalhes.", exception.getMessage());

        // Verifica se a query foi chamada antes do erro
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("itemOrderId"), eq(itemOrderId.toString()));
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).getResultList(); // O erro ocorre aqui
    }

    @Test
    @DisplayName("Should ignore null parameters when building the query")
    void shouldIgnoreNullParametersWhenBuildingQuery() {
        // Arrange
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "asc";
        String orderBy = "c.name";

        // Mock do resultado da query (vazio para simplificar)
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Act
        List<GlobalFullDetailsDto> result = itemOrderRepository.queryFindFullDetailsItemOrders(
                null, null, null, null, // itemOrderId, productId, orderId são null
                page, linesPerPage, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verifica se a query NÃO incluiu parâmetros nulos
        verify(query, never()).setParameter(eq("itemOrderId"), any());
        verify(query, never()).setParameter(eq("productId"), any());
        verify(query, never()).setParameter(eq("orderId"), any());

        // Verifica se os parâmetros não-nulos foram incluídos
        verify(query).setParameter("limit", linesPerPage);
        verify(query).setParameter("offset", page * linesPerPage);
    }

    // Método queryCountFullFilteredItemOrderDetails
    @Test
    @DisplayName("Should return count of full filtered item orders with valid parameters")
    void shouldReturnCountOfFullFilteredItemOrdersWithValidParameters() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;

        // Mock da Query e resultado esperado (COUNT = 5)
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(5L); // Simula 5 registros no banco

        // Configura os parâmetros esperados na query
        when(query.setParameter(eq("itemOrderId"), eq(itemOrderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("productId"), eq(productId.toString()))).thenReturn(query);
        when(query.setParameter(eq("orderId"), eq(orderId.toString()))).thenReturn(query);
        when(query.setParameter(eq("clientId"), eq(clientId))).thenReturn(query);

        // Act
        Long result = itemOrderRepository.queryCountFullFilteredItemOrderDetails(
                itemOrderId, productId, orderId, clientId);

        // Assert
        assertEquals(5L, result); // Verifica se o COUNT retornado é 5

        // Verifica se a query foi montada corretamente
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_item_orders i"));
        verify(em).createNativeQuery(contains("JOIN tb_products p ON i.product_id = p.id"));
        verify(em).createNativeQuery(contains("JOIN tb_orders o ON i.order_id = o.id"));
        verify(em).createNativeQuery(contains("JOIN tb_clients c ON o.client_id = c.id"));

        // Verifica se os parâmetros foram passados corretamente
        verify(query).setParameter("itemOrderId", itemOrderId.toString());
        verify(query).setParameter("productId", productId.toString());
        verify(query).setParameter("orderId", orderId.toString());
        verify(query).setParameter("clientId", clientId);
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should return count ignoring null parameters")
    void shouldReturnCountIgnoringNullParameters() {
        // Arrange
        Long expectedCount = 3L;

        // Mock da Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expectedCount);

        // Act
        Long result = itemOrderRepository.queryCountFullFilteredItemOrderDetails(
                null, // itemOrderId
                null, // productId
                null, // orderId
                null
        );

        // Assert
        assertEquals(expectedCount, result);

        // Verifica se apenas clientId foi usado como filtro
        verify(query, never()).setParameter(eq("clientId"), any());
        verify(query, never()).setParameter(eq("itemOrderId"), any());
        verify(query, never()).setParameter(eq("productId"), any());
        verify(query, never()).setParameter(eq("orderId"), any());

        // Verifica se a query contém os joins básicos
        verify(em).createNativeQuery(contains("JOIN tb_products p ON i.product_id = p.id"));
        verify(em).createNativeQuery(contains("JOIN tb_orders o ON i.order_id = o.id"));
        verify(em).createNativeQuery(contains("JOIN tb_clients c ON o.client_id = c.id"));
    }

    @Test
    @DisplayName("Should throw ClientErrorException when database query fails to count filtered item orders details")
    void shouldThrowClientErrorExceptionWhenQueryFailsToCountFilteredItemOrdersDetails() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;

        // Configura o mock para simular um erro na execução da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.getSingleResult()).thenThrow(new RuntimeException("Database connection failed"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderRepository.queryCountFullFilteredItemOrderDetails(
                    itemOrderId, productId, orderId, clientId
            );
        });

        // Verifica a mensagem de erro
        assertEquals("Erro ao contar todos items dos pedidos filtrados com todos detalhes.", exception.getMessage());

        // Verifica se a query foi chamada antes do erro
        verify(em).createNativeQuery(contains("SELECT COUNT(*) FROM tb_item_orders i"));
        verify(query).setParameter(eq("itemOrderId"), eq(itemOrderId.toString()));
        verify(query).getSingleResult(); // O erro ocorre aqui
    }

    // Método findFullDetailsItemOrders
    @Test
    @DisplayName("Should return paginated full details with valid parameters")
    void shouldReturnPaginatedFullDetailsWithValidParameters() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "asc";
        String orderBy = "p.name";

        // Cria um spy do repositório
        ItemOrderRepository spyItemOrderRepository = Mockito.spy(itemOrderRepository);

        // Mock da contagem total
        doReturn(2L)
                .when(spyItemOrderRepository)
                .queryCountFullFilteredItemOrderDetails(
                        eq(itemOrderId), eq(productId), eq(orderId), eq(clientId)
                );

        // Mock dos resultados detalhados
        List<GlobalFullDetailsDto> mockDetails = new ArrayList<>();

        GlobalFullDetailsDto detail1 = new GlobalFullDetailsDto();
        // ... (preencha com dados de exemplo)
        mockDetails.add(detail1);

        doReturn(mockDetails)
                .when(spyItemOrderRepository)
                .queryFindFullDetailsItemOrders(
                        eq(itemOrderId), eq(productId), eq(orderId), eq(clientId),
                        eq(page), eq(linesPerPage), eq(direction), eq(orderBy)
                );

        // Act
        GlobalPageDto<GlobalFullDetailsDto> result = spyItemOrderRepository.findFullDetailsItemOrders(
                itemOrderId, productId, orderId, clientId,
                page, linesPerPage, direction, orderBy
        );

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal()); // Total de registros
        assertEquals(1, result.getItems().size()); // Itens retornados

        // Verifica se as queries foram chamadas corretamente
        verify(spyItemOrderRepository).queryCountFullFilteredItemOrderDetails(
                eq(itemOrderId), eq(productId), eq(orderId), eq(clientId)
        );
        verify(spyItemOrderRepository).queryFindFullDetailsItemOrders(
                eq(itemOrderId), eq(productId), eq(orderId), eq(clientId),
                eq(page), eq(linesPerPage), eq(direction), eq(orderBy)
        );
    }

    @Test
    @DisplayName("Should handle null parameters correctly")
    void shouldHandleNullParametersCorrectly() {
        // Arrange
        Long clientId = 1L; // Apenas clientId fornecido
        ItemOrderRepository spyRepo = Mockito.spy(itemOrderRepository);

        doReturn(1L)
                .when(spyRepo)
                .queryCountFullFilteredItemOrderDetails(
                        isNull(), isNull(), isNull(), eq(clientId)
                );

        doReturn(new ArrayList<>())
                .when(spyRepo)
                .queryFindFullDetailsItemOrders(
                        isNull(), isNull(), isNull(), eq(clientId),
                        anyInt(), anyInt(), anyString(), anyString()
                );

        // Act
        GlobalPageDto<GlobalFullDetailsDto> result = spyRepo.findFullDetailsItemOrders(
                null, null, null, clientId, // IDs nulos
                0, 10, "asc", "i.quantity"
        );

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        // Verifica se os parâmetros nulos foram ignorados
        verify(spyRepo, never())
                .queryCountFullFilteredItemOrderDetails(any(), any(), any(), isNull());
    }

}
