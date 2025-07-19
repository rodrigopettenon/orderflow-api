package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.ClientDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
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
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(ClientRepositoryTest.class);
    @InjectMocks
    private ClientRepository clientRepository;

    @Mock
    private EntityManager em;

    @Mock
    private Query query;

    // Método saveClient
    @Test
    @DisplayName("Should successfully save a client to the database.")
    void shouldSuccessfullySaveAClientToTheDatabase() {
        // Arrange (Criamos um dto com dados válidos)
        ClientDto clientDto = new ClientDto();
        clientDto.setName("Bruce Wayne");
        clientDto.setEmail("brucewayne@gmail.com");
        clientDto.setCpf("40177715057");
        clientDto.setBirth(LocalDate.of(1972, 2, 19));

        // Mock: quando criar a query retornar nosso mock de Query
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Mock: quando setar qualquer parâmetro, retornar o próprio query (encadeamento de chamadas)
        when(query.setParameter(anyString(), any())).thenReturn(query);

        // Mock: quando executeUpdate for chamado, simular execução (pode retornar 1, por exemplo)
        when(query.executeUpdate()).thenReturn(1);

        // Act (chama o método que será testado)
        clientRepository.saveClient(clientDto);

        // Assert - verificações
        verify(em, times(1)).createNativeQuery(contains(("INSERT INTO tb_clients")));
        verify(query).setParameter("name", clientDto.getName());
        verify(query).setParameter("email", clientDto.getEmail());
        verify(query).setParameter("cpf", clientDto.getCpf());
        verify(query).setParameter("birth", clientDto.getBirth());

        verify(query).executeUpdate();
    }



    @Test
    @DisplayName("Should ThrowException when failing to save a client.")
    void shouldThrowExceptionWhenSavingClientFails() {
        // Arrange
        ClientDto clientDto = new ClientDto();
        clientDto.setName("Clark Kent");
        clientDto.setEmail("clarkkent@gmail.com");
        clientDto.setCpf("40177715057");
        clientDto.setBirth(LocalDate.of(1975, 6, 18));

        // Mock (Simula que o EntityManager retorna o mock da query
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Mock (Simula que ao chamar setParameter com "email", uma exceção é lançada
        doThrow(new RuntimeException("Falha interna"))
                .when(query).setParameter(anyString(), any());

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () ->{
           clientRepository.saveClient(clientDto);
        });

        // Verifica se amensagem da exceção corresponde
        assertEquals("Erro ao cadastrar cliente.", exception.getMessage());

        // Garante que executeUpdate() não foi chamado
        verify(query, never()).executeUpdate();
    }

    // Método existsClientById
    @Test
    @DisplayName("Should return true when client exists by ID.")
    void shouldReturnTrueWhenClientExistsById() {
        // Arrange (Informamos um ID válido e simulamos que o banco encontrou um registro com o ID)
        Long clientId = 1L;
        List<Object> mockedResult = Arrays.asList(1); // Simula que encontrou um registro no banco

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(clientId))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockedResult);

        // Act (executa o método que queremos testar)
        Boolean result = clientRepository.existsClientById(clientId);

        // Assert (verifica se o resultado está correto)
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when client does not exist by ID.")
    void shouldReturnFalseWhenClientNotExistById() {
        // Arrange (Informamos um ID válido e simulamos que o banco não encontrou registro com o ID)
        Long clientId = 1L;
        List<Object> mockedResult = Arrays.asList(); // Simula que não foi encontrado nenhum registro no banco

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(clientId))).thenReturn(query);
        when(query.getResultList()).thenReturn(mockedResult);

        // Act (executa o método que queremos testar)
        Boolean result = clientRepository.existsClientById(clientId);

        // Assert (verifica se o resultado está correto)
        assertFalse(result);
    }

    @Test
    @DisplayName("Should ThrowException when fails to check client existence by ID.")
    void shouldThrowExceptionWhenCheckingClientByIdFails() {
        // Arrange - informamos um ID válido
        Long clientId = 3L;

        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(eq("id"), eq(clientId)))
                .thenThrow(new RuntimeException("Simulated failure"));

        // Act & Assert
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientRepository.existsClientById(clientId);
        });

        assertEquals("Erro ao verificar existência do ID.", exception.getMessage());
    }

    // método existsClientByCpf
    @Test
    @DisplayName("Should return true when client exists by CPF.")
    void shouldReturnTrueWhenClientExistsByCpf() {
        // Arrange - Informamos um CPF válido
        String cpf = "40177715057";
        // Instanciamos a lista que simula ser o resultado da query
        List<?> mockedResult = Arrays.asList(1); // Como se tivesse sido encontrado 1 resultado


        // Simulamos criar uma NativeQuery
        when(em.createNativeQuery(anyString())).thenReturn(query);
        // Simulamos setParameter na nossa query com "CPF" com o nosso cpf instanciado
        when(query.setParameter(eq("cpf"), eq(cpf))).thenReturn(query);
        // Simulamos que o retorno da query foi oque definimos anteriormente no caso (1 resultado)
        when(query.getResultList()).thenReturn(mockedResult);

        // Executamos o método que queremos testar
        Boolean result = clientRepository.existsClientByCpf(cpf);

        // Afirma que o cliente existe no banco
        assertTrue(result);

        // Verificações extras para garantir que os métodos foram chamados corretamente

        // Verifica se o método foi chamado apenas 1 vez e verifica se a query contém SELECT 1 FROM tb_clients
        verify(em, times(1)).createNativeQuery(contains("SELECT 1 FROM tb_clients"));
        // Verifica se os parametros da query estão coerentes
        verify(query).setParameter("cpf", cpf);
        // Verifica o resultado da lista
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should return false when client does not exist by CPF.")
    void shouldReturnFalseWhenClientDoesNotExistByCpf() {
        // Arrange - informamos um CPF válido
        String cpf = "40177715057";
        // Instanciamos uma lista com nenhum resultado para simular que não existe cliente cadastrado com o CPF
        List<?> mockedResult = Arrays.asList();

        // Simulamos criar uma NativeQuery
        when(em.createNativeQuery(anyString())).thenReturn(query);
        // Simulamos que o parametro "cpf" receberá o nosso cpf instanciado anteriormente
        when(query.setParameter(eq("cpf"), eq(cpf))).thenReturn(query);
        // Simulamos que o resultado do método retorna uma lista vazia (porque não foi encontrado nenhum cliente)
        when(query.getResultList()).thenReturn(mockedResult);

        // Executamos o método que queremos testar
        Boolean result = clientRepository.existsClientByCpf(cpf);

        // Afirmamos que o resultado é false (porque não tem nenhum cliente cadastrado com o CPF)
        assertFalse(result);

        // Verificações adicionais para ver se tudo ocorreu como deveria

        // Verificamos se a query contém "SELECT 1 FROM tb_clients"
        verify(em).createNativeQuery(contains("SELECT 1 FROM tb_clients"));
        // Verificamos se os parametros da query estão corretos
        verify(query).setParameter("cpf", cpf);
        // Verificamos o resultado da query
        verify(query).getResultList();
    }

    @Test
    @DisplayName("Should ThrowException when fails to check client existence by CPF.")
    void shouldThrowExceptionWhenFailsToCheckClientExistenceByCpf() {
        // Arrange - informamos um CPF válido
        String cpf = "40177715057";

        // Simulamos criar uma NativeQuery
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos que houve uma exceção ao setar os parametros
        when(query.setParameter(eq("cpf"), eq(cpf)))
                .thenThrow(new RuntimeException("Simulated ThrowException"));

        // Executamos o método que queremos testar e afirmamos a exceção
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientRepository.existsClientByCpf(cpf);
        });


        // Verificamos se a mensagem da exceção é igual a esperada
        assertEquals("Erro ao verificar existência do CPF. ", exception.getMessage());

        // Verificamos que o getResultList não foi executado (porque falhou antes)
        verify(query, never()).getResultList();
    }

    // método existsClientByEmail
    @Test
    @DisplayName("Should return true when client exists by email.")
    void shouldReturnTrueWhenClientExistsByEmail() {
        // Arrange - Informamos um email válido
        String email = "alberto@gmail.com";
        // Instanciamos uma lista com 1 resultado
        List<Object> mockedResult = Arrays.asList(1);

        // Simulamos criar uma NativeQuery
        when(em.createNativeQuery(anyString()))
                .thenReturn(query);
        // Simulamos que a query tem o parametro "email"
        when(query.setParameter(eq("email"), eq(email)))
                .thenReturn(query);
        // Simulamos que a query retorna 1 resultado
        when(query.getResultList()).thenReturn(mockedResult);

        // Executamos o método que queremos testar
        Boolean result = clientRepository.existsClientByEmail(email);

        // Afirmamos o resultado true
        assertTrue(result);
    }

    @Test
    @DisplayName("Should ThrowException when client does not exist by email.")
    void shouldThrowExceptionWhenClientDoesNotExistByEmail() {
        // Arrange - informamos um email válido
        String email = "teste@gmail.com";

        // Instanciamos uma lista com nenhum resultado
        List<?> mockedResult = Arrays.asList();

        // Criamos uma native query
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos uma exceção
        when(query.setParameter(eq("email"),eq(email)))
                .thenReturn(query);

        // Simulamos obter o resultado de uma lista vazia
        when(query.getResultList()).thenReturn(mockedResult);

        // Executamos o método que queremos testar
        Boolean result = clientRepository.existsClientByEmail(email);

        // Confirmamos que o result é false (porque não encontrou nenhum resultado com o email)
        assertFalse(result);
    }

    @Test
    @DisplayName("Should ThrowException when fails to check client existence by email.")
    void shouldThrowExceptionWhenFailsToCheckClientExistenceByEmail() {
        // Arrange - informamos um email válido
        String email = "teste@gmail.com";

        // Criamos uma native query
        when(em.createNativeQuery(anyString())).thenReturn(query);

        // Simulamos uma exceção
        when(query.setParameter(eq("email"),eq(email)))
                .thenThrow(new RuntimeException("Simulated Exception."));

        // Afirmamos que houve uma exceção ao realizar o método que queremos testar
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientRepository.existsClientByEmail(email);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("Erro ao verificar existência do email.", exception.getMessage());

        // Verificamos se o método getResultList não foi usado (porque falhou antes)
        verify(query, never()).getResultList();
    }

    @Test
    @DisplayName("Should return a paginated list of ClientDto when clients exist.")
    void shouldReturnAPaginatedListOfClientDtoWhenClientsExist() {
        // Arrange - informamos parametros válidos esperados pelo método
        Integer page = 0;
        Integer linesPerPage = 2;
        String direction = "asc";
        String orderBy = "name";

        //Criamos uma list de clients
        List<Object[]> mockedResult = Arrays.asList(
                new Object[]{"Bruno Camargo", "bruno@gmail.com", "18068803009", java.sql.Date.valueOf(LocalDate.of(2000, 3, 5))},
                new Object[]{"João Silva", "João@gmail.com", "40177715057", java.sql.Date.valueOf(LocalDate.of(1990, 1, 1))},
                new Object[]{"Mateus Souza", "mateus@gmail.com", "49515077060", java.sql.Date.valueOf(LocalDate.of(1987, 4, 3))}
        );

        // Simulamos criar uma native query
        when(em.createNativeQuery(anyString()))
                .thenReturn(query);
        when(query.setParameter("limit", linesPerPage)).thenReturn(query);
        when(query.setParameter("offset", page * linesPerPage)).thenReturn(query);

        // Simulamos que a query retorna a lista de clientes criada
        when(query.getResultList()).thenReturn(mockedResult);

        // Executamos o método que queremos testar
        List<ClientDto> resultList = clientRepository.findAllClients(page, linesPerPage, direction, orderBy);

        // assert
        assertNotNull(resultList);
        assertEquals(3, resultList.size());

        ClientDto client1 = resultList.get(0);
        assertEquals("Bruno Camargo", client1.getName());
        assertEquals("bruno@gmail.com", client1.getEmail());
        assertEquals("18068803009", client1.getCpf());
        assertEquals(LocalDate.of(2000, 3, 5), client1.getBirth());

        ClientDto client2 = resultList.get(1);
        assertEquals("João Silva", client2.getName());
        assertEquals("João@gmail.com", client2.getEmail());
        assertEquals("40177715057", client2.getCpf());
        assertEquals(LocalDate.of(1990, 1, 1), client2.getBirth());

        ClientDto client3 = resultList.get(2);
        assertEquals("Mateus Souza", client3.getName());
        assertEquals("mateus@gmail.com", client3.getEmail());
        assertEquals("49515077060", client3.getCpf());
        assertEquals(LocalDate.of(1987, 4, 3), client3.getBirth());

        verify(em).createNativeQuery(anyString());
        verify(query).setParameter("limit", linesPerPage);
        verify(query).setParameter("offset", page * linesPerPage);
        verify(query).getResultList();
    }


}