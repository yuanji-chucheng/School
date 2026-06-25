package com.example.demo.mapper;

import com.example.demo.entity.Item;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ItemMapper {
    Item findById(Long id);
    int insert(Item item);
    int update(Item item);
    List<Item> search(@Param("category") String category,
                      @Param("keyword") String keyword,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice,
                      @Param("status") Integer status,
                      @Param("sellerId") Long sellerId,
                      @Param("priceSort") String priceSort,
                      @Param("offset") int offset,
                      @Param("limit") int limit);
    long countSearch(@Param("category") String category,
                     @Param("keyword") String keyword,
                     @Param("minPrice") BigDecimal minPrice,
                     @Param("maxPrice") BigDecimal maxPrice,
                     @Param("status") Integer status,
                     @Param("sellerId") Long sellerId);
    List<Item> findPending(@Param("offset") int offset, @Param("limit") int limit);
    long countPending();
    int deleteBySellerId(@Param("sellerId") Long sellerId);
}
