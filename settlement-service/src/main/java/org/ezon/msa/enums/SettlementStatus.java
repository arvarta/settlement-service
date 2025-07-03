package org.ezon.msa.enums;

public enum SettlementStatus {
	READY,         // 정산 준비됨 (대기 상태)
    COMPLETED,     // 정산 완료
    FAILED         // 정산 실패 (계좌 오류 등)
}
