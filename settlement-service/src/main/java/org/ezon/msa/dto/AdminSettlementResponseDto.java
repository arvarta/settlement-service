package org.ezon.msa.dto;

import lombok.Data;

@Data
public class AdminSettlementResponseDto {
	//정산 신청 번호
	private Long settlementId;
	//사용자 이름
	private String userName;
	//사용자 id
	private Long userId;
	//요청 금액
	private Long amount;
	// 요청 날짜
	private String writeAt;
	// 요청 날짜
	private String paymentAt;
}
