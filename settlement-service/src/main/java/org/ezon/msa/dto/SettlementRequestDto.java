package org.ezon.msa.dto;

import lombok.Data;

@Data
public class SettlementRequestDto {
	//요청 금액
	private Long amount;
	//은행명
	private String bankName;
	//계좌번호
	private String accountNum;
	//사용자 이름
	private String userName;
}
