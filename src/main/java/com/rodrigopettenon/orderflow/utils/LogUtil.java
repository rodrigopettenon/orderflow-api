package com.rodrigopettenon.orderflow.utils;

import com.rodrigopettenon.orderflow.models.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class LogUtil {

    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    //LOGS DE VALIDAÇÕES

    //Genérico
    private static void logValidation(String field, Object value) {
        logger.info("[VALIDAÇÃO] Validando {}: {} ", field, value);
    }

    private static void logFilterValidation(String action, Object value) {
        logger.info("[VALIDAÇÃO - FILTRO] Validando filtro {}: {}",action, value);
    }


    //Client
    public static void logClientNameFilterValidation(String name) {
        logFilterValidation("nome", name);
    }

    public static void logClientEmailFilterValidation(String email) {
        logFilterValidation("email", email);
    }

    public static void logClientCpfFilterValidation(String cpf) {
        logFilterValidation("CPF",cpf);
    }

    public static void logClientBirthStartFilterValidation(LocalDate birthStart) {
        logFilterValidation("data de nascimento ínicio", birthStart);
    }

    public static void logClientBirthEndFilterValidation(LocalDate birthEnd) {
        logFilterValidation("data de nascimento final", birthEnd);
    }

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
    public static void logProductSkuFilterValidation(String sku) {
        logFilterValidation("SKU",sku);
    }

    public static void logProductMinPriceFilterValidation(Double minPrice) {
        logFilterValidation("preço minimo", minPrice);
    }

    public static void logProductMaxPriceFilterValidation(Double maxPrice) {
        logFilterValidation("preço máximo", maxPrice);
    }

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

    //Orders
    public static void logOrderCurrentStatusValidation(String status) {
        logValidation("status atual do pedido", status);
    }

    public static void logOrderNewStatusValidation(OrderStatus status) {
        logValidation("novo status do pedido", status.toString());
    }

    public static void logOrderClientIdValidation(Long id) {
        logValidation("id do cliente do pedido", id);
    }

    public static void logOrderStatusValidation(String status) {
        logValidation("status do pedido", status);
    }

    public static void logOrderIdValidation(UUID id) {
        logValidation("id do pedido", id);
    }

    public static void logFilterOrderIdValidation(UUID id) {
        logFilterValidation("id do pedido", id);
    }

    public static void logFilterOrderClientIdValidation(Long id) {
        logFilterValidation("id do cliente do pedido", id);
    }

    public static void logFilterOrderDateTimeStartValidation(LocalDateTime dateTimeStart) {
        logFilterValidation("data de inicio do pedido", dateTimeStart);
    }

    public static void logFilterOrderDateTimeEndValidation(LocalDateTime dateTimeEnd) {
        logFilterValidation("data final do pedido", dateTimeEnd);
    }

    public static void logFilterOrderStatusValidation(String status) {
        logFilterValidation("status do pedido", status);
    }


    //ItemOrder
    public static void logFilterItemOrderMinQuantityValidation(Integer minQuantity) {
        logFilterValidation("quantidade mínima de item de pedido", minQuantity);
    }

    public static void logFilterItemOrderMaxQuantityValidation(Integer maxQuantity) {
        logFilterValidation("quantidade máxima de item de pedido", maxQuantity);
    }

    public static void logItemOrderOrderIdValidation(UUID orderId) {
        logValidation("id do pedido no item", orderId);
    }

    public static void logItemOrderProductIdValidation(UUID productId) {
        logValidation("id do produto no item", productId);
    }

    public static void logItemOrderQuantityValidation(Integer quantity) {
        logValidation("quantidade do item de pedido", quantity);
    }

    public static void logFilterItemOrderIdValidation(UUID id) {
        logFilterValidation("id do item de pedido", id);
    }

    public static void logFilterItemOrderProductIdValidation(UUID productId) {
        logFilterValidation("id do produto no item de pedido", productId);
    }

    public static void logFilterItemOrderOrderIdValidation(UUID orderId) {
        logFilterValidation("id do pedido no item de pedido", orderId);
    }

    public static void logFilterItemOrderClientIdValidation(Long clientId) {
        logFilterValidation("id do cliente do item de pedido", clientId);
    }


    // LOGS DE INICIOS

    //Genérico
    private static void logStartOfProcess(String process, Object value) {
        logger.info("[INÍCIO] Iniciando processo de {}: {}", process, value);
    }

    public static void logSaveItemOrderStart(UUID orderId, UUID productId) {
        logger.info("[INÍCIO] Iniciando salvamento de item de pedido: orderId={}, productId={}", orderId, productId);
    }



    //Clients
    public static void logSaveClientWithCpfStart(String cpf) {
        logStartOfProcess("salvamento de um novo cliente com CPF", cpf);
    }

    public static void logFindAllClientsStart() {
        logStartOfProcess("busca de todos clientes", null);
    }

    public static void logGettingAllClientListStart() {
        logStartOfProcess("obtenção da lista de clientes", null);
    }

    public static void logCountOfAllClientsInListStart() {
        logStartOfProcess("contagem de todos clientes da lista", null);
    }

    public static void logFindFilteredClientsStart() {
        logStartOfProcess("busca de clientes filtrados", null);
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

    //Orders
    public static void logSaveOrderStart() {
        logStartOfProcess("salvamento de um novo pedido", null);
    }

    public static void logFindFilteredOrdersStart() {
        logStartOfProcess("busca de pedidos filtrados", null);
    }

    public static void logQueryFindFilteredOrdersStart() {
        logStartOfProcess("query de busca de pedidos filtrados", null);
    }

    public static void logQueryCountFilteredOrdersStart() {
        logStartOfProcess("query de contagem de pedidos filtrados", null);
    }

    public static void logQueryFindFilteredOrdersDetailsStart() {
        logStartOfProcess("query de busca de pedidos filtrados com detalhes", null);
    }

    public static void logQueryCountFilteredOrdersDetailsStart() {
        logStartOfProcess("query de contagem de pedidos filtrados com detalhes", null);
    }

    public static void logFindOrderByIdStart(UUID id) {
        logStartOfProcess("busca de pedido pelo id", id);
    }

    public static void logFindFilteredOrderDetailsStart() {
        logStartOfProcess("busca de pediddos filtrados com detalhes", null);
    }

    public static void logCheckExistenceOfOrderByIdStart(UUID id) {
        logStartOfProcess("checagem de existência do pedido pelo id", id);
    }

    public static void logCheckExistenceOfOrderByClientIdStart(Long clientId) {
        logStartOfProcess("checagem de existência do pedido pelo id do cliente", clientId);
    }

    public static void logFindFilteredRelevantOrderDataStart() {
        logStartOfProcess("busca de pedidos com dados relevantes", null);
    }

    //ItemOrder
    public static void logFindFilteredItemOrdersStart() {
        logStartOfProcess("busca de itens de pedido com filtros", null);
    }

    public static void logFindFullDetailsItemOrdersStart() {
        logStartOfProcess("busca de itens de pedido com detalhes", null);
    }


    //LOGS DE INFO

    private static void logInfoClients(String action, Object name, Object email, Object cpf, LocalDate birthStart, LocalDate birthEnd) {
        logger.info("[INFO] Iniciando busca de {}: name={}, email={}, cpf={}, birthStart={}, birthEnd={}", action, name, email, cpf, birthStart, birthEnd);
    }

    private static void logInfoProducts(String action, Object name, Object sku, Object minPrice, Object maxPrice) {
        logger.info("[INFO] Iniciando {}: name={}, sku={}, minPrice={}, maxPrice={}", action, name, sku, minPrice, maxPrice);
    }

    //Clients
    public static void logInfoStartingClientsSearchQueryFiltered(String name, String email, String cpf, LocalDate birthStart, LocalDate birthEnd) {
        logInfoClients("busca de clientes com filtros", name, email, cpf, birthStart, birthEnd);
    }

    public static void logInfoStartingFilteredClientCountQuery(String name, String email, String cpf, LocalDate birthStart, LocalDate birthEnd) {
        logInfoClients("contagem de clientes com filtros", name, email, cpf, birthStart, birthEnd);
    }

    //Products
    public static void logInfoStartingProductsSearchQueryFiltered(String name, String sku, Double minPrice, Double maxPrice) {
        logInfoProducts("busca de produtos com filtros", name, sku, minPrice, maxPrice);
    }

    public static void logInfoStartingFilteredProductsCountQuery(String name, String sku, Double minPrice, Double maxPrice) {
        logInfoProducts("contagem de produtos com filtros", name, sku, minPrice, maxPrice);
    }

    //LOGS DE SUCESSOS

    //Genérico

    private static void logSuccessfully(String action, Object value) {
        logger.info("[SUCESSO] Sucesso ao {}: {}", action, value );
    }

    private static void logSuccessfully(String action, String name, String email, String cpf, LocalDate birthStart, LocalDate birthEnd) {
        logger.info("[SUCESSO] Sucesso ao {}: name={}, email={}, cpf={}, birthStart={}, birthEnd={}",action, name, email, cpf, birthStart, birthEnd);
    }

    private static void logSuccessfully(String action, String name, String sku, Double minPrice, Double maxPrice) {
        logger.info("[SUCESSO] Sucesso ao {}: name={}, sku={}, minPrice={}, maxPrice={}",action, name, sku, minPrice, maxPrice);
    }

    private static void logSuccessfully(String action, UUID id, Long clientId, LocalDateTime orderDate, String status) {
        logger.info("[SUCESSO] Sucesso ao {}: id={}, clientId={}, orderDate={}, status={}", action, id, clientId, orderDate, status);
    }

    public static void logSuccessfully(String action, UUID orderId, UUID productId) {
        logger.info("[SUCESSO] Sucesso ao {}: orderId={}, productId={}",action, orderId, productId);
    }


    //Clients
    public static void logFindFilteredClientsSuccessfully(String name, String email, String cpf, LocalDate birthStart, LocalDate birthEnd) {
        logSuccessfully("buscar por clientes filtrados", name, email, cpf, birthStart, birthEnd);
    }

    public static void logClientSavedWithCpfSuccessfully(String cpf) {
        logSuccessfully("salvar cliente de CPF", cpf);
    }

    public static void logFindAllClientsSuccessfully() {
        logSuccessfully("buscar todos os clientes", null);
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

    //Orders
    public static void logUpdateOrderStatusByIdSuccessfully(UUID id) {
        logSuccessfully("atualizar status do pedido pelo id", id);
    }

    public static void logSaveOrderSuccessfully(UUID id, Long clientId, LocalDateTime orderDate, String status) {
        logSuccessfully("ao salvar um novo pedido", id, clientId, orderDate, status);
    }

    public static void logFindOrderByIdSuccessfully(UUID id) {
        logSuccessfully("encontrar pedido pelo ID", id);
    }

    public static void logCheckingExistenceOfOrderByIdSuccessfully(UUID id) {
        logSuccessfully("verificar existencia do pedido pelo id", id);
    }

    public static void logFindFilteredOrdersSuccessfully() {
        logSuccessfully("buscar por pedidos filtrados", null);
    }

    public static void logCountFilteredOrdersSuccessfully() {
        logSuccessfully("contar pedidos filtrados", null);
    }


    //Products
    public static void logFindFilteredProductsSuccessfully(String name, String sku, Double minPrice, Double maxPrice) {
        logSuccessfully("buscar por produtos filtrados", name, sku, minPrice, maxPrice);
    }

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


    //ItemOrder
    public static void logSaveItemOrderSuccessfully(UUID orderId, UUID productId) {
        logSuccessfully("salvar item do pedido", orderId, productId);
    }

    public static void logFindFilteredItemOrdersSuccessfully() {
        logSuccessfully("buscar por itens de pedido com filtros", null);
    }

    public static void logFindFullDetailsItemOrdersSuccessfully() {
        logSuccessfully("buscar por itens de pedido com detalhes", null);
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

    //Orders
    public static void logOrderNotFoundById(UUID id) {
        logFailed("O id do pedido não foi encontrado", id);
    }

    public static void logClientNotFoundByOrderId(UUID orderId) {
        logFailed("O cliente vinculado ao pedido não foi encontrado", orderId);
    }

    //ItemOrder
    public static void logItemOrderNotFoundById(UUID id) {
        logFailed("O id do item de pedido não foi encontrado", id);
    }

    public static void logItemOrderNotFoundByOrderId(UUID orderId) {
        logFailed("Nenhum item de pedido encontrado com o ID do pedido", orderId);
    }

    public static void logItemOrderNotFoundByProductId(UUID productId) {
        logFailed("Nenhum item de pedido encontrado com o ID do produto", productId);
    }

    public static void logItemOrderNotFoundByClientId(Long clientId) {
        logFailed("Nenhum item de pedido encontrado com o ID do cliente", clientId);
    }



    //LOGS DE ERROS

    //Genérico
    private static void logUnexpectedError(String action, Object identifier, Exception e) {
        logger.error("[ERRO] Erro inesperado ao {} {}: {}", action, identifier, e.getMessage(), e);
    }


    //Clients


    public static void logUnexpectedErrorOnFindAllClientsOrderBy(String orderBy, Exception e) {
        logUnexpectedError("buscar todos os clientes ordenados por", orderBy, e);
    }

    public static void logUnexpectedErrorOnCountAllClientsInList(Exception e) {
        logUnexpectedError("realizar contagem de todos clientes na lista", null, e);
    }

    public static void logUnexpectedErrorOnFindFilteredClients(Exception e) {
        logUnexpectedError("buscar por clientes filtrados", null, e);
    }

    public static void logUnexpectedErrorOnCountFilteredClients(Exception e) {
        logUnexpectedError("contar clientes filtrados", null, e);
    }

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
    public static void logUnexpectedErrorOnCountFilteredProducts(Exception e) {
        logUnexpectedError("contar produtos filtrados", null, e);
    }
    public static void logUnexpectedErrorOnFindFilteredProducts(Exception e) {
        logUnexpectedError("buscar produtos filtrados", null, e);
    }

    public static void logUnexpectedErrorOnFindAllProducts(Exception e) {
        logUnexpectedError("buscar todos produtos", null, e);
    }

    public static void logUnexpectedErrorOnCountAllProducts(Exception e) {
        logUnexpectedError("contar todos produtos",null, e);
    }
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

    //Orders
    public static void logUnexpectedErrorOnSaveOrder(Exception e) {
        logUnexpectedError("salvar um novo pedido",null, e);
    }

    public static void logUnexpectedErrorOnUpdateOrderStatusById(UUID id, Exception e) {
        logUnexpectedError("atualizar um pedido pelo ID", id, e);
    }

    public static void  logUnexpectedErrorOnFindOrderById(UUID id, Exception e) {
        logUnexpectedError("buscar um pedido pelo ID", id, e);
    }

    public static void logUnexpectedErrorCheckingExistenceOfOrderById(UUID id, Exception e) {
        logUnexpectedError("verificar existência do pedido pelo ID", id, e);
    }

    public static void logUnexpectedErrorCheckingExistenceOfOrderByClientId(Long id, Exception e) {
        logUnexpectedError("verificar existência do pedido pelo ID do cliente", id, e);
    }

    public static void logUnexpectedErrorOnFindFilteredOrders(Exception e) {
        logUnexpectedError("buscar pedidos filtrados", null, e);
    }

    public static void logUnexpectedErrorOnCountFilteredOrders(Exception e) {
        logUnexpectedError("contar pedidos filtrados", null, e);
    }

    public static void logUnexpectedErrorOnFindFilteredOrdersDetails(Exception e) {
        logUnexpectedError("buscar pedidos filtrados com detalhes", null, e);
    }

    public static void logUnexpectedErrorOnCountFilteredOrdersDetails(Exception e) {
        logUnexpectedError("contar pedidos filtrados com detalhes", null, e);
    }

    public static void logUnexpectedErrorOnFindOrderModelById(UUID id, Exception e) {
        logUnexpectedError("buscar o pedido (OrderModel) pelo id", id, e);
    }

    //ItemOrder
    public static void logUnexpectedErrorOnSaveItemOrder(UUID orderId, Exception e) {
        logUnexpectedError("salvar item de pedido", orderId, e);
    }

    public static void logUnexpectedErrorOnFindFilteredItemOrders(Exception e) {
        logUnexpectedError("buscar itens de pedido com filtros", null, e);
    }

    public static void logUnexpectedErrorOnFindFullDetailsItemOrders(Exception e) {
        logUnexpectedError("buscar itens de pedido com detalhes", null, e);
    }

    public static void logUnexpectedErrorCheckingExistenceOfItemOrderByOrderId(UUID orderId, Exception e) {
        logUnexpectedError("verificar existência de item de pedido pelo ID do pedido", orderId, e);
    }

    public static void logUnexpectedErrorCheckingExistenceOfItemOrderByProductId(UUID productId, Exception e) {
        logUnexpectedError("verificar existência de item de pedido pelo ID do produto", productId, e);
    }

    public static void logUnexpectedErrorCheckingExistenceOfItemOrderByClientId(Long clientId, Exception e) {
        logUnexpectedError("verificar existência de item de pedido pelo ID do cliente", clientId, e);
    }
}
