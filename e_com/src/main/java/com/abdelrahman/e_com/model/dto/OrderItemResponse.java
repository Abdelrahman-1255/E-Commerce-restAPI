package com.abdelrahman.e_com.model.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    String productName,
    int quantity,
    BigDecimal totalPrice
) {
    
}
