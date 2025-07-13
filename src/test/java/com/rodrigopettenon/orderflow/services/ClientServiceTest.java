package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.ClientDto;
import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
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
import java.util.ArrayList;
import java.util.List;

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


    // Método "save"
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


    // Método findByEmail -- algumas válidações já foram cobertas pelo método save como: "email inválido, email nulo ou com espaços em branco e etc"
    @Test
    @DisplayName("Should return client when email is valid and exists")
    void shouldReturnClientWhenEmailIsValidAndExists() {
        // Arrange (Criamos o DTO que o repositório deve retornar com email válido e existente)
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Galo Cego");
        expectedClient.setEmail("galocego@gmail.com");
        expectedClient.setCpf("256.544.280-71");
        expectedClient.setBirth(LocalDate.of(2000, 9, 10));

        // Simulamos que o email existe no banco
        when(clientRepository.existsClientByEmail(expectedClient.getEmail())).thenReturn(true);

        // Simulamos que o repositório retorna o DTO correto
        when(clientRepository.findClientByEmail(expectedClient.getEmail())).thenReturn(expectedClient);

        // Act (executar a ação que queremos testar)
        ClientDto clientReturned = clientService.findByEmail(expectedClient.getEmail());

        // Assert (verificamos se tudo funcionou como deveria)
        assertEquals("Galo Cego", clientReturned.getName());
        assertEquals("galocego@gmail.com", clientReturned.getEmail());
        assertEquals("256.544.280-71", clientReturned.getCpf());
        assertEquals(LocalDate.of(2000, 9, 10), clientReturned.getBirth());

        // E verificamos se o método do repositório foi mesmo chamado
        verify(clientRepository).findClientByEmail(expectedClient.getEmail());
    }

    @Test
    @DisplayName("Should ThrowException when email is not registered.")
    void shouldThrowExceptionWhenEmailIsNotRegistered() {
        // Arrange (Criamos um DTO que o repositório deve retornar que o email não está cadastrado)
        String email = ("cocielo@gmail.com");

        // Simulamos que o email não existe no banco
        when(clientRepository.existsClientByEmail(email)).thenReturn(false);

        // Act & Assert (Afirma exceção com email não existente)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.findByEmail(email);
        });

        // Verifica se a mensagem do erro é igual a esperada
        assertEquals("Nenhum cliente cadastrado com esse email.", exception.getMessage());

        // Verifica que o método findClientByEmail não foi chamado (por que parou na validação)
        verify(clientRepository, never()).findClientByEmail((any()));

    }


    // Método findByCpf --  algumas válidações já foram cobertas pelo método save como: "cpf inválido, cpf nulo ou com espaços em branco e etc"
    @Test
    @DisplayName("Should return client when cpf is valid and exists.")
    void shouldReturnClientWhenCpfIsValidAndExists() {
        // Arrange (Criar DTO com cpf valído e normalizado que o repository deverá retornar como existente)
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Dorival");
        expectedClient.setEmail("dorival@gmail.com");
        expectedClient.setCpf("25654428071"); // Com cpf já normalizado
        expectedClient.setBirth(LocalDate.of(1988, 1, 7));

        // Simular que o CPF está cadastrado
        when(clientRepository.existsClientByCpf(expectedClient.getCpf())).thenReturn(true);

        // Simular que o repository retorna o dto como o esperado
        when(clientRepository.findClientByCpf(expectedClient.getCpf())).thenReturn(expectedClient);

        // Executar a ação que queremos testar
        ClientDto clientReturned = clientService.findByCpf(expectedClient.getCpf());

        // Assert (Verificamos se tudo funcionou como deveria)
        assertEquals(clientReturned.getName(), expectedClient.getName());
        assertEquals(clientReturned.getEmail(), expectedClient.getEmail());
        assertEquals(clientReturned.getCpf(), expectedClient.getCpf());
        assertEquals(clientReturned.getBirth(), expectedClient.getBirth());

        // Verificamos se o método do repository realmente foi chamado
        verify(clientRepository).findClientByCpf(expectedClient.getCpf());
    }

    @Test
    @DisplayName("Should ThrowException when cpf is not registered.")
    void shouldThrowExceptionWhenCpfIsNotRegistered() {
        // Arrange (Criamos o cpf)
        String cpf = "25654428071"; // CPF já normalizado

        // Simulamos que o CPF não existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(false);

        // Act & Assert (Afirma exceção ao tentar buscar por CPF inexistente)
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.findByCpf(cpf);
        });

        // Verifica se a mensagem da exceção é igual a esperada
        assertEquals("Nenhum cliente cadastrado com esse CPF.", exception.getMessage());

        // Verifica que o método findClientByCpf não foi usado (por que falhou antes)
        verify(clientRepository, never()).findClientByCpf(any());

    }


    // Método updateByCpf
    @Test
    @DisplayName("Should return updated client data when cpf is valid and exists")
    void shouldReturnUpdatedClientDataWhenCpfIsValidAndExists() {
        // Arrange (Informamos um cpf válido e "existente" no banco)
        String cpf = "25654428071"; // CPF válido e normalizado

        // DTO com os dados fornecidos na requisição (Sem CPF pois ele não pode ser atualizado)
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Tony Stark");
        expectedClient.setEmail("tonystark@gmail.com");
        expectedClient.setBirth(LocalDate.of(1989, 4, 8));

        // DTO que deverá ser retornado pelo método após ter sido atualizado
        ClientDto updatedClient = new ClientDto();
        updatedClient.setName("Tony Stark");
        updatedClient.setEmail("tonystark@gmail.com");
        updatedClient.setCpf(cpf);
        updatedClient.setBirth(LocalDate.of(1989, 4, 8));

        // Simulamos que o cpf informado existe no banco de dados
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que os dados do cliente foram atualizados com sucesso
        when(clientRepository.updateClientByCpf(cpf, expectedClient)).thenReturn(updatedClient);

        // Executamos a ação que queremos testar
        ClientDto clientReturned = clientService.updateByCpf(cpf, expectedClient);

        // Assert - Verificamos se tudo funcionou como deveria
        assertEquals(updatedClient.getName(), clientReturned.getName());
        assertEquals(updatedClient.getEmail(), clientReturned.getEmail());
        assertEquals(updatedClient.getCpf(), clientReturned.getCpf());
        assertEquals(updatedClient.getBirth(), clientReturned.getBirth());

        // Verificamos se o método do repository realmente foi chamado
        verify(clientRepository).updateClientByCpf(cpf, expectedClient);
    }


    @Test
    @DisplayName("Should ThrowException when cpf is not registered for an update.")
    void shouldThrowExceptionWhenCpfIsNotRegisteredForAnUpdate() {
        // Arrange (Informamos um CPF inexistente no banco)
        String cpf = "51831487080"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o CPF existisse
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Luva de Pedreiro");
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o cpf não existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(false);

        // Simulamos que deu erro ao tentar dar update no cliente com o CPF não cadastrado no banco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("Nenhum cliente cadastrado com esse CPF.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }


    @Test
    @DisplayName("Should ThrowException when cpf is null for an update.")
    void shouldThrowExceptionWhenCpfIsNullAnUpdate() {
        // Arrange (Informamos um CPF nulo)
        String cpf = null; // CPF null

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o CPF não fosse nulo
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Luva de Pedreiro");
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que deu erro ao tentar dar update no cliente com o CPF nulo
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when cpf contains spaces for an update.")
    void shouldThrowExceptionWhenCpfContainsSpacesForAnUpdate() {
        // Arrange (Informamos um CPF com espaços em branco)
        String cpf = "   "; // CPF com espaços em branco

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o CPF não estivesse com espaços em branco
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Luva de Pedreiro");
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que deu erro ao tentar dar update no cliente com o CPF com espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when cpf is invalid for an update.")
    void shouldThrowExceptionWhenCpfIsInvalidForAnUpdate() {
        // Arrange (Informamos um CPF inválido)
        String cpf = "11111111111"; // CPF inválido

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o CPF fosse inválido
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Luva de Pedreiro");
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que deu erro ao tentar dar update no cliente com o CPF inválido
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O CPF do cliente é inválido.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }


    @Test
    @DisplayName("Should ThrowException when name is null for an update.")
    void shouldThrowExceptionWhenNameIsNullForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o nome não fosse null
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName(null); // nome null
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o nome null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when name contains blank spaces for an update.")
    void shouldThrowExceptionWhenNameContainsBlankSpacesForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o nome não tivesse apenas espaços em branco
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("      "); // nome com espaços em branco
        expectedClient.setEmail("luva@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o nome somente com espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when name contains 3 characters or less for an update.")
    void shouldThrowExceptionWhenNameContains3CharactersOrLessForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o nome não tivesse 3 caracteres ou menos
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Ana"); // nome 3 caracteres ou menos
        expectedClient.setEmail("ana@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o nome de 3 caracteres ou menos
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente deve ter mais de 3 caracteres.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the name contains more than 100 characters for an update.")
    void shouldThrowExceptionWhenNameContainsMoreThan100CharactersForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o nome não tivesse mais de 100 caracteres
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio Carlos da Silva Xavier Pereira Júnior de Almeida Oliveira Santos da Conceição Rodrigues do Vale"); // nome maior que 100 caracteres
        expectedClient.setEmail("ana@gmail.com");
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o nome de maior que 100 caracteres
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O nome do cliente deve ser menor que 100 caracteres.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when email is null for an update.")
    void shouldThrowExceptionWhenIsNullForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o email não fosse nulo.
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio");
        expectedClient.setEmail(null); // email nulo
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o email nulo
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O email do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }


    @Test
    @DisplayName("Should ThrowException when email contains blank spaces for an update.")
    void shouldThrowExceptionWhenEmailContainsBlankSpacesForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o email não tivesse espaços em branco.
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio");
        expectedClient.setEmail("    "); // email com espaços em branco
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o email de espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O email do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the email is invalid for an update.")
    void shouldThrowExceptionWhenEmailIsInvalidForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso o email não fosse inválido
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio");
        expectedClient.setEmail("antoniogmail.com"); // email inválido
        expectedClient.setBirth(LocalDate.of(1999, 9, 1));

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com o email invalido
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O email do cliente é inválido.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when birthdate is null for an update.")
    void shouldThrowExceptionWhenBirthDateIsNullForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso a data de nascimento não fosse null
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio");
        expectedClient.setEmail("antonio@gmail.com");
        expectedClient.setBirth(null); // data de nascimento null

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com a data de nascimento null
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento do cliente é obrigatória.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when the birthdate is in the future for an update.")
    void shouldThrowExceptionWhenBirthDateIsInTheFutureForAnUpdate() {
        // Arrange (Informamos um CPF válido)
        String cpf = "25654428071"; // CPF válido e normalizado

        // Criamos um dto com os dados que "supostamente" seriam atualizados caso a data de nascimento não estivesse no futuro
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName("Antônio");
        expectedClient.setEmail("antonio@gmail.com");
        expectedClient.setBirth(LocalDate.of(3025, 1, 1)); // data de nascimento no futuro

        // Simulamos que o CPF existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // Simulamos que deu erro ao tentar dar update no cliente com a data de nascimento no futuro
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.updateByCpf(cpf, expectedClient);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento não pode ser futura.", exception.getMessage());

        // Verificamos que o método updateClientByCpf nunca foi chamado (porque falhou antes)
        verify(clientRepository, never()).updateClientByCpf(any(), any());
    }

    // Método deleteByCpf
    @Test
    @DisplayName("Should successfully delete the client by cpf.")
    void shouldSuccessfullyDeleteClientByCpf() {
        // Arrange (informamos um CPF válido para a deleção do cliente)
        String cpf = "25654428071"; // CPF normalizado e válido

        // Simulamos que o CPF existe no nosso banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(true);

        // executamos a ação que queremos testar
        clientService.deleteByCpf(cpf);

        // Confirmamos que o repository foi usado porque tudo deu certo
        verify(clientRepository).deleteClientByCpf(cpf);
    }


    @Test
    @DisplayName("Should ThrowException when cpf is not registered for deletion.")
    void shouldThrowExceptionWhenCpfIsNotRegisteredForDeletion() {
        // Arrange (informamos um CPF "inexistente no banco")
        String cpf = "16465154048"; // CPF válido e normalizado "que trataremos como inexistente"

        // Simulamos que o CPF não existe no banco
        when(clientRepository.existsClientByCpf(cpf)).thenReturn(false);

        // Afirmamos que houve uma exceção pois o CPF não existe no banco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.deleteByCpf(cpf);
        });

        // Verificamos que a mensagem de erro é igual a esperada
        assertEquals("Nenhum cliente cadastrado com esse CPF.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).deleteClientByCpf(any());
    }

    @Test
    @DisplayName("Should ThrowException when cpf is invalid for deletion.")
    void shouldThrowExceptionWhenCpfIsInvalidForDeletion() {
        // Arrange (informamos um cpf inválido)
        String cpf = "22222222222"; // CPF inválido

        // Afirmamos uma exceção pois o CPF é inválido
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.deleteByCpf(cpf);
        });

        // Verificamos que a mensagem é igual a esperada
        assertEquals("O CPF do cliente é inválido.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).deleteClientByCpf(any());
    }

    @Test
    @DisplayName("Should ThrowException when cpf is null for deletion.")
    void shouldThrowExceptionWhenCpfIsNullForDeletion() {
        // Arrange (Informamos um CPF nulo)
        String cpf = null; // CPF nulo

        // Afirmamos uma exceção pois o CPF é nulo
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.deleteByCpf(cpf);
        });

        // Verificamos que a mensagem é igual a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository nunca foi usado (porque falhou antes)
        verify(clientRepository, never()).deleteClientByCpf(any());
    }

    @Test
    @DisplayName("Should ThrowException when cpf contains blank spaces for deletion.")
    void shouldThrowExceptionWhenCpfContainsBlankSpacesForDeletion() {
        // Arrange (informamos um cpf com espaços em branco)
        String cpf = "    ";

        // Afirmamos uma exceção pois o CPF tem apenas espaços em branco
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.deleteByCpf(cpf);
        });

        // Verificamos que a mensagem de erro é igual a esperada
        assertEquals("O CPF do cliente é obrigatório.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).deleteClientByCpf(any());
    }

    // Método findFilteredClients
    @Test
    @DisplayName("Should return the successfully filtered client.")
    void shouldReturnTheSuccessfullyFilteredClient() {
        // Arrange (informamos todos os filtros corretamente)
        String name = "Beatriz"; // nome válido
        String email = "beatriz@gmail.com"; // email válido
        String cpf = "16465154048"; // cpf válido e normalizado
        LocalDate birthStart = LocalDate.of(2000, 3, 1); // Data de inicio válida e anterior a data final
        LocalDate birthEnd = LocalDate.of(2010, 1, 1); // Data final válida e após a data inicio
        Integer page = 0; // página válida
        Integer linesPerPage= 10; // linhas por página
        String direction = "asc"; // direção válida
        String orderBy = "name"; // ORDER BY válido

        // Criamos um DTO do cliente que queremos retornar
        ClientDto expectedClient = new ClientDto();
        expectedClient.setName(name);
        expectedClient.setEmail(email);
        expectedClient.setCpf(cpf);
        expectedClient.setBirth(LocalDate.of(2005, 7, 3));

        // Criamos um GlobalPageDto que esperamos que o método retorne
        GlobalPageDto<ClientDto> expectedPageDto = new GlobalPageDto<>();

        // criamos uma lista de clientDto para adicionar expectedPageDto
        List<ClientDto> expectedItems = new ArrayList<>();
        expectedItems.add(expectedClient);
        Number total = expectedItems.size();

        // Salvamos tudo no nosso expectedPageDto
        expectedPageDto.setItems(expectedItems);
        expectedPageDto.setTotal(total.longValue());

        // Simulamos que o nosso método retornou a page esperada
        when(clientRepository.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                page, linesPerPage, direction, orderBy)).thenReturn(expectedPageDto);

        // Executamos o método que queremos testar
        GlobalPageDto<ClientDto> returnedPageDto = clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                page, linesPerPage, direction, orderBy);

        // Verificamos se a page esperada é igual a retornada
        assertEquals(expectedPageDto.getTotal(), returnedPageDto.getTotal());
        assertEquals(expectedPageDto.getItems().size(), returnedPageDto.getItems().size());
        assertEquals(expectedPageDto.getItems().get(0).getName(), returnedPageDto.getItems().get(0).getName());
        assertEquals(expectedPageDto.getItems().get(0).getEmail(), returnedPageDto.getItems().get(0).getEmail());
        assertEquals(expectedPageDto.getItems().get(0).getCpf(), returnedPageDto.getItems().get(0).getCpf());
        assertEquals(expectedPageDto.getItems().get(0).getBirth(), returnedPageDto.getItems().get(0).getBirth());

        // Verificamos que o repository foi usado pois todos os filtros foram preenchidos corretamente
        verify(clientRepository).findFilteredClients(name, email, cpf, birthStart, birthEnd,
                page, linesPerPage, direction, orderBy);
    }

    @Test
    @DisplayName("Should ThrowException when cpf is invalid to find filtered clients.")
    void shouldThrowExceptionWhenCpfContainsLessThan11CharactersToFindFilteredClients() {
        // Arrange (informamos um CPF inválido)
        String name = "Beatriz"; // nome válido
        String email = "beatriz@gmail.com"; // email válido
        String cpf = "33333333333"; // cpf inválido
        LocalDate birthStart = LocalDate.of(2000, 3, 1); // Data de inicio válida e anterior a data final
        LocalDate birthEnd = LocalDate.of(2010, 1, 1); // Data final válida e após a data inicio
        Integer page = 0; // página válida
        Integer linesPerPage= 10; // linhas por página
        String direction = "asc"; // direção válida
        String orderBy = "name"; // ORDER BY válido

        // Afirmamos exceção pois o filtro CPF inválido
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
           clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                   page, linesPerPage, direction, orderBy);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("O CPF do cliente é inválido.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).findFilteredClients(any(),any(), any(), any(),
                any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should ThrowException when birth start date filter is in the future to find filtered clients.")
    void shouldThrowExceptionWhenBirthStartFilterIsInTheFutureToFindFilteredClients() {
        // Arrange (informamos um filtro data de nascimento de início que está futuro)
        String name = "Beatriz"; // nome válido
        String email = "beatriz@gmail.com"; // email válido
        String cpf = "16465154048"; // cpf válido e normalizado
        LocalDate birthStart = LocalDate.of(3000, 3, 1); // Data de nascimento início no futuro
        LocalDate birthEnd = LocalDate.of(2010, 1, 1); // Data de nascimento final válida
        Integer page = 0; // página válida
        Integer linesPerPage= 10; // linhas por página
        String direction = "asc"; // direção válida
        String orderBy = "name"; // ORDER BY válido

        // Afirmamos exceção pois o filtro de inicio de data de nascimento está no futuro
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                    page, linesPerPage, direction, orderBy);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento de início não pode ser futura.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).findFilteredClients(any(),any(), any(), any(),
                any(), any(), any(), any(), any());

    }

    @Test
    @DisplayName("Should ThrowException when birth end date filter is in the future to find filtered clients.")
    void shouldThrowExceptionWhenBirthEndFilterIsInTheFutureToFindFilteredClients() {
        // Arrange (informamos um filtro de data de nascimento final que está futuro)
        String name = "Beatriz"; // nome válido
        String email = "beatriz@gmail.com"; // email válido
        String cpf = "16465154048"; // cpf válido e normalizado
        LocalDate birthStart = LocalDate.of(2010, 3, 1); // Data de nascimento início válida
        LocalDate birthEnd = LocalDate.of(3000, 1, 1); // Data de nascimento final no futuro
        Integer page = 0; // página válida
        Integer linesPerPage= 10; // linhas por página
        String direction = "asc"; // direção válida
        String orderBy = "name"; // ORDER BY válido

        // Afirmamos exceção pois o filtro de inicio de data de nascimento está no futuro
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                    page, linesPerPage, direction, orderBy);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento final não pode ser futura.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).findFilteredClients(any(),any(), any(), any(),
                any(), any(), any(), any(), any());

    }

    @Test
    @DisplayName("Should ThrowException when the birth start date filter is later than the birth end date to find filtered clients.")
    void shouldThrowExceptionWhenBirthStartFilterIsLaterThanTheBirthEndToFindFilteredClients() {
        // Arrange (informamos um filtro de data de nascimento final que está futuro)
        String name = "Beatriz"; // nome válido
        String email = "beatriz@gmail.com"; // email válido
        String cpf = "16465154048"; // cpf válido e normalizado
        LocalDate birthStart = LocalDate.of(2025, 3, 1); // Data de nascimento início posterior a data final
        LocalDate birthEnd = LocalDate.of(2009, 1, 1); // Data de nascimento final
        Integer page = 0; // página válida
        Integer linesPerPage= 10; // linhas por página
        String direction = "asc"; // direção válida
        String orderBy = "name"; // ORDER BY válido

        // Afirmamos exceção pois o filtro de data de nascimento início é posterior a data final
        ClientErrorException exception = assertThrows(ClientErrorException.class, () -> {
            clientService.findFilteredClients(name, email, cpf, birthStart, birthEnd,
                    page, linesPerPage, direction, orderBy);
        });

        // Verificamos se a mensagem de erro é igual a esperada
        assertEquals("A data de nascimento de início não pode ser maior que a data final.", exception.getMessage());

        // Verificamos que o método do repository não foi usado (porque falhou antes)
        verify(clientRepository, never()).findFilteredClients(any(),any(), any(), any(),
                any(), any(), any(), any(), any());

    }

    @Test
    @DisplayName("Should apply default direction ASC when an invalid direction is provided.")
    void shouldApplyDefaultDirectionAscWhenAnInvalidDirectionIsProvided() {
        // Arrange (informamos um direction inválido)
        Integer page = 0; // página válida
        Integer linesPerPage = 10; // linhas por página
        String direction = "lateral"; // direction inválido
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para "asc" foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default direction ASC when null direction is provided.")
    void shouldApplyDefaultDirectionAscWhenNullDirectionIsProvided() {
        // Arrange (informamos um direction null)
        Integer page = 0; // página válida
        Integer linesPerPage = 10; // linhas por página
        String direction = null; // direction null
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para "asc" foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq("asc"), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default order by name when an invalid order by is provided.")
    void shouldApplyDefaultOrderByNameWhenAnInvalidOrderByIsProvided() {
        // Arrange (informamos um order by inválido)
        Integer page = 0; // página válida
        Integer linesPerPage = 10; // linhas por página
        String direction = "asc"; // direction válido
        String orderBy = "mother_name"; // ORDER BY inválido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name")))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para "name" foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"));
    }

    @Test
    @DisplayName("Should apply default order by name when null order by is provided.")
    void shouldApplyDefaultOrderByNameWhenNullOrderByIsProvided() {
        // Arrange (informamos um order by nulo)
        Integer page = 0; // página válida
        Integer linesPerPage = 10; // linhas por página
        String direction = "asc"; // direction válido
        String orderBy = null; // ORDER BY nulo

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name")))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para "name" foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(linesPerPage), eq(direction), eq("name"));
    }


    @Test
    @DisplayName("Should apply default page when a negative number page is provided.")
    void shouldApplyDefaultPageWhenANegativeNumberPageIsProvided() {
        // Arrange (informamos uma pagina com número negativo)
        Integer page = -1; // página com número negativo
        Integer linesPerPage = 10; // linhas por página
        String direction = "asc"; // direction válido
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para 0 foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default page when null page is provided.")
    void shouldApplyDefaultPageWhenNullPageIsProvided() {
        // Arrange (informamos uma pagina nula)
        Integer page = null; // página nula
        Integer linesPerPage = 10; // linhas por página
        String direction = "asc"; // direction válido
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para 0 foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(0), eq(linesPerPage), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default lines per page when null lines per page are provided.")
    void shouldApplyDefaultLinesPerPageWhenNullLinesPerPageAreProvided() {
        // Arrange (informamos linhas por pagina null)
        Integer page = 0; // página válida
        Integer linesPerPage = null; // linhas por página null
        String direction = "asc"; // direction válido
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para 10 foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply default lines per page when a negative number of lines per page is provided.")
    void shouldApplyDefaultLinesPerPageWhenANegativeNumberOfLinesPerPageIsProvided() {
        // Arrange (informamos linhas por pagina com número negativo)
        Integer page = 0; // página válida
        Integer linesPerPage = -5; // linhas por página com número negativo
        String direction = "asc"; // direction válido
        String orderBy = "name"; // ORDER BY válido

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy)))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, null, null, null,
                page, linesPerPage, direction, orderBy);

        // Assert: garante que o fallback para 10 foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), any(), any(), any(),
                eq(page), eq(10), eq(direction), eq(orderBy));
    }

    @Test
    @DisplayName("Should apply the default return null when the filter name contains blank spaces.")
    void shouldApplyTheDefaultReturnNullWhenTheFilterNameContainsBlankSpaces() {
        // Arrange (informamos um nome com espaços em branco)
        String name = "    "; // nome com espaços em branco

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(eq(null), any(), any(), any(), any(),
                any(), any(), any(), any()))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(name, null, null, null, null,
                null, null, null, null);

        // Assert: garante que o fallback para null foi aplicado
        verify(clientRepository).findFilteredClients(eq(null), any(), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should apply the default return null when the filter email contains blank spaces.")
    void shouldApplyTheDefaultReturnNullWhenTheFilterEmailContainsBlankSpaces() {
        // Arrange (informamos um email com espaços em branco)
        String email = "    "; // email com espaços em branco

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), eq(null), any(), any(), any(),
                any(), any(), any(), any()))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, email, null, null, null,
                null, null, null, null);

        // Assert: garante que o fallback para null foi aplicado
        verify(clientRepository).findFilteredClients(any(), eq(null), any(), any(), any(),
                any(), any(), any(), any());
    }

    @Test
    @DisplayName("Should apply the default return null when the filter cpf contains blank spaces.")
    void shouldApplyTheDefaultReturnNullWhenTheFilterCpfContainsBlankSpaces() {
        // Arrange (informamos um cpf com espaços em branco)
        String cpf = "    "; // cpf com espaços em branco

        // Simuala retorno do repository com direction padrão aplicado
        when(clientRepository.findFilteredClients(any(), any(), eq(null), any(), any(),
                any(), any(), any(), any()))
                .thenReturn(new GlobalPageDto<>());

        // Act
        clientService.findFilteredClients(null, null, cpf, null, null,
                null, null, null, null);

        // Assert: garante que o fallback para null foi aplicado
        verify(clientRepository).findFilteredClients(any(), any(), eq(null), any(), any(),
                any(), any(), any(), any());
    }




}