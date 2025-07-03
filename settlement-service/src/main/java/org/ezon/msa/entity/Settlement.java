package org.ezon.msa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "settlement")
public class Settlement {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "settlement_id")
	private Long settlementId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "amount", nullable = false)
	private Long amount;

	@Column(name = "bank_name", nullable = false)
	private String bankName;

	@Column(name = "account_num", nullable = false)
	private String accountNum;

	@Column(name = "write_at", nullable = false)
	private LocalDateTime writeAt;

	@Column(name = "payment_at", nullable = true)
	private LocalDateTime paymentAt;
}
