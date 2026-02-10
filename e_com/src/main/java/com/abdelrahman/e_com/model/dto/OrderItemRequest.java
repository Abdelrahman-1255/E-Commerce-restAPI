package com.abdelrahman.e_com.model.dto;

public record OrderItemRequest(
    int productId,
    int quantity
) {
}
