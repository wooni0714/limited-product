package com.limited.product.limitedProduct.service;

import com.limited.product.common.aop.DistributedLock;
import com.limited.product.common.exception.BusinessException;
import com.limited.product.limitedProduct.entity.LimitedProduct;
import com.limited.product.limitedProduct.repository.LimitedProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.limited.product.common.Constants.PRODUCT_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LimitedProductStockService {
    private final LimitedProductRepository limitedProductRepository;

    @DistributedLock(key = "#productId")
    public void decreaseStock(Long productId, Long quantity) {
        log.info("==> decreaseStock 호출됨: productId={}", productId);
        LimitedProduct product = limitedProductRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND));

        try {
            product.validateMaxQuantity(quantity);
            product.decreaseQuantity(quantity);
        } catch (BusinessException e) {
            log.error("[구매 실패] productId={}, userQuantity={}, stock={}, maxPerUser={}", productId, quantity, product.getQuantity(), product.getMaxQuantity());
            throw e;
        }

        limitedProductRepository.save(product);
    }
}