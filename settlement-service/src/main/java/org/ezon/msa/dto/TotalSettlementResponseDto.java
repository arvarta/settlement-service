package org.ezon.msa.dto;

import lombok.Data;

@Data
public class TotalSettlementResponseDto {
	//총 판매 수
	private Long totalSaleCount;
	//총 판매 수익
	private Long totalSaleAmount;
	//정산 가능한 수익
	private Long SettlementAvailableAmount;
	//총 부과된 수수료 금액
	private Long totalFee;
}
