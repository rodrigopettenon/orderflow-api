package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.ProductDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //Método save
    @Test
    @DisplayName("Should save the product successfully.")
    void shouldSaveTheProductSuccessfully() {
        // Arrange (Criamos um DTO válido para o salvamento do produto)
        ProductDto productDto = new ProductDto();
        productDto.setName("Bala Fini"); // Nome válido
        productDto.setPrice(4.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 5, 2)); // Data de vencimento válida
        productDto.setSku("F2I0N2I6"); // SKU válido

        // Simulamos que o produto não existe no banco
        when(productRepository.existsProductBySku(productDto.getSku())).thenReturn(false);

        // Executamos o método que queremos testar
        productService.saveProduct(productDto);

        // Verificamos se método save do repository foi usado pois todos dados enviados são válidos
        verify(productRepository).saveProduct(productDto);

    }

    @Test
    @DisplayName("Should ThrowException when name is null to save a product")
    void shouldThrowExceptionWhenNameIsNullToSaveAProduct() {
        // Arrange (Criamos um DTO com nome nulo)
        ProductDto productDto = new ProductDto();
        productDto.setName(null); // Nome null
        productDto.setPrice(4.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 5, 2)); // Data de vencimento válida
        productDto.setSku("F2I0N2I6"); // SKU válido

        // Afirma exceção pois o nome é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Should ThrowException when name contains blank spaces to save a product.")
    void shouldThrowExceptionWhenNameContainsBlankSpacesToSaveAProduct() {
        // Arrange (Criamos um DTO com nome de espaços em branco)
        ProductDto productDto = new ProductDto();
        productDto.setName("       "); // Nome de espaços em branco
        productDto.setPrice(4.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 5, 2)); // Data de vencimento válida
        productDto.setSku("F2I0N2I6"); // SKU válido

        // Afirma exceção pois o nome tem apenas espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }


    @Test
    @DisplayName("Should ThrowException when name contains 3 characters or less to save a product.")
    void shouldThrowExceptionWhenNameContains3CharactersOrLessToSaveAProduct() {
        // Arrange (Criamos um DTO com nome de 3 caracteres ou menor)
        ProductDto productDto = new ProductDto();
        productDto.setName("Uno"); // Nome com 3 caracteres ou menor
        productDto.setPrice(4.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 5, 2)); // Data de vencimento válida
        productDto.setSku("F2I0N2I6"); // SKU válido

        // Afirma exceção pois o nome tem apenas 3 caracteres ou menos.
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto deve ter mais de 3 caracteres.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());

    }

    @Test
    @DisplayName("Should ThrowException when price is null to save a product.")
    void shouldThrowExceptionWhenPriceIsNullToSaveAProduct() {
        // Arrange (Criamos um DTO com preço null)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(null); // Preço null
        productDto.setExpiration(LocalDate.of(3000, 10, 2)); // Data de vencimento válida
        productDto.setSku("AR2R6OZT"); // SKU válido

        // Afirma exceção pois o preço é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço do produto é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());

    }

    @Test
    @DisplayName("Should ThrowException when price is 0 or less to save a product.")
    void shouldThrowExceptionWhenPriceIs0OrLessToSaveAProduct() {
        // Arrange (Criamos um DTO com preço 0 ou negativo)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(-1.0); // Preço 0 ou negativo
        productDto.setExpiration(LocalDate.of(3000, 10, 2)); // Data de vencimento válida
        productDto.setSku("AR2R6OZT"); // SKU válido

        // Afirma exceção pois o preço é negativo ou é 0
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço do produto deve ser maior que 0.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());

    }

    @Test
    @DisplayName("Should ThrowException when expiration date is null to save a product.")
    void shouldThrowExceptionWhenExpirationDateIsNullToSaveAProduct() {
        // Arrange (Criamos um DTO com data de vencimento null)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(null); // Data de vencimento null
        productDto.setSku("AR2R6OZT"); // SKU válido

        // Afirma exceção pois a data de vencimento é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de vencimento do produto é obrigatória.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());

    }

    @Test
    @DisplayName("Should ThrowException when the expiration date is in the past to save a product.")
    void shouldThrowExceptionWhenTheExpirationDateIsInThePastToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU null)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(2020, 3, 6)); // Data de vencimento no passado
        productDto.setSku("AR2R6OZT"); // SKU válido

        // Afirma exceção pois a data de vencimento é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de vencimento do produto não pode estar no passado.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());

    }

    @Test
    @DisplayName("Should ThrowException when SKU is null to save a product.")
    void shouldThrowExceptionWhenSKUIsNullToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU null)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 3, 6)); // Data de vencimento válida
        productDto.setSku(null); // SKU null

        // Afirma exceção pois o SKU é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU contains blank spaces to save a product.")
    void shouldThrowExceptionWhenSKUContainsBlankSpacesToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU de espaços em branco)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 3, 6)); // Data de vencimento válida
        productDto.setSku("        "); // SKU de espaços em branco

        // Afirma exceção pois o SKU contém espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU does not contain 8 characters to save a product.")
    void shouldThrowExceptionWhenSKUDoesNotContain8CharactersToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU que não tem 8 caracteres)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 3, 6)); // Data de vencimento válida
        productDto.setSku("LA827D"); // SKU que não tem 8 caracteres

        // Afirma exceção pois o SKU não contém 8 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ter 8 caracteres.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU is not alphanumeric to save a product.")
    void shouldThrowExceptionWhenSKUIsNotAlphanumericToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU que não é alfanumérico)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 3, 6)); // Data de vencimento válida
        productDto.setSku("_?¨$$@{}"); // SKU que é alfanumérico

        // Afirma exceção pois o SKU não é alfanumérico
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ser alfanumerico.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU is already registered to save a product.")
    void shouldThrowExceptionWhenSKUIsAlreadyRegisteredToSaveAProduct() {
        // Arrange (Criamos um DTO com SKU "supostamente" já está cadastrado no banco)
        ProductDto productDto = new ProductDto();
        productDto.setName("Arroz Tio Urbano"); // Nome válido
        productDto.setPrice(32.99); // Preço válido
        productDto.setExpiration(LocalDate.of(3000, 3, 6)); // Data de vencimento válida
        productDto.setSku("TIO2U6RB"); // SKU que válido porém já "cadastrado"

        // Simulamos que o SKU já está cadastrado no banco
        when(productRepository.existsProductBySku(productDto.getSku())).thenReturn(true);

        // Afirma exceção pois o SKU "supostamente" já está cadastrado no banco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.saveProduct(productDto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU informado já está cadastrado.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).saveProduct(any());
    }


}