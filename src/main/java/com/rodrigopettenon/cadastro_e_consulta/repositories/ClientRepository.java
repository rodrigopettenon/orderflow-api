package com.rodrigopettenon.cadastro_e_consulta.repositories;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ClientDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.ClientPageDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rodrigopettenon.cadastro_e_consulta.utils.LogUtil.*;
import static java.util.Objects.isNull;

@Repository
public class ClientRepository {

    @PersistenceContext
    private EntityManager em;


    public void save(ClientDto clientDto) {
        try{
            StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO tb_clients (name, email, cpf, birth_date) ");
            sql.append(" VALUES (:name, :email, :cpf, :birth) ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("name", clientDto.getName())
                    .setParameter("email", clientDto.getEmail())
                    .setParameter("cpf", clientDto.getCpf())
                    .setParameter("birth", clientDto.getBirth());

            query.executeUpdate();
        }catch (Exception e) {
            logUnexpectedErrorOnSaveClientWithCpf(clientDto.getCpf(), e);
            throw new ClientErrorException("Erro ao cadastrar cliente.");
        }
    }

    public boolean existsByCpf(String cpf) {
        try{
            String sql = (" SELECT 1 FROM tb_clients WHERE cpf = :cpf LIMIT 1");

            Query query = em.createNativeQuery(sql)
                    .setParameter("cpf", cpf);

            List<?> result = query.getResultList();
            return !result.isEmpty(); // Se a lista estiver vazia retornará false.

        }catch (Exception e){
            logUnexpectedErrorCheckingClientExistenceByCpf(cpf, e);
            throw new ClientErrorException("Erro ao verificar existência do CPF. ");
        }
    }

    public boolean existsByEmail(String email) {
        try {
            String sql = (" SELECT 1 FROM tb_clients WHERE email = :email LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("email", email);

            List<?> result = query.getResultList();

            return !result.isEmpty(); // Se a lista estiver vazia retornará false.
        }catch (Exception e) {
            logUnexpectedErrorCheckingClientExistenceByEmail(email, e);
            throw new ClientErrorException("Erro ao verificar existência do email.");
        }
    }

    public List<ClientDto> findAllClients(Integer page, Integer linesPerPage, String direction, String orderBy) {
        try {
            logGettingAllClientListStart();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT name, email, cpf, birth_date FROM tb_clients ");
            sql.append(" ORDER BY " + orderBy + " " + direction + " ");
            sql.append(" LIMIT :limit OFFSET :offset ");


            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            List<Object[]> results = query.getResultList();
            List<ClientDto> clientList = new ArrayList<>();

            for (Object[] result : results) {
                ClientDto clientDto = new ClientDto();
                clientDto.setName((String) result[0]);
                clientDto.setEmail((String) result[1]);
                clientDto.setCpf((String) result[2]);
                clientDto.setBirth(((Date) result[3]).toLocalDate());

                clientList.add(clientDto);
            }

            logFindAllClientsStart();
            return clientList;
        } catch (Exception e) {
            logUnexpectedErrorOnFindAllClientsOrderBy(orderBy, e);
            throw new ClientErrorException("Erro ao buscar clientes.");
        }
    }

    public ClientDto findByEmail(String email) {
        try {
            String sql = (" SELECT * FROM tb_clients WHERE email = :email LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("email", email);

            List<Object[]> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                throw new ClientErrorException("Cliente não encontrado pelo email.");
            }

            Object[] result = resultList.get(0);
            ClientDto clientFound = new ClientDto();
            clientFound.setId(((Number) result[0]).longValue());
            clientFound.setName((String) result[1]);
            clientFound.setEmail((String) result[2]);
            clientFound.setCpf((String) result[3]);
            clientFound.setBirth(((Date) result[4]).toLocalDate());

            logFoundClientByEmailSuccessfully(email);
            return clientFound;
        } catch (Exception e) {
            logUnexpectedErrorOnFindClientByEmail(email, e);
            throw new ClientErrorException("Erro ao buscar cliente pelo email.");
        }
    }

    public ClientDto findByCpf(String cpf) {
        try{
            String sql = " SELECT * FROM tb_clients WHERE cpf = :cpf LIMIT 1 ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("cpf", cpf);

            List<Object[]> resultList = query.getResultList();
            if (resultList.isEmpty()) {
                throw new ClientErrorException("Cliente não encontrado pelo CPF.");
            }

            Object[] result = resultList.get(0);
            ClientDto clientFound = new ClientDto();
            clientFound.setId(((Number) result[0]).longValue());
            clientFound.setName((String) result[1]);
            clientFound.setEmail((String) result[2]);
            clientFound.setCpf((String) result[3]);
            clientFound.setBirth(((Date) result[4]).toLocalDate());

            logFoundClientByCpfSuccessfully(cpf);
            return clientFound;
        } catch (Exception e) {
            logUnexpectedErrorOnFindClientByCpf(cpf, e);
            throw new ClientErrorException("Erro ao buscar cliente pelo CPF.");
        }
    }

    public ClientDto updateByCpf(String cpf, ClientDto clientDto) {
        try{
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE tb_clients SET name = :name, email = :email, birth_date = :birth ");
            sql.append(" WHERE cpf = :cpf LIMIT 1 ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("name", clientDto.getName())
                    .setParameter("email", clientDto.getEmail())
                    .setParameter("birth", clientDto.getBirth())
                    .setParameter("cpf", cpf);

            query.executeUpdate();

            ClientDto updatedClientDto = new ClientDto();
            updatedClientDto.setName(clientDto.getName());
            updatedClientDto.setEmail(clientDto.getEmail());
            updatedClientDto.setBirth(clientDto.getBirth());

            logClientUpdatedByCpfSuccessfully(cpf);
            return updatedClientDto;
        } catch (Exception e) {
            logUnexpectedErrorOnUpdateClientByCpf(cpf, e);
            throw new ClientErrorException("Erro ao realizar atualização cadastral do cliente.");
        }

    }

    public void deleteByCpf(String cpf) {
        try{
            String sql = " DELETE FROM tb_clients WHERE cpf = :cpf ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("cpf", cpf);

            query.executeUpdate();

        } catch (Exception e) {
            logUnexpectedErrorOnDeleteClientByCpf(cpf, e);
            throw new ClientErrorException("Erro ao realizar a deleção do cliente pelo cpf.");
        }
    }

    public Long countTotalClients() {
        try {
            logCountOfAllClientsInListStart();
            String sql = "SELECT COUNT(*) FROM tb_clients";

            Query query = em.createNativeQuery(sql);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();
        } catch (Exception e) {
            logUnexpectedErrorOnCountAllClientsInList(e);
            throw new ClientErrorException("Erro ao contar total de clientes.");
        }
    }


    public ClientPageDto findFilteredClients(String validatedNameFilter, String validatedEmailFilter,
                                               String validatedCpfFilter, LocalDate birthStart, LocalDate birthEnd,
                                               Integer page, Integer linesPerPage,
                                               String fixedDirection, String fixedOrderBy) {
        try{

            Map<String, Object> parameters = new HashMap<>();

            StringBuilder sqlClients = buildClientQuery(parameters, validatedNameFilter,
                    validatedEmailFilter, validatedCpfFilter,
                    birthStart, birthEnd, fixedDirection, fixedOrderBy);

            StringBuilder sqlTotal = buildCountQuery(parameters, validatedNameFilter,
                    validatedEmailFilter, validatedCpfFilter,
                    birthStart, birthEnd);

            Query queryClients = em.createNativeQuery(sqlClients.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            Query queryTotal = em.createNativeQuery(sqlTotal.toString());

            setQueryParameters(queryClients, parameters);
            setQueryParameters(queryTotal, parameters);

            logInfoStartingClientsSearchQueryFiltered(validatedNameFilter, validatedEmailFilter, validatedCpfFilter, birthStart, birthEnd);
            List<Object[]> clientResults = queryClients.getResultList();
            List<ClientDto> clients = new ArrayList<>();

            logInfoStartingFilteredClientCountQuery(validatedNameFilter, validatedEmailFilter, validatedCpfFilter, birthStart, birthEnd);
            Object totalResult = queryTotal.getSingleResult();
            Number total = (Number) totalResult;

            for (Object[] result : clientResults) {
                ClientDto clientDto = new ClientDto();
                clientDto.setName((String) result[0]);
                clientDto.setEmail((String) result[1]);
                clientDto.setCpf((String) result[2]);
                clientDto.setBirth(((Date) result[3]).toLocalDate());

                clients.add(clientDto);
            }

            ClientPageDto clientPageDto = new ClientPageDto();
            clientPageDto.setClients(clients);
            clientPageDto.setTotal(total.longValue());

            logFindFilteredClientsSuccessfully(validatedNameFilter, validatedEmailFilter, validatedCpfFilter, birthStart, birthEnd);
            return clientPageDto;

        } catch (Exception e) {
            logUnexpectedErrorOnFindFilteredClients(e);
            throw new ClientErrorException("Erro ao buscar clientes filtrados.");
        }
    }

    private void setQueryParameters (Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }

    private StringBuilder buildClientQuery(Map<String, Object> parameters, String name,
                                          String email, String cpf, LocalDate birthStart,
                                          LocalDate birthEnd, String direction, String orderBy) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT name, email, cpf, birth_date FROM tb_clients WHERE 1=1 ");

        if (!isNull(name)) {
            sql.append(" AND name LIKE :name ");
            parameters.put("name", "%" + name + "%");
        }

        if (!isNull(email)) {
            sql.append(" AND email LIKE :email ");
            parameters.put("email", "%" + email + "%");
        }

        if (!isNull(cpf)) {
            sql.append(" AND cpf = :cpf ");
            parameters.put("cpf", cpf);
        }
        if (!isNull(birthStart) || !isNull(birthEnd)) {
            sql.append(" AND birth_date BETWEEN :birthStart AND :birthEnd ");
            parameters.put("birthStart", birthStart);
            parameters.put("birthEnd", birthEnd);
        }

        sql.append(" ORDER BY " + orderBy + " " + direction + " ");
        sql.append(" LIMIT :limit OFFSET :offset ");

        return sql;
    }

    private StringBuilder buildCountQuery (Map<String, Object> parameters, String name,
                                          String email, String cpf, LocalDate birthStart,
                                          LocalDate birthEnd) {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT COUNT(*) FROM tb_clients WHERE 1=1 ");

        if (!isNull(name)) {
            sql.append(" AND name LIKE :name ");
            parameters.put("name", "%" + name + "%");
        }

        if (!isNull(email)) {
            sql.append(" AND email LIKE :email ");
            parameters.put("email", "%" + email + "%");
        }

        if (!isNull(cpf)) {
            sql.append(" AND cpf = :cpf ");
            parameters.put("cpf", cpf);
        }
        if (!isNull(birthStart) || !isNull(birthEnd)) {
            sql.append(" AND birth_date BETWEEN :birthStart AND :birthEnd ");
            parameters.put("birthStart", birthStart);
            parameters.put("birthEnd", birthEnd);
        }

        return sql;

    }
}
