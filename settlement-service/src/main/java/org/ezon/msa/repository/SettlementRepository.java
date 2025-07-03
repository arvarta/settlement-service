package org.ezon.msa.repository;

import java.util.List;

import org.ezon.msa.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>{

	List<Settlement> findByUserId(Long userId);
	@Query(value = "SELECT * FROM settlement ORDER BY (payment_at IS NULL) DESC, write_at ASC, settlement_id ASC", nativeQuery = true)
	List<Settlement> findAllcustom();
	
	List<Settlement> findByPaymentAtIsNull();
	
}
