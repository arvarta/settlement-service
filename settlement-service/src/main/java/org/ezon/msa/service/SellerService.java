package org.ezon.msa.service;

import java.util.ArrayList;
import java.util.List;

import org.ezon.msa.dto.AdminSettlementResponseDto;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

	
//	public Seller findById(Long userId) {
//		return sellerRepository.findById(userId).get();
//	}
	public List<AdminSettlementResponseDto> setListUserNames(List<AdminSettlementResponseDto> list){
		List<AdminSettlementResponseDto> result = new ArrayList<>();
		for(AdminSettlementResponseDto asrd : list) {
//			asrd.setUserName(sellerRepository.findById(asrd.getUserId()).get().getName());
			result.add(asrd);
		}
		return result;
	}
}
