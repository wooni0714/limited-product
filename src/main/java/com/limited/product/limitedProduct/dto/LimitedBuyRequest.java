package com.limited.product.limitedProduct.dto;

public record LimitedBuyRequest(
        String userId,
        Long quantity
) {
}
