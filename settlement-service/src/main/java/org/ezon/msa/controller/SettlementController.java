package org.ezon.msa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ezon.msa.Pagination;
import org.ezon.msa.dto.AdminSettlementResponseDto;
import org.ezon.msa.dto.OrderItemDto;
import org.ezon.msa.dto.SettlementRequestDto;
import org.ezon.msa.entity.Settlement;
import org.ezon.msa.service.SettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/settlement")
public class SettlementController {
	
	@Autowired
	private SettlementService settlementService;
	
	public SettlementController(SettlementService settlementService) {
		this.settlementService = settlementService;
	}
	
	//정산 총합 관련 정보 도출
	@GetMapping("/seller/{userId}/total")
	public ResponseEntity<Map<String, Object>> getSettlements(@PathVariable Long userId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//구매확정(판매완료)된 주문목록 조회
			List<OrderItemDto> oiList = settlementService.getOIList(userId);
			for(OrderItemDto oi : oiList) {
				System.out.println(oi);
			}
			oiList = settlementService.checkToConfirmed(oiList.toArray(new OrderItemDto[0]));
			
			result.put("settlement", settlementService.getSettlementDto(userId));
			result.put("message", "load success");
		}catch(Exception e) {
			System.out.print(e.getMessage() + " 예외 발생 발생\n자세한 원인 : ");
			e.printStackTrace();
			result.put("message", "exception");
			return ResponseEntity.status(500).body(result);
		}
		return ResponseEntity.ok(result);
	}
	
	//정산 신청
	@PostMapping("/seller/{userId}")
	public ResponseEntity<Map<String, Object>> addSettlements(@PathVariable Long userId,
		@RequestBody SettlementRequestDto dto){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			//판매자 찾기
//			Seller s = sellerService.findById(userId);
//			if(s == null) throw new Exception("not found seller");
			
			int process = settlementService.save(
				userId, dto, settlementService.getOIList(userId).toArray(new OrderItemDto[0]));
			switch (process) {
				case -1 :
					result.put("message", "available amount over"); break;
				case 0 :
					result.put("message", "post fail"); break;
				case 1 :
					result.put("message", "post success"); break;
			}
		}catch(Exception e) {
			System.out.print(e.getMessage() + " 예외 발생 발생\n자세한 원인 : ");
			e.printStackTrace();
			result.put("message", "exception");
			return ResponseEntity.status(500).body(result);
		}
		return ResponseEntity.ok(result);
	}
	
	//정산 내역
	@GetMapping("/seller/{userId}")
	public ResponseEntity<Map<String, Object>> getSettlementList(@PathVariable Long userId,
		@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int perPage){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//			//판매자 찾기
//			Seller s = sellerService.findById(userId);
//			if(s == null) throw new Exception("not found seller");
//			
			List<Settlement> sList = settlementService.getAll(userId);
			if(sList == null) sList = new ArrayList<>();
			result.put("settlementList", Pagination.paging(sList, perPage, page));
			result.put("totalPage", Pagination.totalPage(sList, perPage));
			result.put("message", "load success");
		}catch(Exception e) {
			System.out.print(e.getMessage() + " 예외 발생 발생\n자세한 원인 : ");
			e.printStackTrace();
			result.put("message", "exception");
			return ResponseEntity.status(500).body(result);
		}
		return ResponseEntity.ok(result);
	}
	
	
/* 												어드민 URL 부문													*/
	//정산 신청 내역 조회
	@GetMapping("/admin")
	public ResponseEntity<Map<String, Object>> getAllSettlement(@RequestParam(defaultValue = "1") int page, 
		@RequestParam(defaultValue = "10") int perPage){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//			// 어드민 권한 확인 토큰 사용 예정
//			Seller s = sellerService.findById(userId);
//			if(s == null) throw new Exception("not found seller");
			List<AdminSettlementResponseDto> sList = settlementService.getAll();
			result.put("totalPage", Pagination.totalPage(sList, perPage));
			result.put("settlementList",Pagination.paging(sList, perPage, page));
			result.put("message", "load success");
		}catch(Exception e) {
			System.out.print(e.getMessage() + " 예외 발생 발생\n자세한 원인 : ");
			e.printStackTrace();
			result.put("message", "exception");
			return ResponseEntity.status(500).body(result);
		}
		return ResponseEntity.ok(result);
	}
	
	@GetMapping("/admin/wait")
	public ResponseEntity<List<Settlement>> getWaitSettlementCount(){
		List<Settlement> sList = null;
		try {
			sList = settlementService.getListpaymentAtIsNull();
		}catch(Exception e) {
			return ResponseEntity.status(500).body(sList);
		}
		return ResponseEntity.ok(sList);
	}
	
	//정산 처리
	@PutMapping("/{settlementId}/admin")
	public ResponseEntity<Map<String, Object>> setSettlement(@PathVariable Long settlementId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
//				// 어드민 권한 확인 토큰 사용 예정
//				Seller s = sellerService.findById(userId);
//				if(s == null) throw new Exception("not found seller");
			
			if(settlementService.processSettlement(settlementId)) {
				result.put("message", "process success");
			}else {
				result.put("message", "process fail");
			}
		}catch(Exception e) {
			System.out.print(e.getMessage() + " 예외 발생 발생\n자세한 원인 : ");
			e.printStackTrace();
			result.put("message", "exception");
			return ResponseEntity.status(500).body(result);
		}
		return ResponseEntity.ok(result);
	}
}
