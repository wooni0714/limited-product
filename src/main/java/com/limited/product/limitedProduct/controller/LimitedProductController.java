package com.limited.product.limitedProduct.controller;

import com.limited.product.limitedProduct.dto.LimitedBuyRequest;
import com.limited.product.limitedProduct.service.LimitedBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.limited.product.common.Constants.SUCCESS;

@RestController
@RequiredArgsConstructor
public class LimitedProductController {
    private final LimitedBuyService limitedSaleService;

    @PostMapping("/buy/{productId}")
    public ResponseEntity<String> buyLimitedProduct(@PathVariable Long productId, @RequestBody LimitedBuyRequest limitedSaleRequest) {
        limitedSaleService.limitedSale(productId, limitedSaleRequest.userId(), limitedSaleRequest.quantity());
            return ResponseEntity.ok(SUCCESS);
    }
}
