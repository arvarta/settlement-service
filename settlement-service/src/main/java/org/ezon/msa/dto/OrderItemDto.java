package org.ezon.msa.dto;

import org.ezon.msa.enums.OrderStatus;

import lombok.Data;

@Data
public class OrderItemDto {
	Integer discountPrice;
	Integer price;
	Integer quantity;
	Integer shippingFee;
	Integer totalAmount;
	OrderStatus status;
	
}
