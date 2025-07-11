package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.ClientDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.repositories.ClientRepository;
import com.rodrigopettenon.orderflow.utils.LogUtil;
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
class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LogUtil logUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should successfully save a new client.")
    void shouldSaveClientWithValidData() {
        // Arrange (Configuração dos dados simulados)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Soimulando comportamento dos métodos que consultam se o email ou cpf já existem
        when(clientRepository.existsClientByEmail("joao@gmail.com")).thenReturn(false);
        when(clientRepository.existsClientByCpf("25654428071")).thenReturn(false);

        // Act (Ação que você quer testar)
        clientService.save(dto);


        // Assert (Verificação do comportamento esperado)
        verify(clientRepository).saveClient(dto);
    }

    @Test
    @DisplayName("Should ThrowException when the name is null.")
    void shouldThrowExceptionWhenNameIsNull() {
        // Arrange (Criamos um DTO com nome nulo)
        ClientDto dto = new ClientDto();
        dto.setName(null);
        dto.setEmail("pericleiton@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1983, 8, 12));

        // Act & Assert (Afirma exceção com nome null)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente é obrigatório.", exception.getMessage());

        // Verifica se o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the name contains blank spaces.")
    void shouldThrowExceptionWhenNameContainsBlankSpaces() {
        // Arrange (Criamos um DTO com nome com espaços em branco)
        ClientDto dto = new ClientDto();
        dto.setName(" ");
        dto.setEmail("canetaazul@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1870, 12, 25));

        // Act & Assert (Afirma exceção com nome em branco)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente é obrigatório.", exception.getMessage());

        // Verifica se o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }


    @Test
    @DisplayName("Should ThrowException when name have 3 characters long or less ")
    void shouldThrowExceptionWhenNameHave3CharactersOrLess() {
        // Arrange (Criamos um DTO com nome tem 3 caracteres ou menos)
        ClientDto dto = new ClientDto();
        dto.setName("Ney");
        dto.setEmail("neymarjr@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 10, 10));

        // Act & Assert (Afirma exceção com nome com 3 caracteres ou menos)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente deve ter mais de 3 caracteres.", exception.getMessage());

        // Verifica se o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());

    }

    @Test
    @DisplayName("Should ThrowException when name is longer than 100 characters")
    void shouldThrowExceptionWhenNameIsLongerThan100Characters() {
        // Arrange (Criamos um DTO com nome maior que 100 caracteres)
        ClientDto dto = new ClientDto();
        dto.setName("Antônio Carlos da Silva Xavier Pereira Júnior de Almeida Oliveira Santos da Conceição Rodrigues do Vale");
        dto.setEmail("carimbo@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(2010, 11, 28));

        // Act e Assert (Afirma exceção por nome maior que 100 caracteres)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente deve ser menor que 100 caracteres.", exception.getMessage());

        // Verifica se o método saveClient não foi usado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }


    @Test
    @DisplayName("Should ThrowException when the CPF already exists on DB.")
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        // Arrange (preparar o cenário)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Simulando que o CPF já está cadastrado
        when(clientRepository.existsClientByCpf("25654428071")).thenReturn(true);

        // Act & Assert (executar e verificar se lança exceção)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Assert (verificar a mensagem da exceção)
        assertEquals("O CPF do cliente já está cadastrado.", exception.getMessage());

        // Verifica que o método saveClient não foi chamado
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the CPF is null.")
    void shouldThrowExceptionWhenCpfIsNull() {
        // Arrange (Criamos um DTO com cpf nulo)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf(null);
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Act & Assert (espera lançar a exceção correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Verifica se a mensagem é exatamente a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Garante que não tentamos salvar nada
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the CPF contains blank spaces.")
    void shouldThrowExceptionWhenCpfContainsBlankSpaces() {
        // Arrange (Criamos um DTO com cpf com espaços em branco)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf(" ");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Act & Assert (espera lançar a exceção correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Verifica se a mensagem é exatamente a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Garante que não tentamos salvar nada
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the CPF is invalid.")
    void shouldThrowExceptionWhenCpfIsInvalid() {
        // Arrange (Criamos um dto com o cpf inválido)
        ClientDto dto = new ClientDto();
        dto.setName("Joao Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf("111.111.111-11");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Act & Assert (espera lançar a exceção correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem é exatamente a esperada
        assertEquals("O CPF do cliente é inválido.", exception.getMessage());

        // Garante que não tentamos salvar nada
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the Email already exists on DB.")
    void shouldThrowExceptionWhenEmailAlreadyExists() {

        // Arrange (preparar o cenário)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail("joao@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        // Simulando que o repositório já tem o Email cadastrado
       when(clientRepository.existsClientByEmail("joao@gmail.com")).thenReturn(true);

        // Act & Assert (espera lançar a exceção com a mensagem correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Verifica se a mensagem é exatamente a esperada
        assertEquals("O email do cliente já está cadastrado.", exception.getMessage());

        // Verifica que o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the Email is null.")
    void shouldThrowExceptionWhenEmailIsNull() {

        // Arrange (Criamos DTO com Email nulo)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail(null);
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        //Act & Assert (espera lançar a exceção com a mensagem correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        //Verifica se a mensagem é exatamente a esperada
        assertEquals("O email do cliente é obrigatório.", exception.getMessage());

        //Verifica que o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the Email contains blank spaces.")
    void shouldThrowExceptionWhenEmailContainsBlankSpaces() {

        //Arrange (Criamos um DTO com Email em branco)
        ClientDto dto = new ClientDto();
        dto.setName("João Silva");
        dto.setEmail(" ");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(1990, 1, 1));

        //Act & Assert (espera lançar a exceção com a mensagem correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        //Verifica se a mensagem é exatamente a esperada
        assertEquals("O email do cliente é obrigatório.", exception.getMessage());

        //Verifica que o método saveClient não foi chamado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when the Email is invalid.")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        // Arrange (Criamos um DTO com Email inválido)
        ClientDto dto = new ClientDto();
        dto.setName("Joao Silva");
        dto.setEmail("joao.com");
        dto.setCpf("256.544.280-71");

        // Act & Assert (espera lançar a exceção com a mensagem correta)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Verifica se a mensagem de erro é exatamente a esperada
        assertEquals("O email do cliente é inválido.", exception.getMessage());

        // Verifica que o método do Repository não foi chamado por que falhou antes.
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when birth date is null.")
    void shouldThrowExceptionWhenBirthDateIsNull() {
        // Arrange (Criamos um DTO com a data de nascimento nula)
        ClientDto dto = new ClientDto();
        dto.setName("Irineu");
        dto.setEmail("irineu@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(null);


        // Act & Assert (Afirma exceção com a data de nascimento nula)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento do cliente é obrigatória.", exception.getMessage());

        // Verifica se o método saveClient não foi usado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }

    @Test
    @DisplayName("Should ThrowException when birth date is in the future")
    void shouldThrowExceptionWhenBirthDateIsInTheFuture() {
        // Arrange (Criamos um dto em que a data de nascimento está no futuro)
        ClientDto dto = new ClientDto();
        dto.setName("Doutor Manhattan");
        dto.setEmail("drmanhattancontato@gmail.com");
        dto.setCpf("256.544.280-71");
        dto.setBirth(LocalDate.of(5000, 7, 23));

        // Act & Assert (Afirma exceção com a data de nascimento no futuro)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.save(dto);
        });

        // Verifica se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento não pode ser futura.", exception.getMessage());

        // Verifica se o método saveClient não foi usado (porque falhou antes)
        verify(clientRepository, never()).saveClient(any());
    }
}