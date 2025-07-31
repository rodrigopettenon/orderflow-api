package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.GlobalFullDetailsDto;
import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ItemOrderDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ItemOrderModel;
import com.rodrigopettenon.orderflow.models.OrderModel;
import com.rodrigopettenon.orderflow.models.ProductModel;
import com.rodrigopettenon.orderflow.repositories.ClientRepository;
import com.rodrigopettenon.orderflow.repositories.ItemOrderRepository;
import com.rodrigopettenon.orderflow.repositories.OrderRepository;
import com.rodrigopettenon.orderflow.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemOrderServiceTest {

    @InjectMocks
    private ItemOrderService itemOrderService;

    @Mock
    private ItemOrderRepository itemOrderRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientRepository clientRepository;

    private ItemOrderDto itemOrderDto;
    private OrderModel orderModel;
    private ProductModel productModel;

    @BeforeEach
    void setUp() {
        itemOrderDto = new ItemOrderDto();
        itemOrderDto.setOrderId(UUID.randomUUID());
        itemOrderDto.setProductId(UUID.randomUUID());
        itemOrderDto.setQuantity(2);

        orderModel = new OrderModel();
        orderModel.setId(itemOrderDto.getOrderId());

        productModel = new ProductModel();
        productModel.setId(itemOrderDto.getProductId());
        productModel.setPrice(10.99);
    }

    // Método saveItemOrder
    @Test
    @DisplayName("Should successfully save a new item order with valid data")
    void shouldSaveItemOrderWithValidData() {
        // Arrange
        when(orderRepository.existsOrderById(itemOrderDto.getOrderId())).thenReturn(true);
        when(productRepository.existsProductById(itemOrderDto.getProductId())).thenReturn(true);
        when(orderRepository.findOrderModelById(itemOrderDto.getOrderId())).thenReturn(orderModel);
        when(productRepository.findProductModelById(itemOrderDto.getProductId())).thenReturn(productModel);

        ItemOrderDto savedItemOrder = new ItemOrderDto();
        savedItemOrder.setId(UUID.randomUUID());
        savedItemOrder.setOrderId(itemOrderDto.getOrderId());
        savedItemOrder.setProductId(itemOrderDto.getProductId());
        savedItemOrder.setQuantity(itemOrderDto.getQuantity());
        savedItemOrder.setPrice(productModel.getPrice());

        when(itemOrderRepository.saveItemOrder(any(ItemOrderModel.class))).thenReturn(savedItemOrder);

        // Act
        ItemOrderDto result = itemOrderService.saveItemOrder(itemOrderDto);

        // Assert
        assertNotNull(result);
        assertEquals(itemOrderDto.getOrderId(), result.getOrderId());
        assertEquals(itemOrderDto.getProductId(), result.getProductId());
        assertEquals(itemOrderDto.getQuantity(), result.getQuantity());
        assertEquals(productModel.getPrice(), result.getPrice());
        assertNotNull(result.getId());

        verify(orderRepository).existsOrderById(itemOrderDto.getOrderId());
        verify(productRepository).existsProductById(itemOrderDto.getProductId());
        verify(orderRepository).findOrderModelById(itemOrderDto.getOrderId());
        verify(productRepository).findProductModelById(itemOrderDto.getProductId());
        verify(itemOrderRepository).saveItemOrder(any(ItemOrderModel.class));
    }

    @Test
    @DisplayName("Should throw exception when orderId is null")
    void shouldThrowExceptionWhenOrderIdIsNull() {
        // Arrange
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(null); // orderId nulo
        dto.setProductId(UUID.randomUUID());
        dto.setQuantity(1);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("O ID do pedido é obrigatório.", exception.getMessage());

        // Verifica que nenhum método de repository foi chamado
        verify(orderRepository, never()).existsOrderById(any());
        verify(productRepository, never()).existsProductById(any());
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when productId is null")
    void shouldThrowExceptionWhenProductIdIsNull() {
        // Arrange
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(UUID.randomUUID()); // orderId válido
        dto.setProductId(null); // productId nulo
        dto.setQuantity(1); // quantidade válida

        // Act & Assert
        when(orderRepository.existsOrderById(dto.getOrderId())).thenReturn(true);
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("O ID do produto é obrigatório.", exception.getMessage());

        // Verifica que nenhum método de repository foi chamado
        verify(productRepository, never()).existsProductById(any());
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is null")
    void shouldThrowExceptionWhenQuantityIsNull() {
        // Arrange
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(UUID.randomUUID()); // orderId válido
        dto.setProductId(UUID.randomUUID()); // productId válido
        dto.setQuantity(null); // quantidade nula

        // Mock para orderId e productId existirem (fluxo passa para validação da quantidade)
        when(orderRepository.existsOrderById(dto.getOrderId())).thenReturn(true);
        when(productRepository.existsProductById(dto.getProductId())).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("A quantidade do item do pedido é obrigatória.", exception.getMessage());

        // Verifica que verificou os IDs mas não prosseguiu com o save
        verify(orderRepository).existsOrderById(dto.getOrderId());
        verify(productRepository).existsProductById(dto.getProductId());
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when quantity is less than or equal to zero")
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        // Arrange
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(UUID.randomUUID());
        dto.setProductId(UUID.randomUUID());
        dto.setQuantity(0); // quantidade inválida

        // Mocks para IDs existentes
        when(orderRepository.existsOrderById(dto.getOrderId())).thenReturn(true);
        when(productRepository.existsProductById(dto.getProductId())).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("A quantidade do item do pedido deve ser maior que 0.", exception.getMessage());

        // Verificações
        verify(orderRepository).existsOrderById(dto.getOrderId());
        verify(productRepository).existsProductById(dto.getProductId());
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when orderId does not exist")
    void shouldThrowExceptionWhenOrderIdDoesNotExist() {
        // Arrange
        UUID nonExistentOrderId = UUID.randomUUID();
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(nonExistentOrderId);
        dto.setProductId(UUID.randomUUID());
        dto.setQuantity(1);

        // Mock para orderId não existir
        when(orderRepository.existsOrderById(nonExistentOrderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("O ID do pedido informado não está cadastrado.", exception.getMessage());

        // Verificações
        verify(orderRepository).existsOrderById(nonExistentOrderId);
        verify(productRepository, never()).existsProductById(any());
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    @Test
    @DisplayName("Should throw exception when productId does not exist")
    void shouldThrowExceptionWhenProductIdDoesNotExist() {
        // Arrange
        UUID nonExistentProductId = UUID.randomUUID();
        ItemOrderDto dto = new ItemOrderDto();
        dto.setOrderId(UUID.randomUUID());
        dto.setProductId(nonExistentProductId);
        dto.setQuantity(1);

        // Mocks
        when(orderRepository.existsOrderById(dto.getOrderId())).thenReturn(true);
        when(productRepository.existsProductById(nonExistentProductId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.saveItemOrder(dto);
        });

        assertEquals("O ID do produto informado não está cadastrado.", exception.getMessage());

        // Verificações
        verify(orderRepository).existsOrderById(dto.getOrderId());
        verify(productRepository).existsProductById(nonExistentProductId);
        verify(orderRepository, never()).findOrderModelById(any());
        verify(productRepository, never()).findProductModelById(any());
        verify(itemOrderRepository, never()).saveItemOrder(any());
    }

    // Método findFilteredItemOrders
    @Test
    @DisplayName("Should return filtered item orders successfully with valid parameters")
    void shouldReturnFilteredItemOrdersSuccessfully() {
        // Arrange
        UUID id = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer minQuantity = 1;
        Integer maxQuantity = 10;
        Integer page = 1;
        Integer linesPerPage = 20;
        String direction = "desc";
        String orderBy = "quantity";

        // Mock das validações
        when(itemOrderRepository.existsItemOrderById(id)).thenReturn(true);
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(productRepository.existsProductById(productId)).thenReturn(true);

        // Mock do retorno esperado
        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        expectedPage.setTotal(2L);
        expectedPage.setItems(List.of(
                new ItemOrderDto(),
                new ItemOrderDto()
        ));

        when(itemOrderRepository.findFilteredItemOrders(
                eq(id), eq(orderId), eq(productId), eq(minQuantity), eq(maxQuantity),
                eq(1), eq(20), eq("desc"), eq("quantity"))
        ).thenReturn(expectedPage);

        // Act
        GlobalPageDto<ItemOrderDto> result = itemOrderService.findFilteredItemOrders(
                id, orderId, productId, minQuantity, maxQuantity,
                page, linesPerPage, direction, orderBy);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getItems().size());

        // Verifica as validações
        verify(itemOrderRepository).existsItemOrderById(id);
        verify(orderRepository).existsOrderById(orderId);
        verify(productRepository).existsProductById(productId);

        // Verifica a chamada ao repository com os parâmetros corretos
        verify(itemOrderRepository).findFilteredItemOrders(
                eq(id), eq(orderId), eq(productId), eq(minQuantity), eq(maxQuantity),
                eq(1), eq(20), eq("desc"), eq("quantity"));
    }

    @Test
    @DisplayName("Should throw exception when item order ID does not exist")
    void shouldThrowExceptionWhenItemOrderIdDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        // Mock - ID não existe
        when(itemOrderRepository.existsItemOrderById(nonExistentId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    nonExistentId, orderId, productId,
                    1, 10, 0, 10, "asc", "order_id");
        });

        assertEquals("O ID do item do pedido informado não está cadastrado.", exception.getMessage());

        // Verificações
        verify(itemOrderRepository).existsItemOrderById(nonExistentId);
        verify(orderRepository, never()).existsOrderById(any());
        verify(productRepository, never()).existsProductById(any());
        verify(itemOrderRepository, never()).findFilteredItemOrders(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when order ID does not exist to find filtered item orders")
    void shouldThrowExceptionWhenOrderIdDoesNotExistToFindFilteredItemOrders() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID nonExistentOrderId = UUID.randomUUID();

        // Mocks
        when(itemOrderRepository.existsItemOrderById(itemOrderId)).thenReturn(true);
        when(orderRepository.existsOrderById(nonExistentOrderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    itemOrderId, nonExistentOrderId, null,
                    null, null, 0, 10, "asc", "order_id");
        });

        assertEquals("O ID do pedido informado não está cadastrado.", exception.getMessage());

        // Verificações
        verify(itemOrderRepository).existsItemOrderById(itemOrderId);
        verify(orderRepository).existsOrderById(nonExistentOrderId);
        verify(productRepository, never()).existsProductById(any());
        verify(itemOrderRepository, never()).findFilteredItemOrders(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when product ID does not exist to find filtered item orders")
    void shouldThrowExceptionWhenProductIdDoesNotExistToFindFilteredItemOrders() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID nonExistentProductId = UUID.randomUUID();

        // Mocks
        when(itemOrderRepository.existsItemOrderById(itemOrderId)).thenReturn(true);
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(productRepository.existsProductById(nonExistentProductId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    itemOrderId, orderId, nonExistentProductId,
                    null, null, 0, 10, "asc", "product_id");
        });

        assertEquals("O ID do produto informado não está cadastrado.", exception.getMessage());

        // Verificações
        verify(itemOrderRepository).existsItemOrderById(itemOrderId);
        verify(orderRepository).existsOrderById(orderId);
        verify(productRepository).existsProductById(nonExistentProductId);
        verify(itemOrderRepository, never()).findFilteredItemOrders(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when min quantity is invalid to find filtered item orders.")
    void shouldThrowExceptionWhenMinQuantityIsInvalidToFindFilteredItemOrders() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        int invalidMinQuantity = 0;

        // Mocks para IDs existentes
        when(itemOrderRepository.existsItemOrderById(itemOrderId)).thenReturn(true);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    itemOrderId, null, null,
                    invalidMinQuantity, 10, 0, 10, "asc", "quantity");
        });

        assertEquals("A quantidade mínima do item do pedido deve ser maior que 0.", exception.getMessage());

        // Verificações
        verify(itemOrderRepository).existsItemOrderById(itemOrderId);
        verify(itemOrderRepository, never()).findFilteredItemOrders(any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when max quantity is invalid (<= 0)")
    void shouldThrowExceptionWhenMaxQuantityIsInvalid() {
        // Arrange
        int invalidMaxQuantity = 0;

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    null, null, null,
                    1, invalidMaxQuantity, // maxQuantity = 0 (inválido)
                    0, 10, "asc", "quantity");
        });

        assertEquals("A quantidade máxima do item do pedido deve ser maior que 0.", exception.getMessage());

        // Verificações
        verify(itemOrderRepository, never()).existsItemOrderById(any());
        verify(orderRepository, never()).existsOrderById(any());
        verify(productRepository, never()).existsProductById(any());
    }

    @Test
    @DisplayName("Should throw exception when min quantity > max quantity")
    void shouldThrowExceptionWhenMinQuantityGreaterThanMax() {
        // Arrange
        int minQuantity = 10;
        int maxQuantity = 5;

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFilteredItemOrders(
                    null, null, null,
                    minQuantity, maxQuantity,
                    0, 10, "asc", "quantity");
        });

        assertEquals("A quantidade máxima não pode ser menor que a quantidade mínima.", exception.getMessage());
    }

    @Test
    @DisplayName("Should apply default values when pagination parameters are invalid")
    void shouldApplyDefaultsForInvalidPaginationParams() {
        // Arrange
        when(itemOrderRepository.existsItemOrderById(any())).thenReturn(true);

        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                eq(0), eq(10), eq("asc"), eq("order_id"))
        ).thenReturn(expectedPage);

        // Act
        GlobalPageDto<ItemOrderDto> result = itemOrderService.findFilteredItemOrders(
                UUID.randomUUID(), null, null,
                1, 10,
                -1, 0, "invalid", "invalid_field");

        // Assert
        assertNotNull(result);

        // Verifica se chamou com os valores padrão corrigidos
        verify(itemOrderRepository).findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                eq(0), eq(10), eq("asc"), eq("order_id"));
    }

    @Test
    @DisplayName("Should apply default direction when invalid")
    void shouldApplyDefaultDirectionWhenInvalid() {
        // Arrange
        when(itemOrderRepository.existsItemOrderById(any())).thenReturn(true);

        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                anyInt(), anyInt(), eq("asc"), anyString())
        ).thenReturn(expectedPage);

        // Act
        itemOrderService.findFilteredItemOrders(
                UUID.randomUUID(), null, null,
                1, 10,
                0, 10, "invalid_direction", "quantity");

        // Assert
        verify(itemOrderRepository).findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                anyInt(), anyInt(), eq("asc"), anyString());
    }

    @Test
    @DisplayName("Should apply default orderBy when invalid")
    void shouldApplyDefaultOrderByWhenInvalid() {
        // Arrange
        when(itemOrderRepository.existsItemOrderById(any())).thenReturn(true);

        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                anyInt(), anyInt(), anyString(), eq("order_id"))
        ).thenReturn(expectedPage);

        // Act
        itemOrderService.findFilteredItemOrders(
                UUID.randomUUID(), null, null,
                1, 10,
                0, 10, "asc", "invalid_field");

        // Assert
        verify(itemOrderRepository).findFilteredItemOrders(
                any(), any(), any(), any(), any(),
                anyInt(), anyInt(), anyString(), eq("order_id"));
    }

    @Test
    @DisplayName("Should succeed when all IDs are null but quantities are valid")
    void shouldSucceedWhenAllIdsAreNull() {
        // Arrange
        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                isNull(), isNull(), isNull(), eq(1), eq(10),
                anyInt(), anyInt(), anyString(), anyString())
        ).thenReturn(expectedPage);

        // Act
        GlobalPageDto<ItemOrderDto> result = itemOrderService.findFilteredItemOrders(
                null, null, null,
                1, 10,
                0, 10, "asc", "quantity");

        // Assert
        assertNotNull(result);
        verify(itemOrderRepository, never()).existsItemOrderById(any());
        verify(orderRepository, never()).existsOrderById(any());
        verify(productRepository, never()).existsProductById(any());
    }

    @Test
    @DisplayName("Should validate only orderId when others IDs are null")
    void shouldValidateOnlyOrderIdWhenOthersNull() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);

        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                isNull(), eq(orderId), isNull(), any(), any(),
                anyInt(), anyInt(), anyString(), anyString())
        ).thenReturn(expectedPage);

        // Act
        itemOrderService.findFilteredItemOrders(
                null, orderId, null,
                null, null,
                0, 10, "asc", "order_id");

        // Assert
        verify(orderRepository).existsOrderById(orderId);
        verify(itemOrderRepository, never()).existsItemOrderById(any());
        verify(productRepository, never()).existsProductById(any());
    }

    @Test
    @DisplayName("Should accept minimum valid quantity (1)")
    void shouldAcceptMinimumValidQuantity() {
        // Arrange
        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                any(), any(), any(), eq(1), eq(Integer.MAX_VALUE),
                anyInt(), anyInt(), anyString(), anyString())
        ).thenReturn(expectedPage);

        // Act
        assertDoesNotThrow(() -> {
            itemOrderService.findFilteredItemOrders(
                    null, null, null,
                    1, Integer.MAX_VALUE,
                    0, 10, "asc", "quantity");
        });
    }

    @Test
    @DisplayName("Should succeed when only minQuantity is provided")
    void shouldSucceedWhenOnlyMinQuantityProvided() {
        // Arrange
        GlobalPageDto<ItemOrderDto> expectedPage = new GlobalPageDto<>();
        when(itemOrderRepository.findFilteredItemOrders(
                any(), any(), any(), eq(5), isNull(),
                anyInt(), anyInt(), anyString(), anyString())
        ).thenReturn(expectedPage);

        // Act
        assertDoesNotThrow(() -> {
            itemOrderService.findFilteredItemOrders(
                    null, null, null,
                    5, null,
                    0, 10, "asc", "quantity");
        });
    }

    // método findFullDetailsItemOrders
    @Test
    @DisplayName("Should return full details item orders successfully with valid parameters")
    void shouldReturnFullDetailsItemOrdersSuccessfully() {
        // Arrange
        UUID itemOrderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Long clientId = 1L;
        Integer page = 1;
        Integer linesPerPage = 20;
        String direction = "desc";
        String orderBy = "p.name";

        // Mock das validações
        when(itemOrderRepository.existsItemOrderById(itemOrderId)).thenReturn(true);
        when(productRepository.existsProductById(productId)).thenReturn(true);
        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(clientRepository.existsClientById(clientId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByProductId(productId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByOrderId(orderId)).thenReturn(true);
        when(orderRepository.existsOrderByClientId(clientId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByClientId(clientId)).thenReturn(true);

        // Mock do retorno esperado
        GlobalPageDto<GlobalFullDetailsDto> expectedPage = new GlobalPageDto<>();
        expectedPage.setTotal(2L);
        expectedPage.setItems(List.of(
                new GlobalFullDetailsDto(),
                new GlobalFullDetailsDto()
        ));

        when(itemOrderRepository.findFullDetailsItemOrders(
                eq(itemOrderId), eq(productId), eq(orderId), eq(clientId),
                eq(1), eq(20), eq("desc"), eq("p.name"))
        ).thenReturn(expectedPage);

        // Act
        GlobalPageDto<GlobalFullDetailsDto> result = itemOrderService.findFullDetailsItemOrders(
                itemOrderId, productId, orderId, clientId,
                page, linesPerPage, direction, orderBy);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getItems().size());

        // Verifica as validações
        verify(itemOrderRepository).existsItemOrderById(itemOrderId);
        verify(productRepository).existsProductById(productId);
        verify(orderRepository).existsOrderById(orderId);
        verify(clientRepository).existsClientById(clientId);
        verify(itemOrderRepository).existsItemOrderByProductId(productId);
        verify(itemOrderRepository).existsItemOrderByOrderId(orderId);
        verify(orderRepository).existsOrderByClientId(clientId);
        verify(itemOrderRepository).existsItemOrderByClientId(clientId);

        // Verifica a chamada ao repository com os parâmetros corretos
        verify(itemOrderRepository).findFullDetailsItemOrders(
                eq(itemOrderId), eq(productId), eq(orderId), eq(clientId),
                eq(1), eq(20), eq("desc"), eq("p.name"));
    }

    @Test
    @DisplayName("Should throw exception when itemOrder ID does not exist to find full details item orders")
    void shouldThrowExceptionWhenItemOrderIdDoesNotExistToFindFullDetailsItemOrders() {
        // Arrange
        UUID nonExistentItemOrderId = UUID.randomUUID();

        when(itemOrderRepository.existsItemOrderById(nonExistentItemOrderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    nonExistentItemOrderId, null, null, null,
                    0, 10, "asc", "i.quantity");
        });

        assertEquals("O ID do item do pedido informado não está cadastrado.", exception.getMessage());

        verify(itemOrderRepository).existsItemOrderById(nonExistentItemOrderId);
        verify(itemOrderRepository, never()).findFullDetailsItemOrders(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when product ID does not exist to find full details item orders")
    void shouldThrowExceptionWhenProductIdDoesNotExistToFindFullDetailsItemOrders() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(productRepository.existsProductById(productId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, productId, null, null,
                    0, 10, "asc", "p.name");
        });

        assertEquals("O ID do produto informado não está cadastrado.", exception.getMessage());

        verify(productRepository).existsProductById(productId);
    }

    @Test
    @DisplayName("Should throw exception when order ID does not exist to find full details item orders")
    void shouldThrowExceptionWhenOrderIdDoesNotExistToFindFullDetailsItemOrders() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, null, orderId, null,
                    0, 10, "asc", "o.order_date");
        });

        assertEquals("O ID do pedido informado não está cadastrado.", exception.getMessage());

        verify(orderRepository).existsOrderById(orderId);
    }

    @Test
    @DisplayName("Should throw exception when no items linked to order ID")
    void shouldThrowExceptionWhenNoItemsLinkedToOrderId() {
        // Arrange
        UUID orderId = UUID.randomUUID();

        when(orderRepository.existsOrderById(orderId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByOrderId(orderId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, null, orderId, null,
                    0, 10, "asc", "i.price");
        });

        assertEquals("Nenhum item de pedido cadastrado com o ID do pedido informado.", exception.getMessage());

        verify(itemOrderRepository).existsItemOrderByOrderId(orderId);
    }

    @Test
    @DisplayName("Should throw exception when no items linked to product ID")
    void shouldThrowExceptionWhenNoItemsLinkedToProductId() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(productRepository.existsProductById(productId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByProductId(productId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, productId, null, null,
                    0, 10, "asc", "p.price");
        });

        assertEquals("Nenhum item de pedido cadastrado com o ID do produto informado.", exception.getMessage());

        verify(itemOrderRepository).existsItemOrderByProductId(productId);
    }

    @Test
    @DisplayName("Should throw exception when no client linked to client ID")
    void shouldThrowExceptionWhenNoClientLinkedToClientId() {
        // Arrange
        Long clientId = 1L;

        when(clientRepository.existsClientById(clientId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, null, null, clientId,
                    0, 10, "asc", "o.order_date");
        });

        assertEquals("O ID do cliente informado não está cadastrado.", exception.getMessage());

        verify(clientRepository).existsClientById(clientId);
    }

    @Test
    @DisplayName("Should throw exception when no orders linked to client ID")
    void shouldThrowExceptionWhenNoOrdersLinkedToClientId() {
        // Arrange
        Long clientId = 1L;

        when(clientRepository.existsClientById(clientId)).thenReturn(true);
        when(orderRepository.existsOrderByClientId(clientId)).thenReturn(false);

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            itemOrderService.findFullDetailsItemOrders(
                    null, null, null, clientId,
                    0, 10, "asc", "o.order_date");
        });

        assertEquals("Nenhum pedido cadastrado com o ID do cliente informado.", exception.getMessage());

        verify(orderRepository).existsOrderByClientId(clientId);
    }

    @Test
    @DisplayName("Should apply default pagination when params are null")
    void shouldApplyDefaultPagination() {
        when(itemOrderRepository.existsItemOrderByOrderId(any())).thenReturn(true);
        when(orderRepository.existsOrderById(any())).thenReturn(true);

        itemOrderService.findFullDetailsItemOrders(
                null, null, UUID.randomUUID(), null,
                null, null, null, null);

        verify(itemOrderRepository).findFullDetailsItemOrders(
                any(), any(), any(), any(),
                eq(0), eq(10), eq("asc"), eq("i.quantity"));
    }

    @Test
    @DisplayName("Should apply default orderBy when invalid")
    void shouldUseDefaultOrderByWhenInvalid() {
        when(orderRepository.existsOrderById(any())).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByOrderId(any())).thenReturn(true);

        itemOrderService.findFullDetailsItemOrders(
                null, null, UUID.randomUUID(), null,
                0, 10, "asc", "invalid_column");

        verify(itemOrderRepository).findFullDetailsItemOrders(
                any(), any(), any(), any(),
                anyInt(), anyInt(), anyString(), eq("i.quantity"));
    }

    @Test
    @DisplayName("Should throw exception when no items linked to client ID")
    void shouldThrowWhenNoItemsForClient() {
        Long clientId = 1L;
        when(clientRepository.existsClientById(clientId)).thenReturn(true);
        when(orderRepository.existsOrderByClientId(clientId)).thenReturn(true);
        when(itemOrderRepository.existsItemOrderByClientId(clientId)).thenReturn(false);

        assertThrows(ClientErrorException.class, () ->
                itemOrderService.findFullDetailsItemOrders(
                        null, null, null, clientId, 0, 10, "asc", "i.quantity"));
    }
}
