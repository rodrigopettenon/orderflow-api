package com.rodrigopettenon.cadastro_e_consulta.services;

import com.rodrigopettenon.cadastro_e_consulta.dtos.ProductDto;
import com.rodrigopettenon.cadastro_e_consulta.dtos.ProductPageDto;
import com.rodrigopettenon.cadastro_e_consulta.exceptions.ClientErrorException;
import com.rodrigopettenon.cadastro_e_consulta.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.rodrigopettenon.cadastro_e_consulta.utils.LogUtil.*;
import static com.rodrigopettenon.cadastro_e_consulta.utils.StringsValidation.normalizeSpaces;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isAlphanumeric;
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

    public ProductPageDto findAllProducts(Integer page, Integer linesPerPage, String direction, String orderBy) {

        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(page);

        String fixedDirection = fixDirection(ALLOWED_DIRECTION, direction);
        String fixedOrderBy = fixOrderBy(ALLOWED_ORDER_BY, orderBy);

        Long total = productRepository.countAllProducts();
        List<ProductDto> products = productRepository.findAllProducts(sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);

        ProductPageDto productPageDto = new ProductPageDto();
        productPageDto.setTotal(total);
        productPageDto.setProducts(products);

        return productPageDto;
    }

    public ProductPageDto findFilteredProducts(String name, String sku, Double minPrice,
                                       Double maxPrice, Integer page, Integer linesPerPage,
                                       String direction, String orderBy) {
        Integer sanitizedPage = sanitizePage(page);
        Integer sanitizedLinesPerPage = sanitizeLinesPerPage(linesPerPage);

        String fixedDirection = fixDirection(ALLOWED_DIRECTION, direction);
        String fixedOrderBy = fixOrderBy(ALLOWED_ORDER_BY, orderBy);

        String sanitizedName = sanitizeNameFilter(name);
        String sanitizedSku = sanitizeSkuFilter(sku);
        validateMinPriceAndMaxPriceRange(minPrice, maxPrice);



        return  productRepository.findFilteredProducts(sanitizedName, sanitizedSku, minPrice, maxPrice, sanitizedPage, sanitizedLinesPerPage, fixedDirection, fixedOrderBy);
    }

    @Transactional(readOnly = true)
    public ProductDto findBySku(String sku) {
        logFindProductBySkuStart(sku);

        validateSku(sku);
        skuNotExist(sku);

        return productRepository.findBySku(sku);
    }

    @Transactional
    public ProductDto updateBySku(String sku, ProductDto productDto) {
        logProductUpdateBySkuStart(sku);

        validateDtoData(productDto);
        validateSku(sku);
        skuNotExist(sku);

        return productRepository.updateBySku(sku, productDto);
    }

    @Transactional
    public void deleteBySku(String sku) {
        logProductDeletionBySkuStart(sku);

        validateSku(sku);
        skuNotExist(sku);

        logProductDeletedBySkuSuccessfully(sku);
        productRepository.deleteBySku(sku);
    }

    // Validações filtros GET

    private String sanitizeNameFilter(String name) {
        String nameWithoutDuplicateSpaces =  normalizeSpaces(name);
        if (isBlank(name)) {
            return null;
        }
        return nameWithoutDuplicateSpaces;
    }

    private String sanitizeSkuFilter(String sku) {
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

    private void validateMinPriceAndMaxPriceRange(Double minPrice, Double maxPrice) {
        if (!isNull(minPrice) && minPrice <= 0) {
            throw new ClientErrorException("O preço minimo do produto não pode ser menor ou igual a 0.");
        }
        if (!isNull(maxPrice) && maxPrice <= 0) {
            throw new ClientErrorException("O preço máximo do produto não pode ser menor ou igual a 0.");
        }
        if (!isNull(minPrice) && !isNull(maxPrice)) {
            if (minPrice > maxPrice) {
                throw new ClientErrorException("O preço minimo não pode ser maior que o preço maximo.");
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

    private String fixDirection(List<String> directionAllowed, String direction) {
        if (isBlank(direction) || !directionAllowed.contains(direction.toLowerCase())) {
            return "asc";
        }
        return direction;
    }

    private String fixOrderBy(List<String> orderByAllowed, String orderBy) {
        if (isBlank(orderBy) || !orderByAllowed.contains(orderBy.toLowerCase())) {
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
        if (productRepository.existsBySku(sku)) {
            logProductSkuAlreadyExists(sku);
            throw new ClientErrorException("O SKU informado já está cadastrado.");
        }
    }

    private void skuNotExist(String sku) {
        if (!productRepository.existsBySku(sku)) {
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
            throw new ClientErrorException("A data de vencimento do produto não pode ser no passado.");
        }
    }

}
