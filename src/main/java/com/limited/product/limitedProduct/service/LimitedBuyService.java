package com.limited.product.limitedProduct.service;

import com.limited.product.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.limited.product.common.Constants.NOT_ALLOWED_TO_ACCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class LimitedBuyService {
    private final LimitedQueueService limitedQueueService;
    private final LimitedProductStockService productService;

    public void limitedSale(Long productId, String userId, Long quantity) {
        if (!limitedQueueService.isAlreadyConnected(userId)) {
            throw new BusinessException(NOT_ALLOWED_TO_ACCESS);
        }
        productService.decreaseStock(productId, quantity);
        limitedQueueService.removeFromConnected(userId);
    }
}
