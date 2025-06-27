package com.rodrigopettenon.cadastro_e_consulta.services;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ClientDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.GlobalPageDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.rodrigopettenon.cadastro_e_consulta.utils.LogUtil.*;
import static com.rodrigopettenon.cadastro_e_consulta.utils.StringsValidation.*;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ClientService {

    private static final List<String> ALLOWED_DIRECTIONS = Arrays.asList("asc", "desc");
    private static final List<String> ALLOWED_ORDER_BY = Arrays.asList("name", "email", "cpf", "birth_date");

    @Autowired
    private ClientRepository clientRepository;


    @Transactional
    public void save(ClientDto clientDto){
        logSaveClientWithCpfStart(clientDto.getCpf());

        validateName(clientDto.getName());
        validateEmail(clientDto.getEmail());
        emailExists(clientDto.getEmail());
        validateBirth(clientDto.getBirth());

        String cpf = validateAndNormalizeCpf(clientDto.getCpf());
        cpfExists(cpf);
        clientDto.setCpf(cpf);

        clientRepository.saveClient(clientDto);
        logClientSavedWithCpfSuccessfully(cpf);
    }

    public GlobalPageDto<ClientDto> findAllClients(Integer page, Integer linesPerPage, String direction, String orderBy) {
        logFindAllClientsStart();
        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(linesPerPage);
        String fixedDirection = resolveDirectionOrDefault(direction);
        String fixedOrderBy = resolveOrderByOrDefault(orderBy);


        List<ClientDto> clients = clientRepository.findAllClients(sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);
        Long total = clientRepository.countTotalClients();

        GlobalPageDto<ClientDto> clientPageDto = new GlobalPageDto<>();
        clientPageDto.setItems(clients);
        clientPageDto.setTotal(total);

        logFindAllClientsSuccessfully();
        return clientPageDto;
    }

    public GlobalPageDto<ClientDto> findFilteredClients(String name, String email,
                                             String cpf, LocalDate birthStart,
                                             LocalDate birthEnd, Integer page,
                                             Integer linesPerPage, String direction,
                                             String orderBy) {
        logFindFilteredClientsStart();

        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(linesPerPage);

        String validatedNameFilter = sanitizeNameFilter(name);
        String validatedEmailFilter = sanitizeEmailFilter(email);
        String validatedCpfFilter = sanitizeAndValidateCpfFilter(cpf);
        String fixedDirection = resolveDirectionOrDefault(direction);
        String fixedOrderBy = resolveOrderByOrDefault(orderBy);

        LocalDate validatedBirthStart = validateBirthStart(birthStart, birthEnd);
        LocalDate validatedBirthEnd = validateBirthEnd(birthEnd, birthStart);

        return clientRepository.findFilteredClients(validatedNameFilter, validatedEmailFilter, validatedCpfFilter, validatedBirthStart,
                validatedBirthEnd, sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    @Transactional(readOnly = true)
    public ClientDto findByEmail(String email) {
        logFindClientByEmailStart(email);

        validateEmail(email);
        emailNotExist(email);

        return clientRepository.findClientByEmail(email);
    }

    @Transactional(readOnly = true)
    public ClientDto findByCpf(String cpf) {
        logFindClientByCpfStart(cpf);
        String validatedCpf = validateAndNormalizeCpf(cpf);
        cpfNotExist(validatedCpf);

        return clientRepository.findClientByCpf(validatedCpf);
    }

    @Transactional
    public ClientDto updateByCpf(String cpf, ClientDto clientDto) {
        logClientUpdateByCpfStart(cpf);
        //Validações do CPF
        String validatedCpf = validateAndNormalizeCpf(cpf);
        cpfNotExist(validatedCpf);

        //Validações do body da requisição
        validateName(clientDto.getName());
        validateEmail(clientDto.getEmail());
        validateBirth(clientDto.getBirth());

        return clientRepository.updateClientByCpf(validatedCpf, clientDto);
    }

    @Transactional
    public void deleteByCpf(String cpf) {
        logClientDeletionByCpfStart(cpf);

        String validatedCpf = validateAndNormalizeCpf(cpf);
        cpfNotExist(validatedCpf);

        clientRepository.deleteClientByCpf(validatedCpf);
        logClientDeletedByCpfSuccessfully(cpf);
    }

    //Validações filtros GET

    private String sanitizeNameFilter(String name) {
        logClientNameFilterValidation(name);
        String validatedNameFilter = normalizeSpaces(name);
        if (isBlank(validatedNameFilter)) {
            return null;
        }
        return validatedNameFilter;
    }

    private String sanitizeEmailFilter(String email) {
        logClientEmailFilterValidation(email);
        String validatedEmailFilter = removeAllSpaces(email);
        if (isBlank(validatedEmailFilter)) {
            return null;
        }
        return validatedEmailFilter;
    }

    private String sanitizeAndValidateCpfFilter(String cpf) {
        logClientCpfFilterValidation(cpf);
        String validatedCpfFilter = denormalizeCpf(cpf);
        if(isBlank(validatedCpfFilter)) {
            return null;
        }
        if (validatedCpfFilter.length() != 11) {
            throw new ClientErrorException("O CPF do cliente deve conter 11 digitos.");
        }
        if (!isValidCPF(validatedCpfFilter)) {
            throw new ClientErrorException("O CPF do cliente é inválido.");
        }

        return validatedCpfFilter;
    }

    private LocalDate validateBirthStart(LocalDate birthStart, LocalDate birthEnd) {
        logClientBirthStartFilterValidation(birthStart);
        if (isNull(birthStart) || isNull(birthEnd)) {
            return null;
        }
        if (birthStart.isAfter(LocalDate.now())) {
            throw new ClientErrorException("A data de nascimento de ínicio não pode ser futura.");
        }
        if (birthStart.isAfter(birthEnd)) {
            throw new ClientErrorException("A data de nascimento de ínicio não pode ser maior que a data final.");
        }
        return birthStart;
    }

    private LocalDate validateBirthEnd(LocalDate birthEnd, LocalDate birthStart) {
        logClientBirthEndFilterValidation(birthEnd);
        if(isNull(birthStart) || isNull(birthEnd)) {
            return null;
        }
        if (birthEnd.isAfter(LocalDate.now())) {
            throw new ClientErrorException("A data de nascimento final não pode ser futura.");
        }
        if (birthEnd.isBefore(birthStart)) {
            throw new ClientErrorException("A data de nascimento final não pode ser menor que a data de ínicio. ");
        }
        return birthEnd;
    }

    // Validações page, linesPerPage, direction e OrderBy paginação

    private Integer sanitizePage(Integer page) {
        if (isNull(page) || page < 0) {
            return 0;
        }
        return page;
    }

    private Integer sanitizeLinesPerPage(Integer linesPerPage) {
        if(isNull(linesPerPage)|| linesPerPage <= 0) {
            return 10;
        }
        return linesPerPage;
    }

    private String resolveDirectionOrDefault(String direction) {
        if (isBlank(direction) || !ALLOWED_DIRECTIONS.contains(direction.toLowerCase())) {
            return "asc";
        }
        return direction.toLowerCase();
    }

    private String resolveOrderByOrDefault(String orderBy) {
        if (isBlank(orderBy) || !ALLOWED_ORDER_BY.contains(orderBy.toLowerCase())) {
            return "name";
        }
        return orderBy.toLowerCase();
    }

    //Validações de dados

    private void validateName(String name){
        logClientNameValidation(name);

        if (isBlank(name)) {
            throw new ClientErrorException("O nome do cliente é obrigatório.");
        }
        if (name.length() <= 3) {
            throw new ClientErrorException("O nome do cliente deve ter mais de 3 caracteres.");
        }
    }

    private void validateBirth(LocalDate birth) {
        logClientBirthValidation(birth);

        if (isNull(birth)) {
            throw new ClientErrorException("A data de nascimento do cliente é obrigatória.");
        }
        if (birth.isAfter(LocalDate.now())) {
            throw new ClientErrorException("A data de nascimento não pode ser futura.");
        }
    }

    private void validateEmail(String email) {
        logClientEmailValidation(email);

        if (isBlank(email)) {
            throw new ClientErrorException("O email do cliente é obrigatório.");
        }
        if (!isValidEmail(email)) {
            throw new ClientErrorException("O email do cliente é inválido.");
        }
    }

    private void emailExists(String email) {
        if (clientRepository.existsClientByEmail(email)) {
            logClientEmailAlreadyExists(email);
            throw new ClientErrorException("O email do cliente já está cadastrado.");
        }
    }

    private void emailNotExist(String email) {
        if (!clientRepository.existsClientByEmail(email)) {
            logClientNotFoundByEmail(email);
            throw new ClientErrorException("Nenhum cliente cadastrado com esse email.");
        }
    }


    private String validateAndNormalizeCpf(String cpf) {
        String validatedCpf = denormalizeCpf(cpf);
        validateCpf(validatedCpf);

        return validatedCpf;
    }

    private void validateCpf(String cpf) {
        logClientCpfValidation(cpf);

        if (isBlank(cpf)) {
            throw new ClientErrorException("O CPF do cliente é obrigatório.");
        }
        if (cpf.length() != 11) {
            throw new ClientErrorException("O CPF do cliente deve conter 11 digitos.");
        }
        if (!isValidCPF(cpf)) {
            throw new ClientErrorException("O CPF do cliente é inválido.");
        }
    }

    private void cpfExists(String cpf) {
        if (clientRepository.existsClientByCpf(cpf)) {
            logClientCpfAlreadyExists(cpf);
            throw new ClientErrorException("O CPF do cliente já está cadastrado.");
        }
    }

    private void cpfNotExist(String cpf) {
        if (!clientRepository.existsClientByCpf(cpf)) {
            logClientNotFoundByCpf(cpf);
            throw new ClientErrorException("Nenhum cliente cadastrado com esse CPF.");
        }
    }

}