package com.rodrigopettenon.orderflow.services;

import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ProductDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.rodrigopettenon.orderflow.utils.LogUtil.*;
import static com.rodrigopettenon.orderflow.utils.StringsValidation.isAlphanumeric;
import static com.rodrigopettenon.orderflow.utils.StringsValidation.normalizeSpaces;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class ProductService {

    private static final List<String> ALLOWED_DIRECTION = Arrays.asList("asc", "desc");
    private static final List<String> ALLOWED_ORDER_BY = Arrays.asList("name", "sku", "price", "expiration_date");

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void saveProduct(ProductDto productDto) {
        logSaveProductWithSkuStart(productDto.getSku());

        validateDtoData(productDto);
        validateSku(productDto.getSku());
        skuExists(productDto.getSku());

        productRepository.saveProduct(productDto);
        logProductSavedWithSkuSuccessfully(productDto.getSku());
    }

    @Transactional(readOnly = true)
    public GlobalPageDto<ProductDto> findAllProducts(Integer page, Integer linesPerPage, String direction, String orderBy) {

        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(linesPerPage);

        String fixedDirection = fixDirection(direction);
        String fixedOrderBy = fixOrderBy(orderBy);

        Long total = productRepository.countAllProducts();
        List<ProductDto> products = productRepository.findAllProducts(sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);

        GlobalPageDto<ProductDto> productPageDto = new GlobalPageDto<>();
        productPageDto.setTotal(total);
        productPageDto.setItems(products);

        return productPageDto;
    }

    @Transactional(readOnly = true)
    public GlobalPageDto<ProductDto> findFilteredProducts(String name, String sku, Double minPrice,
                                       Double maxPrice, Integer page, Integer linesPerPage,
                                       String direction, String orderBy) {
        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(linesPerPage);

        String fixedDirection = fixDirection(direction);
        String fixedOrderBy = fixOrderBy(orderBy);

        String sanitizedName = sanitizeNameFilter(name);
        String sanitizedSku = validateSkuFilter(sku);
        validateMinPriceFilter(minPrice, maxPrice);
        validateMaxPriceFilter(maxPrice, minPrice);



        return  productRepository.findFilteredProducts(sanitizedName, sanitizedSku, minPrice, maxPrice, sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    @Transactional(readOnly = true)
    public ProductDto findBySku(String sku) {
        logFindProductBySkuStart(sku);

        validateSku(sku);
        skuNotExist(sku);

        return productRepository.findProductBySku(sku);
    }

    @Transactional
    public ProductDto updateBySku(String sku, ProductDto productDto) {
        logProductUpdateBySkuStart(sku);

        validateDtoData(productDto);
        validateSku(sku);
        skuNotExist(sku);

        return productRepository.updateProductBySku(sku, productDto);
    }

    @Transactional
    public void deleteBySku(String sku) {
        logProductDeletionBySkuStart(sku);

        validateSku(sku);
        skuNotExist(sku);

        logProductDeletedBySkuSuccessfully(sku);
        productRepository.deleteProductBySku(sku);
    }

    // Validações filtros GET

    private String sanitizeNameFilter(String name) {
        String nameWithoutDuplicateSpaces =  normalizeSpaces(name);
        if (isBlank(name)) {
            return null;
        }
        return nameWithoutDuplicateSpaces;
    }

    private String validateSkuFilter(String sku) {
        logProductSkuFilterValidation(sku);
        if (isBlank(sku)) {
            return null;
        }
        if (sku.length() != 8) {
            throw new ClientErrorException("O SKU do produto deve ter 8 caractéres.");
        }
        if (!isAlphanumeric(sku)) {
            throw new ClientErrorException("O SKU do produto deve ser alfanumérico.");
        }
        return sku;
    }

    private void validateMinPriceFilter(Double minPrice, Double maxPrice) {
        logProductMinPriceFilterValidation(minPrice);
        if (nonNull(minPrice) && minPrice <= 0) {
            throw new ClientErrorException("O preço minimo do produto não pode ser menor ou igual a 0.");
        }
        if (nonNull(minPrice) && nonNull(maxPrice)) {
            if (minPrice > maxPrice) {
                throw new ClientErrorException("O preço minimo não pode ser maior que o preço maximo.");
            }
        }
    }

    private void validateMaxPriceFilter(Double maxPrice, Double minPrice) {
        logProductMaxPriceFilterValidation(maxPrice);
        if (nonNull(maxPrice) && maxPrice <= 0) {
            throw new ClientErrorException("O preço máximo do produto não pode ser menor ou igual a 0.");
        }
        if (nonNull(maxPrice) && nonNull(minPrice)) {
            if (minPrice > maxPrice) {
                throw new ClientErrorException("O preço maximo não pode ser menor que o preço minimo.");
            }
        }
    }

    // Validações de paginação page, linesPerPage, direction e OrderBy

    private Integer sanitizePage(Integer page) {
        if (isNull(page) || page < 0 ) {
            return 0;
        }
        return page;
    }

    private Integer sanitizeLinesPerPage(Integer linesPerPage) {
        if (isNull(linesPerPage) || linesPerPage < 0) {
            return 10;
        }
        return linesPerPage;
    }

    private String fixDirection(String direction) {
        if (isBlank(direction) || !ALLOWED_DIRECTION.contains(direction.toLowerCase())) {
            return "asc";
        }
        return direction;
    }

    private String fixOrderBy(String orderBy) {
        if (isBlank(orderBy) || !ALLOWED_ORDER_BY.contains(orderBy.toLowerCase())) {
            return "name";
        }
        return orderBy;
    }

    private void validateDtoData(ProductDto productDto) {
        validateName(productDto.getName());
        validatePrice(productDto.getPrice());
        validateExpiration(productDto.getExpiration());
    }

    private void validateName(String name) {
        logProductNameValidation(name);

        if (isBlank(name)) {
            throw new ClientErrorException("O nome do produto é obrigatório.");
        }
        if (name.length() <= 3) {
            throw new ClientErrorException("O nome do produto deve ter mais de 3 caracteres.");
        }
    }

    private void validateSku(String sku) {
        logProductSkuValidation(sku);

        if (isBlank(sku)) {
            throw new ClientErrorException("O SKU do produto é obrigatório.");
        }
        if (sku.length() != 8) {
            throw new ClientErrorException("O SKU do produto deve ter 8 caracteres.");
        }
        if (!isAlphanumeric(sku)) {
            throw new ClientErrorException("O SKU do produto deve ser alfanumerico.");
        }
    }

    private void skuExists(String sku) {
        if (productRepository.existsProductBySku(sku)) {
            logProductSkuAlreadyExists(sku);
            throw new ClientErrorException("O SKU informado já está cadastrado.");
        }
    }

    private void skuNotExist(String sku) {
        if (!productRepository.existsProductBySku(sku)) {
            logProductNotFoundBySku(sku);
            throw new ClientErrorException("O SKU informado não está cadastrado.");
        }
    }

    private void validatePrice(Double price) {
        logProductPriceValidation(price);

        if (isNull(price)) {
            throw new ClientErrorException("O preço do produto é obrigatório.");
        }
        if (price <= 0) {
            throw new ClientErrorException(("O preço do produto deve ser maior que 0."));
        }
    }

    private void validateExpiration(LocalDate expiration) {
        logProductExpirationValidation(expiration);

        if (isNull(expiration)) {
            throw new ClientErrorException("A data de vencimento do produto é obrigatória.");
        }
        if (expiration.isBefore(LocalDate.now())) {
            throw new ClientErrorException("A data de vencimento do produto não pode estar no passado.");
        }
    }

}
