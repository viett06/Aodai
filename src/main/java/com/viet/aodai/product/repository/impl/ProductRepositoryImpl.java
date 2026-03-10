package com.viet.aodai.product.repository.impl;

import com.viet.aodai.product.domain.dto.ProductSearchResultDTO;
import com.viet.aodai.product.domain.dto.SqlHolder;
import com.viet.aodai.product.domain.request.ProductSearchRequestDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements com.viet.aodai.product.repository.custom.ProductRepositoryCustom {

    private final EntityManager em;

    @Override
    public Page<ProductSearchResultDTO> searchProducts(ProductSearchRequestDTO request, Pageable pageable) {

        SqlHolder holder = buildSql(request);

        Query dataQuery = em.createNativeQuery(holder.getDataSQL());
        holder.getParams().forEach(dataQuery::setParameter);

        dataQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
                List<Object[]> rows = dataQuery.getResultList();
        List<ProductSearchResultDTO> results = rows.stream()
                .map(this::mapRow)
                .toList();

        Query countQuery = em.createNativeQuery(holder.getCountSQL());
        holder.getParams().forEach(countQuery::setParameter);
        long total = ((Number) countQuery.getSingleResult()).longValue();

        return  new PageImpl<>(results, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()), total);

    }

    private static final Map<String, String> SORT_COLUMN_MAP = Map.of(
            "name", "p.name",
            "price", "p.price",
            "created_at", "p.created_at",
            "brand", "p.brand"
    );

    // build sql

    private SqlHolder buildSql(ProductSearchRequestDTO req){

        Map<String, Object> params = new LinkedHashMap<>();

        StringBuilder where = new StringBuilder(" WHERE 1=1 ");

        if (StringUtils.hasText(req.getKeyword())){
            where.append("""
                    AND (
                    LOWER(p.name) LIKE lOWER(:keyword)
                    OR LOWER(p.brand) LIKE LOWER(:keyword)
                    OR LOWER(p.description) LIKE LOWER(:keyword)
                    )
                    """);
            params.put("keyword", "%" + req.getKeyword().trim() + "%");
        }

        if (StringUtils.hasText(req.getBrand())){
            where.append(" AND LOWER(p.brand) = LOWER(:brand) ");
            params.put("brand", req.getBrand().trim());
        }

        if (req.getCategoryId() != null){
            where.append(" AND p.category_id = :categoryId ");
            params.put("categoryId", req.getCategoryId());
        }

        if (req.getMinPrice() != null){
            where.append(" AND p.price >= :minPrice ");
            params.put("minPrice", req.getMinPrice());
        }

        if (req.getMaxPrice() != null){
            where.append(" AND p.price <= :maxPrice ");
            params.put("maxPrice", req.getMaxPrice());
        }

        if (Boolean.TRUE.equals(req.getInStock())){
            where.append(" AND i.quantity > 0 ");
        }

        if (Boolean.FALSE.equals(req.getInStock())){
            where.append(" AND (i.quantity IS NULL OR i.quantity = 0) ");
        }

        String from = """
                FROM products p
                LEFT JOIN category c ON p.category_id = c.id
                LEFT JOIN inventory i ON i.product_id = p.id
                """;
        String sortColumn = SORT_COLUMN_MAP.getOrDefault(req.getSortBy(), "p.created_at");
        String sortDir = "ASC".equalsIgnoreCase(req.getSortDir()) ? "ASC" : "DESC";
        String orderBy = "ORDER BY " + sortColumn + " " + sortDir;


        String dataSql = new StringBuilder()
                .append(
                        """
                                SELECT DISTINCT
                                p.id,
                                p.name,
                                p.description,
                                p.brand,
                                p.image,
                                p.price,
                                p.created_at,
                                p.updated_at,
                                c.id AS category_id,
                                c.name AS category_name,
                                i.quantity AS stock_quantity
                                """
                )
                .append(from)
                .append(where)
                .append(orderBy)
                .toString();

        String countSQL = new StringBuilder()
                .append("SELECT COUNT(DISTINCT p.id) ")
                .append(from)
                .append(where)
                .toString();

        return new SqlHolder(dataSql, countSQL, params);

    }

    private ProductSearchResultDTO mapRow(Object[] row){
        return new ProductSearchResultDTO(
                toLong(row[0]),
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (String) row[4],
                (BigDecimal) row[5],
                toLocalDateTime(row[6]),
                toLocalDateTime(row[7]),
                toLong(row[8]),
                (String) row[9],
                toInteger(row[10])
        );
    }

    private Long toLong(Object val){
        return val == null ? null : ((Number) val).longValue();
    }

    private Integer toInteger(Object val){
        return val == null ? null : ((Number) val).intValue();
    }

    private LocalDateTime toLocalDateTime(Object val){
        if (val == null) return  null;
        if (val instanceof LocalDateTime ldt) return ldt;
        if (val instanceof Timestamp ts)     return ts.toLocalDateTime();
        return null;
    }
}
