package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
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
import java.util.ArrayList;
import java.util.List;

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


    //Método findBySku
    @Test
    @DisplayName("Should return the product successfully found by sku.")
    void shouldReturnTheProductSuccessfullyFundBySku() {
        // Arrange (Informamos um SKU válido que "supostamente" existe no banco)
        String sku = "A2R02ODC"; // SKU válido


        // DTO do produto que queremos retornar (porque estamos simulando que ele existe)
        ProductDto expectedProduct = new ProductDto();
        expectedProduct.setName("Sobrecoxa de Frango C.Vale"); // Nome válido
        expectedProduct.setPrice(10.50); // Preço válido
        expectedProduct.setSku(sku); // SKU criado anteriormente
        expectedProduct.setExpiration(LocalDate.of(3000, 10 , 2)); // data válida e futura

        // Simulamos que o SKU existe no nosso banco de dados
        when(productRepository.existsProductBySku(sku)).thenReturn(true);

        // Simulamos que o foi retornado o DTO do SKU informado
        when(productRepository.findProductBySku(sku)).thenReturn(expectedProduct);

        // Executamos o método que queremos testar
        ProductDto returnedProduct = productService.findBySku(sku);


        // Verificamos que o DTO retornado pelo método é igual ao esperado
        assertEquals(returnedProduct.getName(), expectedProduct.getName());
        assertEquals(returnedProduct.getPrice(), expectedProduct.getPrice());
        assertEquals(returnedProduct.getExpiration(), expectedProduct.getExpiration());
        assertEquals(returnedProduct.getSku(), expectedProduct.getSku());

        // Verificamos que o método do repository foi utilizado (porque o SKU é válido e existe no banco)
        verify(productRepository).findProductBySku(sku);

    }

    @Test
    @DisplayName("Should ThrowException when sku is null to find a product.")
    void shouldThrowExceptionWhenSkuIsNullToFindAProduct() {
        // Arrange (Informamos um SKU null)
        String sku = null;

        // Afirmamos uma exceção pois o SKU é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           productService.findBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku contains blank spaces to find a product.")
    void shouldThrowExceptionWhenContainsBlankSpacesToFindAProduct() {
        // Arrange (Informamos um SKU de espaços em branco)
        String sku = "         ";

        // Afirmamos uma exceção pois o SKU tem somente espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku does not contain 8 characters to find a product.")
    void shouldThrowExceptionWhenSkuDoesNotContains8CharactersToFindAProduct() {
        // Arrange (Informamos um SKU que não tenha 8 caracteres)
        String sku = "801U24I01JK192U3";

        // Afirmamos uma exceção pois o SKU não tem exatamente 8 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ter 8 caracteres.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not alphanumeric to find a product.")
    void shouldThrowExceptionWhenSkuIsNotAlphanumericToFindAProduct() {
        // Arrange (Informamos um SKU que não é alfanumérico)
        String sku = "?&#$%!@#";

        // Afirmamos uma exceção pois o SKU não é alfanumérico
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ser alfanumerico.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not registered to find a product.")
    void shouldThrowExceptionWhenSkuIsNotRegisteredToFindAProduct() {
        // Arrange (Informamos um SKU que "supostamente" não está cadastrado no banco de dados)
        String sku = "A2R02ODC";

        // Simulamos que o SKU não está cadastrado no banco de dados
        when(productRepository.existsProductBySku(sku)).thenReturn(false);

        // Afirmamos uma exceção pois o SKU não está cadastrado no banco de dados
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU informado não está cadastrado.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findProductBySku(any());
    }

    // Método updateBySku
    @Test
    @DisplayName("Should successfully return the updated product.")
    void shouldSuccessfullyReturnTheUpdatedProduct() {
        // Arrange (Informamos um SKU que "supostamente" existe no nosso banco)
        String sku = "A2R02ODC";

        // DTO do produto atualizado que queremos que o método retorne (porque nosso produto "supostamente" existe no banco)
        // Sem informar o sku pois ele não pode ser alterado
        ProductDto expectedProduct = new ProductDto();
        expectedProduct.setName("Sobrecoxa de Frango C.Vale"); // Nome válido
        expectedProduct.setPrice(10.50); // Preço válido
        expectedProduct.setExpiration(LocalDate.of(3000, 10 , 2)); // data válida e futura

        // Simulamos que existe um produto com o SKU informado no nosso banco de dados
        when(productRepository.existsProductBySku(sku)).thenReturn(true);

        // Simulamos que o nosso método do repository atualiza o produto e retorna o dto do produto atualizado
        when(productRepository.updateProductBySku(sku, expectedProduct)).thenReturn(expectedProduct);

        // Executamos o método que queremos testar
        ProductDto returnedProduct = productService.updateBySku(sku, expectedProduct);

        // Verificamos que o produto retornado pelo método é igual ao esperado
        assertEquals(returnedProduct.getName(), expectedProduct.getName());
        assertEquals(returnedProduct.getPrice(), expectedProduct.getPrice());
        assertEquals(returnedProduct.getSku(), expectedProduct.getSku());
        assertEquals(returnedProduct.getExpiration(), expectedProduct.getExpiration());

        // Verificamos se o método do repository foi usado como o esperado
        verify(productRepository).updateProductBySku(sku, expectedProduct);

    }

    @Test
    @DisplayName("Should ThrowException when name is null to update product by sku.")
    void shouldThrowExceptionWhenNameIsNullToUpdateProductBySku() {
        // Arrange (informamos um nome null)
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName(null); // Nome null
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de vencimento válida e no futuro

        // Afirmamos uma exceção pois o nome é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when name contains blank spaces to update product by sku.")
    void shouldThrowExceptionWhenNameContainsBlankSpacesToUpdateProductBySku() {
        // Arrange (informamos um nome com espaços em branco )
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("           "); // Nome que contém apenas espaços em branco
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de vencimento válida e no futuro

        // Afirmamos uma exceção pois o nome contém apenas espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when name contains 3 characters or less to update product by sku.")
    void shouldThrowExceptionWhenNameContains3CharactersOrLessToUpdateProductBySku() {
        // Arrange (informamos um nome com 3 caracteres ou menos )
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Uva"); // Nome que contém 3 caracteres ou menos
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de vencimento válida e no futuro

        // Afirmamos uma exceção pois o nome contém 3 caracteres ou menos
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do produto deve ter mais de 3 caracteres.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when price is null to update product by sku.")
    void shouldThrowExceptionWhenPriceIsNullToUpdateProductBySku() {
        // Arrange (informamos o preço null)
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(null); // Preço null
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de vencimento válida e no futuro

        // Afirmamos uma exceção pois o preço é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when price is 0 or a negative number to update product by sku.")
    void shouldThrowExceptionWhenPriceIs0OrNegativeNumberToUpdateProductBySku() {
        // Arrange (informamos o preço com 0 ou número negativo)
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(0.0); // O preço é 0 ou é um número negativo
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de vencimento válida e no futuro

        // Afirmamos uma exceção pois o preço é 0 ou um número negativo
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço do produto deve ser maior que 0.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when expiration date is null to update product by sku.")
    void shouldThrowExceptionWhenExpirationDateIsNullToUpdateProductBySku() {
        // Arrange (informamos uma data de vencimento null)
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(null); // data de vencimento null

        // Afirmamos uma exceção pois a data de vencimento é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de vencimento do produto é obrigatória.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when expiration date is in the past to update product by sku.")
    void shouldThrowExceptionWhenExpirationDateIsInThePastToUpdateProductBySku() {
        // Arrange (informamos uma data de vencimento no passado)
        String sku = "N5O8TREG"; // SKU válido

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(2020, 1, 1)); // data de vencimento no passado

        // Afirmamos uma exceção pois a data de vencimento está no passado
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de vencimento do produto não pode estar no passado.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is null to update product by sku.")
    void shouldThrowExceptionWhenIsNullToUpdateProductBySku() {
        // Arrange (informamos um um sku null)
        String sku = null; // SKU null

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de válida e no futuro

        // Afirmamos uma exceção pois o sku é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when sku contains blank spaces to update product by sku.")
    void shouldThrowExceptionWhenSkuContainsBlankSpacesToUpdateProductBySku() {
        // Arrange (informamos um sku com espaços em branco)
        String sku = "        "; // SKU com espaços em branco

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço váli do
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de válida e no futuro

        // Afirmamos uma exceção pois o sku contém apenas espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when sku does not contain 8 characters to update product by sku.")
    void shouldThrowExceptionWhenSkuDoesNotContain8CharactersToUpdateProductBySku() {
        // Arrange (informamos um sku não tem exatamente 8 caracteres)
        String sku = "K1293UJ23MKJ23MN2"; // SKU não tem exatamente 8 caracteres

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de válida e no futuro

        // Afirmamos uma exceção pois o sku não contém exatamente 8 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ter 8 caracteres.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not alphanumeric to update product by sku.")
    void shouldThrowExceptionWhenSkuIsNotAlphaNumericToUpdateProductBySku() {
        // Arrange (informamos um sku que não é alfanumérico)
        String sku = "(&*&*#$%"; // SKU que não é alfanumérico

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de válida e no futuro

        // Afirmamos uma exceção pois o sku não é alfanumérico
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ser alfanumerico.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not registered to update product by sku.")
    void shouldThrowExceptionWhenSkuIsNotRegisteredToUpdateProductBySku() {
        // Arrange (informamos um sku válido)
        String sku = "N5O8TREG"; // SKU válido mas que "supostamente" não está cadastrado no banco de dados

        // DTO sem SKU pois ele é enviado separadamente e não pode ser atualizado
        ProductDto dto = new ProductDto();
        dto.setName("Lasanha Bolonhesa Sadia"); // Nome válido
        dto.setPrice(10.50); // Preço válido
        dto.setExpiration(LocalDate.of(3000, 1, 1)); // data de válida e no futuro

        // Afirmamos uma exceção pois o sku não está cadastrado no banco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.updateBySku(sku, dto);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU informado não está cadastrado.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).updateProductBySku(any(), any());
    }

    //Método deleteBySku
    @Test
    @DisplayName("Should successfully delete the product by sku.")
    void shouldSuccessfullyDeleteTheProductBySku() {
        // Arrange (informamos um SKU válido e que "supostamente existe no banco)
        String sku = "N5O8TREG";

        // Simulamos que o SKU existe no nosso banco de dados
        when(productRepository.existsProductBySku(sku)).thenReturn(true);

        // Executamos o método que queremos testar
        productService.deleteBySku(sku);

        // Verificamos se o método do repository foi usado como o esperado (porque o SKU existe)
        verify(productRepository).deleteProductBySku(sku);
    }

    @Test
    @DisplayName("Should ThrowException when sku is null to delete a product.")
    void shouldThrowExceptionWhenSkuIsNullToDeleteAProduct() {
        // Arrange (informamos um SKU null)
        String sku = null;

        // Afirmamos uma exceção pois o sku é null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
           productService.deleteBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o repository realmente não foi usado (porque falhou antes)
        verify(productRepository, never()).deleteProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku contains blank spaces to delete a product.")
    void shouldThrowExceptionWhenSkuContainsBlankSpacesToDeleteAProduct() {
        // Arrange (informamos um SKU com espaços em branco)
        String sku = "            ";

        // Afirmamos uma exceção pois o sku contém espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
            productService.deleteBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto é obrigatório.", exception.getMessage());

        // Verificamos se o repository realmente não foi usado (porque falhou antes)
        verify(productRepository, never()).deleteProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku does not contain 8 characters to delete a product.")
    void shouldThrowExceptionWhenSkuDoesNotContain8CharactersToDeleteAProduct() {
        // Arrange (informamos um SKU que não contenha exatamente 8 caracteres)
        String sku = "HJ5FAS0DFH1324JH";

        // Afirmamos uma exceção pois o sku não contém exatos 8 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
            productService.deleteBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ter 8 caracteres.", exception.getMessage());

        // Verificamos se o repository realmente não foi usado (porque falhou antes)
        verify(productRepository, never()).deleteProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not alphanumeric to delete a product.")
    void shouldThrowExceptionWhenSkuIsNotAlphanumericToDeleteAProduct() {
        // Arrange (informamos um SKU que não é alfanumérico)
        String sku = "$#!@#$¨%";

        // Afirmamos uma exceção pois o sku que não é alfanumérico
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
            productService.deleteBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ser alfanumerico.", exception.getMessage());

        // Verificamos se o repository realmente não foi usado (porque falhou antes)
        verify(productRepository, never()).deleteProductBySku(any());
    }

    @Test
    @DisplayName("Should ThrowException when sku is not registered to delete a product.")
    void shouldThrowExceptionWhenSkuIsNotRegisteredToDeleteAProduct() {
        // Arrange (informamos um SKU que "supostamente" não está cadastrado)
        String sku = "TEST2026"; // sku válido

        // Simulamos que o SKU não está cadastrado no banco
        when(productRepository.existsProductBySku(sku)).thenReturn(false);

        // Afirmamos uma exceção pois o sku "supostamente" não está cadastrado
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
            productService.deleteBySku(sku);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU informado não está cadastrado.", exception.getMessage());

        // Verificamos se o repository realmente não foi usado (porque falhou antes)
        verify(productRepository, never()).deleteProductBySku(any());
    }


    // Método findFilteredProducts
    @Test
    @DisplayName("Should successfully return the filtered products.")
    void shouldSuccessfullyReturnTheFilteredProducts() {
        // Arrange (informamos todos os filtros válidos)
        String name = "Calabresa"; // nome válido
        String sku = "TEST2026"; // sku válido
        Double minPrice = 10.00; // preço minimo válido
        Double maxPrice = 60.00; // preço máximo válido
        Integer page = 0; // page válida
        Integer linesPerPage = 10; // linhas por página válido
        String direction = "desc"; // direction válida
        String orderBy = "name"; // orderBy válido

        // Criamos um DTO que salvaremos a lista que será salva no GlobalPageDto
        ProductDto expectedProduct = new ProductDto();
        expectedProduct.setName("Calabresa Frimesa");
        expectedProduct.setSku(sku);
        expectedProduct.setPrice(43.90);
        expectedProduct.setExpiration(LocalDate.of(2030, 7, 6));

        // Criamos a lista que "supostamente" armazenará os filtrados pelo banco e que será salva ao GlobalPageDto
        List<ProductDto> productDtoList = new ArrayList<>();
        productDtoList.add(expectedProduct); // salvamos o produto na lista

        // Obtemos o total de produtos que foram encontrados na lista
        Number total = productDtoList.size();


        // Criamos um GlobalPageDto para armazenar os dados que "supostamente" foram retornados pelo banco
        GlobalPageDto<ProductDto> expectedProductPageDto = new GlobalPageDto<>();
        expectedProductPageDto.setItems(productDtoList); // Salvamos a lista no Global dto
        expectedProductPageDto.setTotal(total.longValue()); // Salvamos o total de produtos que foram encontrados e convertemos para longValue

        // Simulamos que ao buscar pelos filtros informados encontramos o DTO esperado
        when(productRepository.findFilteredProducts(name, sku, minPrice, maxPrice,
                page, linesPerPage, direction, orderBy)).thenReturn(expectedProductPageDto);

        // Executamos o método que queremos testar
        GlobalPageDto<ProductDto> returnedProductPageDto = productService.findFilteredProducts(name, sku, minPrice, maxPrice,
                page, linesPerPage, direction, orderBy);

        // Verificamos se todos os dados retornados estão de acordo com os dados esperados
        assertEquals(returnedProductPageDto.getItems().get(0).getName(), expectedProductPageDto.getItems().get(0).getName());
        assertEquals(returnedProductPageDto.getItems().get(0).getPrice(), expectedProductPageDto.getItems().get(0).getPrice());
        assertEquals(returnedProductPageDto.getItems().get(0).getSku(), expectedProductPageDto.getItems().get(0).getSku());
        assertEquals(returnedProductPageDto.getItems().get(0).getExpiration(), expectedProductPageDto.getItems().get(0).getExpiration());
        assertEquals(returnedProductPageDto.getTotal(), expectedProductPageDto.getTotal());

        // Verificamos que o método do repository foi usado (porque tudo foi informado corretamente)
        verify(productRepository).findFilteredProducts(name, sku, minPrice, maxPrice,
                page, linesPerPage, direction, orderBy);
    }

    @Test
    @DisplayName("Should apply default direction when given direction is not in allowed list to find filtered products.")
    void shouldApplyDefaultDirectionWhenGivenDirectionIsNotInAllowedListToFindFilteredProducts() {
        // Informamos uma direction inválida
        Integer page = 0; // page válida
        Integer linesPerPage = 10; // linhas por página válido
        String direction = "alternada"; // direction inválida
        String orderBy = "name"; // orderBy válido

        // Simulamos que no repository a direction muda para "asc"
        when(productRepository.findFilteredProducts(any(), any(), any(), any(), eq(page),
                eq(linesPerPage), eq("asc"), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com a direction inválida
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a direction que foi usada foi "asc" mesmo tendo informado uma direction inválida
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(), eq(page),
                eq(linesPerPage), eq("asc"), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default direction when given direction is null to find filtered products.")
    void shouldApplyDefaultDirectionWhenGivenDirectionIsNullToFindFilteredProducts() {
        // Informamos uma direction null
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = null;
        String orderBy = "name";

        // Simulamos que no repository a direction muda para "asc" mesmo tendo sido enviada como null
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        //Executamos o método que queremos testar com a direction null
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a direction usada foi "asc" mesmo tendo sido informada uma null no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default orderBy when given orderBy is not in allowed list to find filtered products.")
    void shouldApplyDefaultOrderByWhenGivenOrderByIsNotInAllowedListToFindFilteredProducts() {
        // informamos um orderBy que não está na lista ALLOWED
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "desc";
        String orderBy = "tamanho";

        // Simulamos que o método do repository recebe o orderBy "name" mesmo tendo sido enviado como "tamanho" no service
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com orderBy inválido
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a orderBy usada foi "name" mesmo tendo sido informada como "tamanho" no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"));
    }

    @Test
    @DisplayName("Should apply default orderBy when given orderBy is null to find filtered products.")
    void shouldApplyDefaultOrderByWhenGivenOrderByIsNullToFindFilteredProducts() {
        // informamos um orderBy null
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "desc";
        String orderBy = null;

        // Simulamos que o método do repository recebe o orderBy "name" mesmo tendo sido enviado como null
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com orderBy null
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a orderBy usada foi "name" mesmo tendo sido informada como null no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"));
    }

    @Test
    @DisplayName("Should apply default number page when given negative number page to find filtered products.")
    void shouldApplyDefaultNumberPageWhenGivenNegativeNumberPageToFindFilteredProducts() {
        // Informamos um número negativo em page
        Integer page = -1;
        Integer linesPerPage = 10;
        String direction = "desc";
        String orderBy = "name";

        // Simulamos que o método do repository recebe o page "0" mesmo tendo sido informado como "-1"
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com page negativa
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a page usada foi "0" mesmo tendo sido informada como "-1" no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default number page when page is null to find filtered products.")
    void shouldApplyDefaultNumberPageWhenPageIsNullToFindFilteredProducts() {
        // Informamos page como null
        Integer page = null;
        Integer linesPerPage = 10;
        String direction = "desc";
        String orderBy = "name";

        // Simulamos que o método do repository recebe o page "0" mesmo tendo sido informado como null
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com page null
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository a page usada foi "0" mesmo tendo sido informada como null no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default number of linesPerPage when given negative number of linesPerPage to find filtered products.")
    void shouldApplyDefaultNumberOfLinesPerPageWhenGivenNegativeNumberOfLinesPerPageToFindFilteredProducts() {
        // Informamos um número negativo em linesPerPage
        Integer page = 0;
        Integer linesPerPage = -1;
        String direction = "desc";
        String orderBy = "name";

        // Simulamos que o método do repository recebe o linesPerPage "10" mesmo tendo sido informado como "-1"
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com linesPerPage negativo
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository o linesPerPage usado foi "0" mesmo tendo sido informado como "-1" no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default number of linesPerPage when linesPerPage is null to find filtered products.")
    void shouldApplyDefaultNumberOfLinesPerPageWhenLinesPerPageIsNullToFindFilteredProducts() {
        // Informamos linesPerPage como null
        Integer page = 0;
        Integer linesPerPage = null;
        String direction = "desc";
        String orderBy = "name";

        // Simulamos que o método do repository recebe o linesPerPage "10" mesmo tendo sido informado como null
        when(productRepository.findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy))).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com linesPerPage null
        productService.findFilteredProducts(null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Verificamos que no método do repository o linesPerPage usado foi "0" mesmo tendo sido informado como null no service
        // Isso porque o service converteu para a direction default
        verify(productRepository).findFilteredProducts(any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default null value when name contains blank spaces to find filtered products.")
    void shouldApplyDefaultNullValueWhenNameContainsBlankSpacesToFindFilteredProducts() {
        // Informamos um nome com espaços em branco
        String name = "     ";

        // Simulamos que no repository mesmo tendo sido enviado um nome com espaços em branco ele converte para null
        when(productRepository.findFilteredProducts(eq(null), any(), any(), any(),
                any(), any(), any(), any())).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com o nome que contém apenas espaços em branco
        productService.findFilteredProducts(name, null, null, null,
                null, null, null, null);

        // Verificamos que no método do repository o nome usado foi null mesmo que tendo sido preenchido com espaços em branco no service
        // Isso porque o service converteu para null.
        verify(productRepository).findFilteredProducts(eq(null), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should apply default null value when SKU contains blank spaces to find filtered products.")
    void shouldApplyDefaultNullValueWhenSkuContainsBlankSpacesToFindFilteredProducts() {
        // Informamos um sku com espaços em branco
        String sku = "     ";

        // Simulamos que no repository mesmo tendo sido enviado um sku com espaços em branco ele converte para null
        when(productRepository.findFilteredProducts(any(), eq(null), any(), any(),
                any(), any(), any(), any())).thenReturn(new GlobalPageDto<>());

        // Executamos o método que queremos testar com o sku que contém apenas espaços em branco
        productService.findFilteredProducts(null, sku, null, null,
                null, null, null, null);

        // Verificamos que no método do repository o sku usado foi null mesmo que tendo sido preenchido com espaços em branco no service
        // Isso porque o service converteu para null.
        verify(productRepository).findFilteredProducts(any(), eq(null), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU does not contain 8 characters to find filtered products.")
    void shouldThrowExceptionWhenSkuDoesNotContain8CharactersToFindFilteredProducts() {
        // Informamos um SKU que não contém exatamente 8 caracteres
        String sku = "1DJK4HI";

        // Afirmamos uma exceção pois o SKU não contém 8 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           productService.findFilteredProducts(null, sku, null, null,
                   null, null, null, null);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ter 8 caractéres.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findFilteredProducts(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when SKU is not alphanumeric to find filtered products.")
    void shouldThrowExceptionWhenSkuIsNotAlphanumericToFindFilteredProducts() {
        // Informamos um SKU que não é alfanumérico
        String sku = "?$!@%¨¨$";

        // Afirmamos uma exceção pois o SKU não é alfanumérico
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findFilteredProducts(null, sku, null, null,
                    null, null, null, null);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O SKU do produto deve ser alfanumérico.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findFilteredProducts(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the minimum price is less than or equal 0 to find filtered products.")
    void shouldThrowExceptionWhenTheMinimumPriceIsLessThanOrEqual0ToFindFilteredProducts() {
        // Informamos um preço minimo menor ou igual a zero
        Double minPrice = 0.0;

        // Afirmamos uma exceção pois o preço minimo é menor ou igual a zero
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findFilteredProducts(null, null, minPrice, null,
                    null, null, null, null);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço minimo do produto não pode ser menor ou igual a 0.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findFilteredProducts(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the maximum price is less than or equal 0 to find filtered products.")
    void shouldThrowExceptionWhenTheMaximumPriceIsLessThanOrEqual0ToFindFilteredProducts() {
        // Informamos um preço máximo menor ou igual a zero
        Double maxPrice = -7.0;

        // Afirmamos uma exceção pois o preço máximo é menor ou igual a zero
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findFilteredProducts(null, null, null, maxPrice,
                    null, null, null, null);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço máximo do produto não pode ser menor ou igual a 0.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findFilteredProducts(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the minimum price is greater than maximum price to find filtered products.")
    void shouldThrowExceptionWhenTheMinimumPriceIsGreaterThanMaximumPriceToFindFilteredProducts() {
        // Informamos um preço minimo maior que o preço máximo
        Double minPrice = 20.0;
        Double maxPrice = 9.0;

        // Afirmamos uma exceção pois o preço minimo é maior que o preço maximo
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productService.findFilteredProducts(null, null, minPrice, maxPrice,
                    null, null, null, null);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O preço minimo não pode ser maior que o preço maximo.", exception.getMessage());

        // Verificamos se o método do repository não foi usado (porque falhou antes)
        verify(productRepository, never()).findFilteredProducts(any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should successfully return all products with pagination and sorting.")
    void shouldSuccessfullyReturnAllProductsWithPaginationAndSorting() {
        // Arrange – definimos os parâmetros de paginação e ordenação válidos
        Integer page = 0;
        Integer linesPerPage = 5;
        String direction = "desc";
        String orderBy = "price";

        // Criamos um DTO que simula um produto retornado do banco
        ProductDto expectedProduct = new ProductDto();
        expectedProduct.setName("Queijo Mussarela");
        expectedProduct.setSku("QJMO7890");
        expectedProduct.setPrice(49.90);
        expectedProduct.setExpiration(LocalDate.of(2032, 8, 10));

        // Criamos a lista de produtos retornada do banco
        List<ProductDto> productList = new ArrayList<>();
        productList.add(expectedProduct);

        // Definimos o total simulado
        Number total = productList.size();

        // Criamos o DTO de resposta com lista e total
        GlobalPageDto<ProductDto> expectedPageDto = new GlobalPageDto<>();
        expectedPageDto.setItems(productList);
        expectedPageDto.setTotal(total.longValue());

        // Simulamos o retorno do repository
        when(productRepository.findAllProducts(page, linesPerPage, direction, orderBy))
                .thenReturn(productList);
        when(productRepository.countAllProducts())
                .thenReturn(total.longValue());

        // Act – executamos o método que queremos testar
        GlobalPageDto<ProductDto> returnedPageDto = productService.findAllProducts(page, linesPerPage, direction, orderBy);

        // Assert – verificamos se os dados retornados batem com os esperados
        assertEquals(productList.get(0).getName(), returnedPageDto.getItems().get(0).getName());
        assertEquals(productList.get(0).getSku(), returnedPageDto.getItems().get(0).getSku());
        assertEquals(productList.get(0).getPrice(), returnedPageDto.getItems().get(0).getPrice());
        assertEquals(productList.get(0).getExpiration(), returnedPageDto.getItems().get(0).getExpiration());
        assertEquals(total.longValue(), returnedPageDto.getTotal());

        // Verificamos que o método do repository foi chamado corretamente
        verify(productRepository).findAllProducts(page, linesPerPage, direction, orderBy);
    }

}
