package com.limited.product.limitedProduct.entity;

import com.limited.product.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import static com.limited.product.common.Constants.EXCEEDS_MAX_PURCHASE_QUANTITY;
import static com.limited.product.common.Constants.INSUFFICIENT_STOCK;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@Table(name = "LIMITED_PRODUCT")
public class LimitedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private Long quantity;

    private int maxQuantity;

    @Builder
    public LimitedProduct(Long productId, Long quantity, int maxQuantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
    }

    public void validateMaxQuantity(Long requestedQuantity) {
        if (requestedQuantity > this.maxQuantity) {
            throw new BusinessException(EXCEEDS_MAX_PURCHASE_QUANTITY + this.maxQuantity);
        }
        if (this.quantity - requestedQuantity < 0) {
            throw new BusinessException(INSUFFICIENT_STOCK);
        }
    }

    public void decreaseQuantity(Long quantity) {
        this.quantity -= quantity;
    }
}
