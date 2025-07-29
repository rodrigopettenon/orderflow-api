package com.rodrigopettenon.orderflow.repositories;

import com.rodrigopettenon.orderflow.dtos.GlobalPageDto;
import com.rodrigopettenon.orderflow.dtos.ProductDto;
import com.rodrigopettenon.orderflow.exceptions.ClientErrorException;
import com.rodrigopettenon.orderflow.models.ProductModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.*;

import static com.rodrigopettenon.orderflow.utils.LogUtil.*;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Repository
public class ProductRepository {

    @PersistenceContext
    private EntityManager em;

    public void saveProduct(ProductDto productDto) {
        try{
            UUID id = UUID.randomUUID();

            StringBuilder sql = new StringBuilder();
            sql.append(" INSERT INTO tb_products (id, name, sku, price, expiration_date) ");
            sql.append(" VALUES (:id ,:name, :sku, :price, :expiration) ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("id", id.toString())
                    .setParameter("name", productDto.getName())
                    .setParameter("sku", productDto.getSku())
                    .setParameter("price", productDto.getPrice())
                    .setParameter("expiration", productDto.getExpiration());

            query.executeUpdate();
        } catch (Exception e) {
            logUnexpectedErrorOnSaveClientWithSku(productDto.getSku(), e);
            throw new ClientErrorException("Erro ao cadastrar um novo produto. ");
        }
    }

    public Boolean existsProductBySku(String sku) {
        try{
            String sql = " SELECT 1 FROM tb_products WHERE sku = :sku LIMIT 1 ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("sku", sku);

            List<?> result = query.getResultList(); //Criando uma lista que receberá itens do tipo '?' ela receberá o resultado da query.

            return !result.isEmpty(); // Se o objeto pesquisado pela query existir retornará true, caso contrário retornará false.

        } catch (Exception e) {
            logUnexpectedErrorCheckingProductExistenceBySku(sku, e);
            throw new ClientErrorException("Erro ao verificar existência do produto pelo SKU.");
        }
    }

    public Boolean existsProductById(UUID id) {
        try{
            String sql = " SELECT 1 FROM tb_products WHERE id = :id LIMIT 1 ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<?> result = query.getResultList();

            return !result.isEmpty();

        } catch (Exception e) {
            throw new ClientErrorException("Erro ao verificar existência do produto pelo id.");
        }
    }

    public List<ProductDto> findAllProducts(Integer page, Integer linesPerPage, String direction, String orderBy) {
        try{
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT name, sku, price, expiration_date FROM tb_products ");
            sql.append(" ORDER BY " + orderBy + " " + direction + " ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);

            List<Object[]> results = query.getResultList();
            List<ProductDto> products =  new ArrayList<>();

            for (Object[] result: results) {
                ProductDto productDto = new ProductDto();
                productDto.setName((String) result[0]);
                productDto.setSku((String) result[1]);
                productDto.setPrice(((Number) result[2]).doubleValue());
                productDto.setExpiration(((Date) result[3]).toLocalDate());

                products.add(productDto);
            }

            return products;

        } catch (Exception e) {
            logUnexpectedErrorOnFindAllProducts(e);
            throw new ClientErrorException("Erro ao buscar todos produtos.");
        }

    }

    public ProductModel findProductModelById(UUID id) {
        try{
            String sql = (" SELECT id, name, sku, price, expiration_date FROM tb_products WHERE id = :id LIMIT 1 ");

            Query query = em.createNativeQuery(sql)
                    .setParameter("id", id.toString());

            List<Object[]> resultList = query.getResultList();

            if (resultList.isEmpty()) {
                throw new ClientErrorException("Produto não encontrado pelo id: " + id);
            }

            Object[] result = resultList.get(0);
            ProductModel productModel = new ProductModel();

            productModel.setId(UUID.fromString((String) result[0]));
            productModel.setName((String) result[1]);
            productModel.setSku((String) result[2]);
            productModel.setPrice(((Number) result[3]).doubleValue());
            productModel.setExpiration(((Date) result[4]).toLocalDate());

            return productModel;

        }catch (ClientErrorException e) {
            throw e; // Não capturar e engolir a exceção esperada
        }
        catch (Exception e) {
            throw new ClientErrorException("Erro ao buscar produto pelo id.");
        }
    }

    public ProductDto findProductBySku(String sku) {
        try{
            String sql = " SELECT id, name, sku, price, expiration_date FROM tb_products WHERE sku = :sku";

            Query query = em.createNativeQuery(sql)
                    .setParameter("sku", sku);

            List<Object[]> resultList = query.getResultList();

            if(resultList.isEmpty()) {
                throw new ClientErrorException("Produto não encontrado pelo SKU.");
            }
            Object[] result = resultList.get(0);
            ProductDto productDtoFound = new ProductDto();
            productDtoFound.setId(UUID.fromString((String) result[0]));
            productDtoFound.setName((String) result[1]);
            productDtoFound.setSku((String) result[2]);
            productDtoFound.setPrice((Double) result[3]);
            productDtoFound.setExpiration(((Date) result[4]).toLocalDate());

            logFoundProductBySkuSuccessfully(sku);
            return productDtoFound;
        }catch (ClientErrorException e) {
            throw  e;  // Não capturar e engolir a exceção esperada
        }
        catch (Exception e) {
            logUnexpectedErrorOnFindProductBySku(sku, e);
            throw new ClientErrorException("Erro ao buscar produto pelo SKU.");
        }

    }

    public ProductDto updateProductBySku(String sku, ProductDto productDto) {
        try{
            StringBuilder sql = new StringBuilder();
            sql.append(" UPDATE tb_products SET name = :name, price = :price, expiration_date = :expiration ");
            sql.append(" WHERE sku = :sku ");

            Query query = em.createNativeQuery(sql.toString())
                    .setParameter("name", productDto.getName())
                    .setParameter("price", productDto.getPrice())
                    .setParameter("expiration", productDto.getExpiration())
                    .setParameter("sku", sku);

            query.executeUpdate();

            logProductUpdatedBySkuSuccessfully(sku);
            return productDto;
        } catch (Exception e) {
            logUnexpectedErrorOnUpdateProductBySku(sku, e);
            throw new ClientErrorException("Erro ao realizar atualização no produto pelo SKU.");
        }
    }

    public void deleteProductBySku(String sku) {
        try{
            String sql = " DELETE FROM tb_products WHERE sku = :sku LIMIT 1 ";

            Query query = em.createNativeQuery(sql)
                    .setParameter("sku", sku);

            query.executeUpdate();
        } catch (Exception e) {
            logUnexpectedErrorOnDeleteProductBySku(sku, e);
            throw new ClientErrorException("Erro ao realizar deleção do produto pela SKU.");
        }

    }

    public Long countAllProducts() {
        try{
            String sql = " SELECT COUNT(*) FROM tb_products ";

            Query query = em.createNativeQuery(sql);

            Object result = query.getSingleResult();
            Number total = (Number) result;

            return total.longValue();
        } catch (Exception e) {
            logUnexpectedErrorOnCountAllProducts(e);
            throw new ClientErrorException("Erro ao contar todos produtos.");
        }
    }

    public GlobalPageDto<ProductDto> findFilteredProducts(String name, String sku, Double minPrice,
                                              Double maxPrice, Integer page, Integer linesPerPage,
                                              String fixedDirection, String fixedOrderBy) {
            List<ProductDto> products = queryFindFilteredProducts(name, sku, minPrice, maxPrice,
                    page, linesPerPage, fixedDirection, fixedOrderBy);

            Long total = queryCountFilteredProducts(name, sku, minPrice, maxPrice);

            GlobalPageDto<ProductDto> productPageDto = new GlobalPageDto<>();
            productPageDto.setItems(products);
            productPageDto.setTotal(total);

            logFindFilteredProductsSuccessfully(name, sku, minPrice, maxPrice);
            return productPageDto;
    }

    protected List<ProductDto> queryFindFilteredProducts(String name, String sku, Double minPrice,
                                                       Double maxPrice, Integer page, Integer linesPerPage,
                                                       String fixedDirection, String fixedOrderBy) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT name, sku, price, expiration_date FROM tb_products WHERE 1=1 ");

            if (isNotBlank(name)) {
                sql.append(" AND name LIKE :name ");
                parameters.put("name", "%" + name + "%");
            }
            if (isNotBlank(sku)) {
                sql.append(" AND sku = :sku ");
                parameters.put("sku", sku);
            }
            if (nonNull(minPrice)) {
                sql.append(" AND price >= :minPrice ");
                parameters.put("minPrice", minPrice);
            }
            if (nonNull(maxPrice)) {
                sql.append(" AND price <= :maxPrice ");
                parameters.put("maxPrice", maxPrice);
            }

            sql.append(" ORDER BY " + fixedOrderBy + " " + fixedDirection + " ");
            sql.append(" LIMIT :limit OFFSET :offset ");

            Query queryProducts = em.createNativeQuery(sql.toString())
                    .setParameter("limit", linesPerPage)
                    .setParameter("offset", page * linesPerPage);
            setQueryParameters(queryProducts, parameters);

            logInfoStartingProductsSearchQueryFiltered(name, sku, minPrice, maxPrice);
            List<Object[]> productResults = queryProducts.getResultList();
            List<ProductDto> products = new ArrayList<>();

            for (Object[] result : productResults) {
                ProductDto productDto = new ProductDto();
                productDto.setName((String) result[0]);
                productDto.setSku((String) result[1]);
                productDto.setPrice(((Number) result[2]).doubleValue());
                productDto.setExpiration(((Date) result[3]).toLocalDate());

                products.add(productDto);
            }

            return products;
        } catch (Exception e) {
            logUnexpectedErrorOnFindFilteredProducts(e);
            throw new ClientErrorException("Erro ao buscar produtos filtrados.");
        }
    }

    protected Long queryCountFilteredProducts(String name, String sku,
                                                     Double minPrice, Double maxPrice){
        try {
            Map<String, Object> parameters = new HashMap<>();
            StringBuilder sql = new StringBuilder();

            sql.append(" SELECT COUNT(*) FROM tb_products WHERE 1=1 ");

            if (isNotBlank(name)) {
                sql.append(" AND name LIKE :name ");
                parameters.put("name", "%" + name + "%");
            }
            if (isNotBlank(sku)) {
                sql.append(" AND sku = :sku ");
                parameters.put("sku", sku);
            }
            if (nonNull(minPrice)) {
                sql.append(" AND price >= :minPrice ");
                parameters.put("minPrice", minPrice);
            }
            if (nonNull(maxPrice)) {
                sql.append(" AND price <= :maxPrice ");
                parameters.put("maxPrice", maxPrice);
            }

            Query queryCount = em.createNativeQuery(sql.toString());
            setQueryParameters(queryCount, parameters);

            logInfoStartingFilteredProductsCountQuery(name, sku, minPrice, maxPrice);
            Object countResult = queryCount.getSingleResult();
            Number total = (Number) countResult;

            return total.longValue();
        } catch (Exception e) {
            logUnexpectedErrorOnCountFilteredProducts(e);
            throw new ClientErrorException("Erro ao contar produtos filtrados.");
        }

    }

    void setQueryParameters(Query query, Map<String, Object> parameters) {
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
    }
}
