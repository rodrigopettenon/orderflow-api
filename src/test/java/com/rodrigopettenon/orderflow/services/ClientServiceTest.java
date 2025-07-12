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


}