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

}
