package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ProductDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Array;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    private ProductRepository productRepository;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    // Método saveProduct
    @Test
    @DisplayName("Should save product successfully when insert query executes without errors")
    void shouldSaveProductSuccessfully() {
        // Criamos um objeto ProductDto com dados válidos para simular um novo produto
        ProductDto productDto = new ProductDto();
        productDto.setName("Camiseta Polo");
        productDto.setSku("AB12CD34"); // SKU com 8 caracteres alfanuméricos
        productDto.setPrice(79.99);
        productDto.setExpiration(LocalDate.of(2026, 12, 31));

        // Simulamos o comportamento do EntityManager ao criar uma query nativa
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos o comportamento da query ao setar os parâmetros
        when(query.setParameter(eq("id"), anyString())).thenReturn(query);
        when(query.setParameter(eq("name"), eq("Camiseta Polo"))).thenReturn(query);
        when(query.setParameter(eq("sku"), eq("AB12CD34"))).thenReturn(query);
        when(query.setParameter(eq("price"), eq(79.99))).thenReturn(query);
        when(query.setParameter(eq("expiration"), eq(LocalDate.of(2026, 12, 31)))).thenReturn(query);

        // Executamos o método que queremos testar
        productRepository.saveProduct(productDto);

        // Verificamos se a query de INSERT foi criada corretamente
        verify(em).createNativeQuery(contains("INSERT INTO tb_products"));

        // Verificamos se a query foi de fato executada
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when saving product fails")
    void shouldThrowClientErrorExceptionWhenSaveFails() {
        // Criamos um produto de teste com valores válidos
        ProductDto productDto = new ProductDto();
        productDto.setName("Produto Teste");
        productDto.setSku("ZXCVBN12"); // SKU válido com 8 caracteres alfanuméricos
        productDto.setPrice(45.50);
        productDto.setExpiration(LocalDate.now().plusYears(1));

        // Simulamos uma exceção sendo lançada ao criar a query no EntityManager
        when(em.createNativeQuery(anyString())).thenThrow(RuntimeException.class);

        // Verificamos se o método lança a exceção personalizada esperada
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->
                productRepository.saveProduct(productDto)
        );

        // Verificamos se a mensagem da exceção está correta
        assertEquals("Erro ao cadastrar um novo produto. ", exception.getMessage());
    }

    // Método existsProductBySku
    @Test
    @DisplayName("Should return true when a product with the given SKU exists")
    void shouldReturnTrueWhenProductWithSkuExists() {
        // Simulamos o SKU que será buscado no banco
        String sku = "AB12CD34"; // SKU com 8 caracteres alfanuméricos

        // Criamos um ArrayList que será retornado pela query
        List<Object> resultList = new ArrayList<>();
        resultList.add(1L);

        // Simulamos a criação da query nativa com EntityManager
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos o retorno da contagem com uma lista com 1 produto (produto existe)
        when(query.setParameter("sku", sku)).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);

        // Executamos o método que queremos testar
        boolean exists = productRepository.existsProductBySku(sku);

        // Verificamos se a resposta é verdadeira, como esperado
        assertTrue(exists);

        // Verificamos se a query de SELECT COUNT(*) foi executada
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_products"));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when a product with the given SKU does not exist")
    void shouldReturnFalseWhenProductWithSkuDoesNotExist() {
        // Simulamos um SKU que não está presente no banco
        String sku = "ZZZZ1234"; // Ainda dentro do padrão alfanumérico e com 8 caracteres

        // Simulamos o comportamento do EntityManager e da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("sku", sku)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Executamos o método que queremos testar
        boolean exists = productRepository.existsProductBySku(sku);

        // Verificamos se a resposta é falsa, como esperado
        assertFalse(exists);

        // Verificamos se a query foi montada e executada corretamente
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_products"));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while checking product existence by SKU")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursWhileCheckingProductExistenceBySku() {
        // SKU simulado para a busca
        String sku = "AB12CD34";

        // Simulamos uma exceção inesperada ao criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Executamos o método e verificamos se a exceção ClientErrorException é lançada
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.existsProductBySku(sku);
        });

        // Verificamos se a mensagem da exceção é a esperada
        assertEquals("Erro ao verificar existência do produto pelo SKU.", exception.getMessage());

        // Verificamos se o EntityManager tentou criar a query antes de falhar
        verify(em).createNativeQuery(anyString());
    }


    // Método existsProductById
    @Test
    @DisplayName("Should return true when a product with the given ID exists")
    void shouldReturnTrueWhenProductWithIdExists() {
        // Simulamos um ID que será buscado no banco
        UUID id = UUID.randomUUID();

        // Criamos uma lista simulando que a consulta encontrou um produto
        List<Object> resultList = new ArrayList<>();
        resultList.add(1L); // Pode ser qualquer valor, pois só testamos se a lista está vazia ou não

        // Simulamos o comportamento do EntityManager e da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", id.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(resultList);

        // Executamos o método que queremos testar
        boolean exists = productRepository.existsProductById(id);

        // Verificamos se o retorno é verdadeiro, indicando que o produto foi encontrado
        assertTrue(exists);

        // Verificamos se a query foi construída e executada corretamente
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_products"));
        verify(query).setParameter("id", id.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when a product with the given ID does not exist")
    void shouldReturnFalseWhenProductWithIdDoesNotExist() {
        // Simulamos um ID que não existe no banco
        UUID id = UUID.randomUUID();

        // Simulamos o comportamento do EntityManager e da query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("id", id.toString())).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>()); // Lista vazia = não encontrou

        // Executamos o método que queremos testar
        boolean exists = productRepository.existsProductById(id);

        // Verificamos se o retorno é falso, indicando que o produto não foi encontrado
        assertFalse(exists);

        // Verificamos se a query foi montada e executada corretamente
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_products"));
        verify(query).setParameter("id", id.toString());
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while checking product existence by ID")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursWhileCheckingProductExistenceById() {
        // UUID simulado para a busca
        UUID productId = UUID.randomUUID();

        // Simulamos uma exceção inesperada ao criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Executamos o método e verificamos se a exceção ClientErrorException é lançada
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.existsProductById(productId);
        });

        // Verificamos se a mensagem da exceção é a esperada
        assertEquals("Erro ao verificar existência do produto pelo id.", exception.getMessage());

        // Verificamos se o EntityManager tentou criar a query antes de falhar
        verify(em).createNativeQuery(anyString());
    }


    // Método findAllProducts
    @Test
    @DisplayName("Should return list of ProductDto with pagination and sorting when data is available")
    void ShouldReturnListOfProductDtoWhenDataIsAvailable() {
        // Simulamos os parâmetros de paginação e ordenação fornecidos pelo usuário
        Integer page = 0;
        Integer linesPerPage = 2;
        String direction = "ASC";
        String orderBy = "name";

        // Simulamos o resultado retornado pelo banco de dados (lista de Object[])
        Object[] row1 = {"Produto A", "SKU00001", 10.0, Date.valueOf("2025-12-31")};
        Object[] row2 = {"Produto B", "SKU00002", 20.0, Date.valueOf("2025-11-30")};
        List<Object[]> mockResultList = List.of(row1, row2);

        // Criamos um mock da query e configuramos o comportamento do EntityManager
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResultList);

        // Executamos o método real do repositório
        List<ProductDto> returnedProducts = productRepository.findAllProducts(page, linesPerPage, direction, orderBy);

        // Verificamos se os resultados foram corretamente convertidos e retornados
        assertEquals(2, returnedProducts.size());

        assertEquals("Produto A", returnedProducts.get(0).getName());
        assertEquals("SKU00001", returnedProducts.get(0).getSku());
        assertEquals(10.0, returnedProducts.get(0).getPrice());
        assertEquals(LocalDate.of(2025, 12, 31), returnedProducts.get(0).getExpiration());

        assertEquals("Produto B", returnedProducts.get(1).getName());
        assertEquals("SKU00002", returnedProducts.get(1).getSku());
        assertEquals(20.0, returnedProducts.get(1).getPrice());
        assertEquals(LocalDate.of(2025, 11, 30), returnedProducts.get(1).getExpiration());

        // Verificar se todos os métodos foram executados conforme o esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).setParameter(eq("offset"), eq(page * linesPerPage));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return empty list when no products are found")
    void shouldReturnEmptyListWhenNoProductsFound() {
        // Informamos parâmetros válidos
        Integer page = 0;
        Integer linesPerPage = 2;
        String direction = "ASC";
        String orderBy = "name";

        // Simulamos criar uma query para buscar todos os clientes
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Executamos o método que queremos testar
        List<ProductDto> returnedProducts = productRepository.findAllProducts(page, linesPerPage, direction, orderBy);

        // Afirmamos que a lista é empty
        assertTrue(returnedProducts.isEmpty());

        // Verificamos se todos os passos foram seguidos conforme o esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).setParameter(eq("offset"), eq(page * linesPerPage));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs in findAllProducts")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccurs() {
        // Definimos os parâmetros de entrada
        Integer page = 0;
        Integer linesPerPage = 2;
        String direction = "ASC";
        String orderBy = "name";

        // Simulamos o EntityManager retornando a query mockada
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos que ao configurar o parâmetro "limit" lança uma exceção RuntimeException
        when(query.setParameter(eq("limit"), eq(linesPerPage))).thenReturn(query);
        when(query.setParameter(eq("offset"), eq(page * linesPerPage))).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Erro inesperado no banco"));

        // Verificamos se a exceção ClientErrorException é lançada ao chamar o método
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.findAllProducts(page, linesPerPage, direction, orderBy);
        });

        // Verificamos se a mensagem da exceção é a esperada
        assertEquals("Erro ao buscar todos produtos.", exception.getMessage());

        // Confirmamos que o EntityManager e a Query foram invocados conforme esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("limit"), eq(linesPerPage));
        verify(query).setParameter(eq("offset"), eq(page * linesPerPage));
        verify(query).getResultList();
    }

    // Método findProductModelById
    @Test
    @DisplayName("Should return ProductModel when product with given ID exists")
    void shouldReturnProductModelWhenProductWithIdExists() {
        // Criamos um UUID fixo para simular a busca pelo produto
        UUID productId = UUID.randomUUID();

        // Simulamos a linha retornada pelo banco (Object[]), com dados do produto
        Object[] row = {
                productId.toString(),
                "Produto Teste",
                "SKU12345",
                99.99,
                Date.valueOf("2025-12-31")
        };

        // Criamos uma lista com o resultado esperado contendo a linha simulada
        List<Object[]> resultList = new ArrayList<>();
        resultList.add(row);

        // Mockamos o EntityManager para retornar a query mockada
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Mockamos o setParameter da query para retornar a própria query para encadeamento
        when(query.setParameter(eq("id"), eq(productId.toString()))).thenReturn(query);

        // Mockamos o resultado da query para retornar nossa lista simulada
        when(query.getResultList()).thenReturn(resultList);

        // Executamos o método real do repository
        ProductModel returnedProduct = productRepository.findProductModelById(productId);

        // Verificamos se o produto retornado tem os valores corretamente convertidos
        assertEquals(productId, returnedProduct.getId());
        assertEquals("Produto Teste", returnedProduct.getName());
        assertEquals("SKU12345", returnedProduct.getSku());
        assertEquals(99.99, returnedProduct.getPrice());
        assertEquals(LocalDate.of(2025, 12, 31), returnedProduct.getExpiration());

        // Verificamos se os métodos do EntityManager e Query foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("id"), eq(productId.toString()));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when no product found with given ID")
    void shouldThrowExceptionWhenNoProductFoundWithId() {
        // Criamos um UUID fixo para simular a busca pelo produto
        UUID productId = UUID.randomUUID();

        // Simulamos a query que retorna uma lista vazia, indicando que o produto não existe
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(productId.toString()))).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Verificamos se a exceção ClientErrorException é lançada com a mensagem correta
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.findProductModelById(productId);
        });

        assertEquals("Produto não encontrado pelo id: " + productId, exception.getMessage());

        // Verificamos se os métodos foram chamados conforme o esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("id"), eq(productId.toString()));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException on unexpected error")
    void shouldThrowExceptionOnUnexpectedError() {
        // Criamos um UUID fixo para simular a busca pelo produto
        UUID productId = UUID.randomUUID();

        // Simulamos uma exceção inesperada ao executar a query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(productId.toString()))).thenReturn(query);
        when(query.getResultList()).thenThrow(new RuntimeException("Erro inesperado"));

        // Verificamos se a exceção ClientErrorException é lançada quando ocorre erro inesperado
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.findProductModelById(productId);
        });

        assertEquals("Erro ao buscar produto pelo id.", exception.getMessage());

        // Verificamos se os métodos foram chamados conforme o esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("id"), eq(productId.toString()));
        verify(query).getResultList();
    }

    // Método findProductBySku
    @Test
    @DisplayName("Should return ProductDto when product is found with given SKU")
    void shouldReturnProductDtoWhenProductFoundWithSku() {
        // Simulamos um SKU existente
        String sku = "SKU12345";

        // Criamos valores simulados para o produto encontrado
        UUID productId = UUID.randomUUID();
        String name = "Produto Teste";
        Double price = 99.90;
        LocalDate expiration = LocalDate.of(2025, 12, 31);

        Object[] resultRow = {
                productId.toString(),
                name,
                sku,
                price,
                java.sql.Date.valueOf(expiration)
        };

        List<Object[]> simulatedResultList = new ArrayList<>();
        simulatedResultList.add(resultRow);

        // Simulamos o comportamento da query com os dados acima
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("sku"), eq(sku))).thenReturn(query);
        when(query.getResultList()).thenReturn(simulatedResultList);

        // Executamos o método que queremos testar
        ProductDto returnedProduct = productRepository.findProductBySku(sku);

        // Verificamos se os dados retornados são exatamente os que simulamos
        assertEquals(productId, returnedProduct.getId());
        assertEquals(name, returnedProduct.getName());
        assertEquals(sku, returnedProduct.getSku());
        assertEquals(price, returnedProduct.getPrice());
        assertEquals(expiration, returnedProduct.getExpiration());

        // Verificamos se os métodos da query foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("sku"), eq(sku));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when no product found with given SKU")
    void shouldThrowExceptionWhenNoProductFoundWithSku() {
        // Simulamos um SKU inexistente
        String sku = "SKU12345";

        // Simulamos o comportamento do EntityManager retornando uma query mockada
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("sku"), eq(sku))).thenReturn(query);

        // Simulamos que o resultado da query é uma lista vazia
        when(query.getResultList()).thenReturn(new ArrayList<>());

        // Verificamos se a exceção é lançada com a mensagem esperada
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            productRepository.findProductBySku(sku);
        });

        assertEquals("Produto não encontrado pelo SKU.", exception.getMessage());

        // Verificamos se os métodos da query foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("sku"), eq(sku));
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while finding product by SKU")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnFindBySku() {
        // Simulamos um SKU qualquer
        String sku = "SKU12345";

        // Simulamos que createNativeQuery lança uma exceção inesperada (ex: NullPointerException)
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro simulado"));

        // Executamos o método e verificamos se a exceção esperada é lançada
        ClientErrorException exception = assertThrows(
                ClientErrorException.class,
                () -> productRepository.findProductBySku(sku)
        );

        // Verificamos se a mensagem da exceção é a esperada
        assertEquals("Erro ao buscar produto pelo SKU.", exception.getMessage());

        // Verificamos se o EntityManager tentou criar a query antes do erro
        verify(em).createNativeQuery(anyString());
    }

    // Método updateProductBySku
    @Test
    @DisplayName("Should return ProductDto when product is successfully updated by SKU")
    void shouldReturnProductDtoWhenProductSuccessfullyUpdatedBySku() {
        // Dados simulados para o SKU e o produto a ser atualizado
        String sku = "SKU12345";
        ProductDto productDto = new ProductDto();
        productDto.setName("Produto Atualizado");
        productDto.setPrice(99.99);
        productDto.setExpiration(LocalDate.of(2025, 12, 31));

        // Criamos um mock da query e configuramos o comportamento do EntityManager
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("name"), eq(productDto.getName()))).thenReturn(query);
        when(query.setParameter(eq("price"), eq(productDto.getPrice()))).thenReturn(query);
        when(query.setParameter(eq("expiration"), eq(productDto.getExpiration()))).thenReturn(query);
        when(query.setParameter(eq("sku"), eq(sku))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1); // Simula que 1 linha foi afetada pela atualização

        // Executamos o método real do repositório
        ProductDto returnedProduct = productRepository.updateProductBySku(sku, productDto);

        // Verificamos se o produto retornado é o mesmo que passamos como entrada
        assertEquals(productDto.getName(), returnedProduct.getName());
        assertEquals(productDto.getPrice(), returnedProduct.getPrice());
        assertEquals(productDto.getExpiration(), returnedProduct.getExpiration());

        // Verificamos se os métodos da query foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("name"), eq(productDto.getName()));
        verify(query).setParameter(eq("price"), eq(productDto.getPrice()));
        verify(query).setParameter(eq("expiration"), eq(productDto.getExpiration()));
        verify(query).setParameter(eq("sku"), eq(sku));
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while updating product by SKU")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnUpdateBySku() {
        // Dados simulados
        String sku = "SKU12345";
        ProductDto productDto = new ProductDto();
        productDto.setName("Produto Atualizado");
        productDto.setPrice(99.99);
        productDto.setExpiration(LocalDate.of(2025, 12, 31));

        // Simulamos erro genérico no createNativeQuery
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro simulado"));

        // Executamos o método e capturamos a exceção
        ClientErrorException exception = assertThrows(
                ClientErrorException.class,
                () -> productRepository.updateProductBySku(sku, productDto)
        );

        // Verificamos a mensagem da exceção
        assertEquals("Erro ao realizar atualização no produto pelo SKU.", exception.getMessage());

        // Verificamos se tentou criar a query
        verify(em).createNativeQuery(anyString());
    }

    // Método deleteProductBySku
    @Test
    @DisplayName("Should delete product by SKU successfully")
    void shouldDeleteProductBySkuSuccessfully() {
        // SKU simulado
        String sku = "12345678";

        // Configura o mock do EntityManager e Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("sku"), eq(sku))).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1); // Simula deleção de um produto

        // Executa o método real
        productRepository.deleteProductBySku(sku);

        // Verifica se os métodos foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter(eq("sku"), eq(sku));
        verify(query).executeUpdate();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs during product deletion by SKU")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnDeleteProductBySku() {
        // SKU simulado
        String sku = "12345678";

        // Simula que uma exceção será lançada ao tentar criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Executa o método e verifica se a exceção é lançada com a mensagem correta
        ClientErrorException thrown = assertThrows(ClientErrorException.class, () -> {
            productRepository.deleteProductBySku(sku);
        });

        assertEquals("Erro ao realizar deleção do produto pela SKU.", thrown.getMessage());

        // Verifica se o EntityManager tentou criar a query
        verify(em).createNativeQuery(anyString());
    }

    // Método countAllProducts
    @Test
    @DisplayName("Should count all products successfully")
    void shouldCountAllProductsSuccessfully() {
        // Simula o valor retornado pelo banco de dados
        Long expectedCount = 42L;

        // Configura o mock do EntityManager e da Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expectedCount);

        // Executa o método real
        Long result = productRepository.countAllProducts();

        // Verifica se o valor retornado é igual ao simulado
        assertEquals(expectedCount, result);

        // Verifica se os métodos do EntityManager e da Query foram chamados corretamente
        verify(em).createNativeQuery(anyString());
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while counting all products")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnCountAllProducts() {
        // Simula que uma exceção será lançada ao tentar criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Executa o método e verifica se a exceção esperada é lançada
        ClientErrorException thrown = assertThrows(ClientErrorException.class, () -> {
            productRepository.countAllProducts();
        });

        // Verifica se a mensagem da exceção está correta
        assertEquals("Erro ao contar todos produtos.", thrown.getMessage());

        // Verifica se o EntityManager tentou criar a query
        verify(em).createNativeQuery(anyString());
    }


    // Método queryFindFilteredProducts
    @Test
    @DisplayName("Should return filtered products successfully")
    void shouldReturnFilteredProductsSuccessfully() {
        // Arrange - parâmetros simulados
        String name = "Camiseta";
        String sku = "SKU12333";
        Double minPrice = 50.0;
        Double maxPrice = 100.0;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "ASC";
        String orderBy = "name";

        // Mock do resultado da query
        List<Object[]> mockResultList = List.of(
                new Object[]{"Camiseta Polo", "SKU12333", 79.9, java.sql.Date.valueOf("2025-12-01")},
                new Object[]{"Camiseta Básica", "SKU12333", 59.9, java.sql.Date.valueOf("2025-11-01")}
        );

        // Mock do comportamento do EntityManager e Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("limit", linesPerPage)).thenReturn(query);
        when(query.setParameter("offset", page * linesPerPage)).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResultList);

        // Act - chamamos o método real do repositório
        List<ProductDto> result = ReflectionTestUtils.invokeMethod(
                productRepository,
                "queryFindFilteredProducts",
                name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy
        );

        // Assert - verificamos o conteúdo da lista retornada
        assertEquals(2, result.size());

        assertEquals("Camiseta Polo", result.get(0).getName());
        assertEquals("SKU12333", result.get(0).getSku());
        assertEquals(79.9, result.get(0).getPrice());
        assertEquals(LocalDate.of(2025, 12, 1), result.get(0).getExpiration());

        assertEquals("Camiseta Básica", result.get(1).getName());
        assertEquals("SKU12333", result.get(1).getSku());
        assertEquals(59.9, result.get(1).getPrice());
        assertEquals(LocalDate.of(2025, 11, 1), result.get(1).getExpiration());

        // Verifica interações com mocks
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("limit", linesPerPage);
        verify(query).setParameter("offset", page * linesPerPage);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return filtered products successfully when all filter parameters are null")
    void shouldReturnFilteredProductsSuccessfullyWhenAllFiltersAreNull() {
        // Arrange - todos os parâmetros de filtro como null
        String name = null;
        String sku = null;
        Double minPrice = null;
        Double maxPrice = null;
        Integer page = 0;
        Integer linesPerPage = 2;
        String direction = "ASC";
        String orderBy = "name";

        // Mock do resultado da query (2 produtos fictícios)
        List<Object[]> mockResultList = List.of(
                new Object[]{"Produto A", "SKU00001", 100.0, java.sql.Date.valueOf("2025-12-31")},
                new Object[]{"Produto B", "SKU00002", 50.0, java.sql.Date.valueOf("2025-11-30")}
        );

        // Mock do comportamento do EntityManager e Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("limit", linesPerPage)).thenReturn(query);
        when(query.setParameter("offset", page * linesPerPage)).thenReturn(query);
        when(query.getResultList()).thenReturn(mockResultList);

        // Act - chamamos o método real protegido via ReflectionTestUtils
        List<ProductDto> result = ReflectionTestUtils.invokeMethod(
                productRepository,
                "queryFindFilteredProducts",
                name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy
        );

        // Assert - verificamos o conteúdo da lista retornada
        assertEquals(2, result.size());

        assertEquals("Produto A", result.get(0).getName());
        assertEquals("SKU00001", result.get(0).getSku());
        assertEquals(100.0, result.get(0).getPrice());
        assertEquals(LocalDate.of(2025, 12, 31), result.get(0).getExpiration());

        assertEquals("Produto B", result.get(1).getName());
        assertEquals("SKU00002", result.get(1).getSku());
        assertEquals(50.0, result.get(1).getPrice());
        assertEquals(LocalDate.of(2025, 11, 30), result.get(1).getExpiration());

        // Verifica interações com mocks
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("limit", linesPerPage);
        verify(query).setParameter("offset", page * linesPerPage);
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should throw ClientErrorException when unexpected error occurs while filtering products")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursOnQueryFindFilteredProducts() {
        // Arrange - parâmetros simulados
        String name = "Camiseta";
        String sku = "SKU12333";
        Double minPrice = 50.0;
        Double maxPrice = 100.0;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "ASC";
        String orderBy = "name";

        // Simula erro inesperado ao criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Act + Assert - espera exceção ao executar método real
        ClientErrorException thrown = assertThrows(ClientErrorException.class, () ->
                ReflectionTestUtils.invokeMethod(
                        productRepository,
                        "queryFindFilteredProducts",
                        name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy
                )
        );

        assertEquals("Erro ao buscar produtos filtrados.", thrown.getMessage());

        // Verifica se tentou criar a query
        verify(em).createNativeQuery(anyString());
    }


    //Método queryCountFilteredProducts
    @Test
    @DisplayName("Should return the total number of filtered products.")
    void shouldReturnTheTotalNumberOfFilteredProducts() {
        // Informamos parâmetros válidos
        String name = "Mouse";
        String sku = "MOU45644";
        Double minPrice = 100.0;
        Double maxPrice = 200.0;

        // Variável object que será o resultado da query
        Object result = 5;
        Number expectedResult = (Number) result;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter("name", "%" + name + "%")).thenReturn(query);
        when(query.setParameter("sku", sku)).thenReturn(query);
        when(query.setParameter("minPrice", minPrice)).thenReturn(query);
        when(query.setParameter("maxPrice", maxPrice)).thenReturn(query);
        when(query.getSingleResult()).thenReturn(expectedResult);

        // Executamos o método que queremos testar
        Long returnedResult = productRepository.queryCountFilteredProducts(name, sku, minPrice, maxPrice);

        // Verificamos se o resultado retornado é igual ao esperado
        assertEquals(expectedResult.longValue(), returnedResult);

        // Verificamos se todos os passos foram seguidos como o esperado
        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("name", "%" + name + "%");
        verify(query).setParameter("sku", sku);
        verify(query).setParameter("minPrice", minPrice);
        verify(query).setParameter("maxPrice", maxPrice);
        verify(query).getSingleResult();
    }

    @Test
    @DisplayName("Should return total count of filtered products when all filters are null")
    void shouldReturnTotalCountWhenAllFiltersAreNull() {
        // Arrange - todos os parâmetros de filtro nulos
        String name = null;
        String sku = null;
        Double minPrice = null;
        Double maxPrice = null;

        // Mock do resultado da query, simulando total 5
        Object countResult = 5;
        Number expectedTotal = (Number) countResult;

        // Mock do comportamento do EntityManager e Query
        when(em.createNativeQuery(anyString())).thenReturn(query);
        // Como não há parâmetros de filtro, setQueryParameters não deve setar nada
        // Porém como é método interno, não precisamos mockar aqui (supondo void)

        when(query.getSingleResult()).thenReturn(expectedTotal);

        // Act - chamamos o método protegido via ReflectionTestUtils
        Long result = ReflectionTestUtils.invokeMethod(
                productRepository,
                "queryCountFilteredProducts",
                name, sku, minPrice, maxPrice
        );

        // Assert - verifica se retornou o valor esperado
        assertEquals(expectedTotal.longValue(), result);

        // Verifica interações com mocks
        verify(em).createNativeQuery(anyString());
        verify(query).getSingleResult();
    }


    @Test
    @DisplayName("Should throw ClientErrorException when an unexpected error occurs during filtered product count.")
    void shouldThrowClientErrorExceptionWhenUnexpectedErrorOccursDuringFilteredProductCount() {
        // Informamos parâmetros válidos
        String name = "Produto";
        String sku = "ABC12334";
        Double minPrice = 50.0;
        Double maxPrice = 200.0;

        // Simulamos erro inesperado ao tentar criar a query
        when(em.createNativeQuery(anyString())).thenThrow(new RuntimeException("Erro inesperado"));

        // Executamos o método e verificamos se a exceção esperada é lançada
        ClientErrorException thrownException = assertThrows(ClientErrorException.class, () ->
                productRepository.queryCountFilteredProducts(name, sku, minPrice, maxPrice)
        );

        // Verificamos se a mensagem da exceção é a esperada
        assertEquals("Erro ao contar produtos filtrados.", thrownException.getMessage());

        // Verificamos se o EntityManager tentou criar a query
        verify(em).createNativeQuery(anyString());
    }

    // Método findFilteredProducts
    @Test
    @DisplayName("Should return GlobalPageDto with filtered products and total count")
    void shouldReturnGlobalPageDtoWithFilteredProductsAndTotalCount() {
        // Arrange - parâmetros simulados
        String name = "Camiseta";
        String sku = "SKU12345";
        Double minPrice = 50.0;
        Double maxPrice = 150.0;
        Integer page = 0;
        Integer linesPerPage = 10;
        String direction = "ASC";
        String orderBy = "name";

        // Simulamos lista de produtos filtrados
        List<ProductDto> filteredProducts = new ArrayList<>();

        ProductDto productDto1 = new ProductDto();
        productDto1.setName("Camiseta Polo");
        productDto1.setSku("SKU12345");
        productDto1.setPrice(79.9);
        productDto1.setExpiration(LocalDate.of(2025, 12, 1));

        ProductDto productDto2 = new ProductDto();
        productDto1.setName("Camiseta Básica");
        productDto1.setSku("SKU12347");
        productDto1.setPrice(59.9);
        productDto1.setExpiration(LocalDate.of(2025, 11, 1));

        filteredProducts.add(productDto1);
        filteredProducts.add(productDto2);

        // Simulamos total de itens filtrados
        Long totalFiltered = 2L;

        // Criamos spy para mockar métodos protegidos na mesma instância
        ProductRepository spyRepository = Mockito.spy(productRepository);

        // Mockamos métodos protegidos para retornar valores simulados
        doReturn(filteredProducts).when(spyRepository).queryFindFilteredProducts(
                eq(name), eq(sku), eq(minPrice), eq(maxPrice), eq(page), eq(linesPerPage), eq(direction), eq(orderBy));

        doReturn(totalFiltered).when(spyRepository).queryCountFilteredProducts(
                eq(name), eq(sku), eq(minPrice), eq(maxPrice));

        // Act - chamamos o método real via spy
        GlobalPageDto<ProductDto> result = spyRepository.findFilteredProducts(
                name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy);

        // Assert - verifica se o resultado está correto
        assertNotNull(result);
        assertEquals(totalFiltered, result.getTotal());
        assertEquals(filteredProducts, result.getItems());

        // Verifica se os métodos protegidos foram chamados
        verify(spyRepository).queryFindFilteredProducts(
                eq(name), eq(sku), eq(minPrice), eq(maxPrice), eq(page), eq(linesPerPage), eq(direction), eq(orderBy));
        verify(spyRepository).queryCountFilteredProducts(eq(name), eq(sku), eq(minPrice), eq(maxPrice));
    }



}