package com.rodrigopettenon.orderflow.repositories;

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
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        String sku = "SKU123";

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
        String sku = "NOTFOUND123";

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
        String sku = "SKU123";

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



}