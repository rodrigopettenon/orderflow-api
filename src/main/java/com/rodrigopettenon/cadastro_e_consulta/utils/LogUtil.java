package com.rodrigopettenon.cadastro_e_consulta.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    //LOGS DE VALIDAÇÕES

    //Genérico
    private static void logValidation(String field, Object value) {
        logger.info("[VALIDAÇÃO] Validando {}: {} ", field, value);
    }


    //Client
    public static void logClientNameValidation(String name) {
        logValidation("nome", name);
    }

    public static void logClientCpfValidation(String cpf) {
        logValidation("CPF", cpf);
    }

    public static void logClientEmailValidation(String email) {
        logValidation("email", email);
    }

    public static void logClientBirthValidation(LocalDate birth) {
        logValidation("data de nascimento", birth);
    }


    //Product
    public static void logProductNameValidation(String name) {
        logValidation("nome", name);
    }

    public static void logProductSkuValidation(String sku) {
        logValidation("SKU", sku);
    }

    public static void logProductPriceValidation(Double price) {
        logValidation("preço", price);
    }

    public static void logProductExpirationValidation(LocalDate expiration) {
        logValidation("data de vencimento", expiration);
    }


    // LOGS DE INICIOS

    //Genérico
    private static void logStartOfProcess(String process, Object value) {
        logger.info("[INÍCIO] Iniciando processo de {}: {}", process, value);
    }


    //Clients
    public static void logSaveClientWithCpfStart(String cpf) {
        logStartOfProcess("salvamento de um novo cliente com CPF", cpf);
    }

    public static void logFindClientByCpfStart(String cpf) {
        logStartOfProcess("pesquisa do cliente com CPF", cpf);
    }

    public static void logFindClientByEmailStart(String email) {
        logStartOfProcess("pesquisa do cliente com email", email);
    }

    public static void logClientUpdateByCpfStart(String cpf) {
        logStartOfProcess("atualização do cliente pelo CPF", cpf);
    }

    public static void logClientDeletionByCpfStart(String cpf) {
        logStartOfProcess("deleção do cliente pelo CPF", cpf);
    }


    //Products
    public static void logSaveProductWithSkuStart(String sku) {
        logStartOfProcess("salvamento de um novo produto com SKU", sku);
    }

    public static void logFindProductBySkuStart(String sku) {
        logStartOfProcess("pesquisa do produto pelo SKU", sku);
    }

    public static void logProductUpdateBySkuStart(String sku) {
        logStartOfProcess("atualização do produto pelo SKU", sku);
    }

    public static void logProductDeletionBySkuStart(String sku) {
        logStartOfProcess("deleção do produto pelo SKU", sku);
    }


    //LOGS DE SUCESSOS

    //Genérico
    private static void logSuccessfully(String action, Object value) {
        logger.info("[SUCESSO] Sucesso ao {}: {}", action, value );
    }


    //Clients
    public static void logClientSavedWithCpfSuccessfully(String cpf) {
        logSuccessfully("salvar cliente de CPF", cpf);
    }

    public static void logFoundClientByCpfSuccessfully(String cpf) {
        logSuccessfully("encontrar cliente pelo CPF", cpf);
    }

    public static void logFoundClientByEmailSuccessfully(String email) {
        logSuccessfully("encontrar cliente pelo email", email);
    }

    public static void logClientUpdatedByCpfSuccessfully(String cpf) {
        logSuccessfully("atualizar cliente pelo CPF", cpf);
    }

    public static void logClientDeletedByCpfSuccessfully(String cpf) {
        logSuccessfully("deletar cliente pelo CPF", cpf);
    }


    //Products
    public static void logProductSavedWithSkuSuccessfully(String sku) {
        logSuccessfully("salvar produto de SKU", sku);
    }

    public static void logProductDeletedBySkuSuccessfully(String sku) {
        logSuccessfully("deletar produto pelo SKU", sku);
    }

    public static void logFoundProductBySkuSuccessfully(String sku) {
        logSuccessfully("encontrar produto pelo SKU", sku);
    }

    public static void logProductUpdatedBySkuSuccessfully(String sku) {
        logSuccessfully("atualizar produto pelo SKU", sku);
    }


    // LOGS DE FALHAS

    //Genérico
    private static void logFailed(String reason, Object value) {
        logger.warn("[FALHA] {}: {}", reason, value);
    }


    //Clients
    public static void logClientEmailAlreadyExists(String email) {
        logFailed("O email do cliente já está cadastrado", email);
    }

    public static void logClientCpfAlreadyExists(String cpf) {
        logFailed("O CPF do cliente já está cadastrado", cpf);
    }

    public static void logClientNotFoundByCpf(String cpf) {
        logFailed("O CPF do cliente não foi encontrado", cpf);
    }

    public static void logClientNotFoundByEmail(String email) {
        logFailed("O email do cliente não foi encontrado", email);
    }


    //Products
    public static void logProductSkuAlreadyExists(String sku) {
        logFailed("O SKU do produto já está cadastrado", sku);
    }

    public static void logProductNotFoundBySku(String sku) {
        logFailed("O SKU do produto não foi encontrado", sku);
    }


    //LOGS DE ERROS

    //Genérico
    private static void logUnexpectedError(String action, String identifier, Exception e) {
        logger.error("[ERRO] Erro inesperado ao {} {}: {}", action, identifier, e.getMessage(), e);
    }


    //Clients
    public static void logUnexpectedErrorOnFindClientByCpf(String cpf, Exception e) {
        logUnexpectedError("buscar cliente pelo CPF", cpf, e);
    }

    public static void logUnexpectedErrorOnFindClientByEmail(String email, Exception e) {
        logUnexpectedError("buscar cliente pelo Email", email, e);
    }

    public static void logUnexpectedErrorOnSaveClientWithCpf(String cpf, Exception e) {
        logUnexpectedError("salvar cliente de CPF", cpf, e);
    }

    public static void logUnexpectedErrorCheckingClientExistenceByCpf(String cpf, Exception e) {
        logUnexpectedError("verificar existência pelo CPF", cpf, e);
    }

    public static void logUnexpectedErrorCheckingClientExistenceByEmail(String email, Exception e) {
        logUnexpectedError("verificar existência pelo Email", email, e);
    }

    public static void logUnexpectedErrorOnUpdateClientByCpf(String cpf, Exception e) {
        logUnexpectedError("realizar atualização do cliente pelo CPF", cpf, e);
    }

    public static void logUnexpectedErrorOnDeleteClientByCpf(String cpf, Exception e) {
        logUnexpectedError("realizar deleção do cliente pelo CPF", cpf, e);
    }


    //Products
    public static void logUnexpectedErrorOnSaveClientWithSku(String sku, Exception e) {
        logUnexpectedError("salvar produto de SKU", sku, e);
    }

    public static void logUnexpectedErrorCheckingProductExistenceBySku(String sku, Exception e) {
        logUnexpectedError("verificar existencia pelo SKU", sku, e);
    }

    public static void logUnexpectedErrorOnFindProductBySku(String sku, Exception e) {
        logUnexpectedError("buscar produto pelo SKU", sku, e);
    }
    public static void logUnexpectedErrorOnUpdateProductBySku(String sku, Exception e ) {
        logUnexpectedError("realizar atualização do produto pelo SKU", sku, e);
    }

    public static void logUnexpectedErrorOnDeleteProductBySku(String sku, Exception e ) {
        logUnexpectedError("realizar deleção do produto pelo SKU", sku, e);
    }
}
