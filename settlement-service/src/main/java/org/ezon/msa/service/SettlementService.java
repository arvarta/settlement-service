package org.ezon.msa.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ezon.msa.dto.AdminSettlementResponseDto;
import org.ezon.msa.dto.OrderItemDto;
import org.ezon.msa.dto.SettlementRequestDto;
import org.ezon.msa.dto.TotalSettlementResponseDto;
import org.ezon.msa.dto.UserDto;
import org.ezon.msa.entity.Settlement;
import org.ezon.msa.enums.OrderStatus;
import org.ezon.msa.repository.SettlementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SettlementService {
	@Autowired
	private SettlementRepository settlementRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public SettlementService(SettlementRepository settlementRepository,
		RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.settlementRepository = settlementRepository;
	}
	
	public List<OrderItemDto> getOIList(Long sellerId){
		String url = "http://localhost:10200/api/orders/sellers?sellerId="+sellerId;
		ResponseEntity<List<OrderItemDto>> resp = restTemplate.exchange(
			url, 
			HttpMethod.GET,
			null,
			new ParameterizedTypeReference<List<OrderItemDto>>() {}
		);
		return resp.getBody();
	}
	
	// SettlementDTO 구하기
	public TotalSettlementResponseDto getSettlementDto(Long userId) {
		List<OrderItemDto> oiList = getOIList(userId);
		System.out.println(oiList);
		TotalSettlementResponseDto dto = new TotalSettlementResponseDto();
		dto.setTotalSaleCount(getTotalCount(oiList.toArray(new OrderItemDto[0])));
		dto.setTotalSaleAmount(getTotalSales(oiList.toArray(new OrderItemDto[0])));
		dto.setTotalFee(getTotalFee(oiList.toArray(new OrderItemDto[0])));
		dto.setSettlementAvailableAmount(getSettlementAvailableAmount(userId, oiList.toArray(new OrderItemDto[0])));
		return dto;
	}
	
	//판매완료(구매확정)된 금액 총합 구하기
	private Long getTotalSales(OrderItemDto... oiArr) {
		Long result = 0L;
		for(OrderItemDto oi : oiArr) {
			Integer temp = oi.getDiscountPrice();
			if(temp == null || temp < 1) {
				temp = oi.getPrice();
				if(temp == null || temp < 1) {
					temp = oi.getTotalAmount();
				}
			}
			result += temp;
		}
		return result;
	}
	
	//판매완료(구매확정)된 물품 총 수량 구하기
	private Long getTotalCount(OrderItemDto... oiArr) {
		Long result = 0L;
		for(OrderItemDto oi : oiArr) {
			result += oi.getQuantity();
		}
		return result;
	}
	
	//판매완료(구매확정)된 물품 총 수수료 구하기
	//현재는 배달비만 존재하여, 다른 수수료는 추가하지 않음
	public Long getTotalFee(OrderItemDto... oiArr) {
		Long result = 0L;
		for(OrderItemDto oi : oiArr) {
			result += oi.getShippingFee();
		}
		return result;
	}
	
	// 정산 받은 금액
	public Long getTotalReceivedAmount(Long userId) {
		List<Settlement> list = settlementRepository.findByUserId(userId);
		Long result =  0L;
		if(list != null && list.size() > 0) {
			for(Settlement s : list) {
				result += s.getAmount();
			}
		}
		return result;
	}
	
	// 신청 가능 금액 조회
	private Long getSettlementAvailableAmount(Long userId, OrderItemDto... oiArr) {
		return getTotalSales(oiArr) - getTotalReceivedAmount(userId);
	}
	
	public int save(Long userId, SettlementRequestDto dto,OrderItemDto... oiArr) {
		int result = -1;
		if(dto.getAmount() > getSettlementAvailableAmount(userId, oiArr)) {
			return result;
		}else {
			result = 0;
		}
		Settlement s = new Settlement();
		s.setAccountNum(dto.getAccountNum());
		s.setAmount(dto.getAmount());
		s.setBankName(dto.getBankName());
		s.setWriteAt(LocalDateTime.now());
		s.setUserId(userId);
		if(null != settlementRepository.save(s)){
			result = 1;
		}
		return result;
	}
	
	public List<Settlement> getAll(Long userId) {
		return settlementRepository.findByUserId(userId);
	}
	public List<AdminSettlementResponseDto> getAll() {
		List<Settlement> stList = settlementRepository.findAllcustom();
		List<AdminSettlementResponseDto> result = new ArrayList<>();
		for(Settlement s : stList) {
			AdminSettlementResponseDto temp = new AdminSettlementResponseDto();
			try {
				String url = "http://localhost:10000/api/users/" + s.getUserId();
				ResponseEntity<UserDto> resp = restTemplate.getForEntity(url, UserDto.class
						);
				UserDto dto = resp.getBody();
				
				temp.setAmount(s.getAmount());
				temp.setSettlementId(s.getSettlementId());
				temp.setUserName(dto.getName());
				temp.setUserId(s.getUserId());
				temp.setWriteAt(s.getWriteAt().toLocalDate().toString());
				String pa = null;
				LocalDateTime ld = s.getPaymentAt();
				if(ld != null) pa = ld.toLocalDate().toString();
				temp.setPaymentAt(pa);
				result.add(temp);
				System.out.println("settlement : " + temp);
			}catch (Exception e) {
				System.out.println("url 연동 실패 -> " + e.getMessage());
			}
		}
		return result;
	}
	public boolean processSettlement(Long settlementId) throws NullPointerException {
		Settlement s = settlementRepository.findById(settlementId).orElse(null);
		if(s == null) throw new NullPointerException("not exist settlement");
		s.setPaymentAt(LocalDateTime.now());
		if(settlementRepository.save(s) != null) {
			return true;
		}else {
			return false;
		}
	}

	public List<OrderItemDto> checkToConfirmed(OrderItemDto... oiArr) {
		List<OrderItemDto> result = new ArrayList<>();
		for(OrderItemDto oi : oiArr) {
			if(oi.getStatus().equals(OrderStatus.PURCHASE_CONFIRMED)) {
				result.add(oi);
			}
		}
		return null;
	}

	public List<Settlement> getListpaymentAtIsNull() {
		return settlementRepository.findByPaymentAtIsNull();
	}
	
}
