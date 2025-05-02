package com.limited.product.limitedProduct.repository;

import com.limited.product.limitedProduct.entity.LimitedProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimitedProductRepository extends JpaRepository<LimitedProduct, Long> {
    Optional<LimitedProduct> findByProductId(Long productId);
}
