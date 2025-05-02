package com.limited.product.limitedProduct.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LimitedSaleServiceTest {

    @Autowired
    private LimitedProductStockService limitedProductStockService;

    @Autowired
    private LimitedQueueService limitedQueueService;

    @Autowired
    private LimitedBuyService limitedSaleService;

}