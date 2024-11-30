package com.example.mall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mall.service.CategoryService;
import com.example.mall.service.GoodsService;
import com.example.mall.util.TeamColor;
import com.example.mall.vo.Category;
import com.example.mall.vo.Goods;
import com.example.mall.vo.GoodsForm;
import com.example.mall.vo.Page;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;



@Controller
@Slf4j
public class GoodsController {
	
	@Autowired GoodsService goodsService;
	@Autowired CategoryService categoryService;
	
	// Author : 김문정
	// getGoodsOne : 상품 상세보기 + 후기 리스트 + 후기 작성, 수정
	@GetMapping("/getGoodsOne")
	public String getGoodsOne(@RequestParam Integer goodsNo, Model model, HttpSession session) {
		log.debug( TeamColor.KMJ + "GET[GoodsController - getGoodsOne]" + TeamColor.RESET );
		log.debug( TeamColor.KMJ + "goodsNo : " + goodsNo + TeamColor.RESET );
	
		String loginCustomer = null;
		String loginStaff = null;
		
		if(session.getAttribute("loginCustomer") != null ) {
			loginCustomer = (String) session.getAttribute("loginCustomer");			
			log.debug( TeamColor.KMJ + "loginCustomer : " + loginCustomer + TeamColor.RESET );
			
		}else {
			loginStaff = (String) session.getAttribute("loginStaff");
			log.debug( TeamColor.KMJ + "loginStaff : " + loginStaff + TeamColor.RESET );
		}
		

		// goodsOne : 상품 상세보기
		Goods goods = goodsService.getGoodsOne(goodsNo);
		log.debug(TeamColor.KMJ + "goods : " + goods.toString() + TeamColor.RESET );
		
		// boardList : 후기 리스트
		List<Map<String, Object>> boardList = goodsService.getBoardListByGoodsNo(goodsNo);
		
		// 후기 작성 가능 회원 확인 (해당 상품의 구매이력이 존재 + payment_Status가 '배송완료' + 후기를 작성하지 않은 회원 + 모든 staff)
		List<Map<String, Object>> eligibleReviewersListMap = goodsService.getEligibleReviewers(goodsNo, loginCustomer);
		
		log.debug(TeamColor.KMJ + "eligibleReviewersListMap : " + eligibleReviewersListMap.toString() + TeamColor.RESET );
		boolean isEligibleReviewer = false;
		
		if(eligibleReviewersListMap.size() > 0) {
			
			if(Integer.parseInt(String.valueOf((eligibleReviewersListMap.get(0).get("boardOrdersNo")))) == -1) {
				
				isEligibleReviewer = true;
			}

			Integer orderNo = null;

			for(int i = 0; i<eligibleReviewersListMap.size(); i++) {
				if(eligibleReviewersListMap.get(i).get("paymentStatus").equals("배송완료") && eligibleReviewersListMap.get(i).get("boardOrdersNo").equals("-1")) {
					orderNo = Integer.parseInt(String.valueOf(eligibleReviewersListMap.get(i).get("orderNo")));
					model.addAttribute("orderNo", orderNo);
					log.debug(TeamColor.KMJ + "orderNo : " + orderNo + TeamColor.RESET );
				}

			}
			
		}

		log.debug(TeamColor.KMJ + "eligibleReviewer : " + isEligibleReviewer + TeamColor.RESET );

		model.addAttribute("goodsNo", goodsNo);
		model.addAttribute("goods", goods);
		model.addAttribute("boardList", boardList);
		model.addAttribute("isEligibleReviewer", isEligibleReviewer);
	
		
		return "/off/getGoodsOne";
	}
	
	// 김동현
	// getGoodsListByStaff Form
	@GetMapping("/staff/getGoodsListByStaff")
	public String getGoodsListByStaff(Model model, Page page, @RequestParam(required = false) String searchWord) {
		
		Map<String, Object> goodsList = goodsService.getGoodsListByStaff(page, searchWord);
		log.debug(TeamColor.KDH + "goodsList" + goodsList.toString() + TeamColor.RESET); // debug
		
		if(page.getCurrentPage() > page.getLastPage()) {
			return "redirect:/staff/getGoodsListByStaff?currentPage=" + page.getLastPage();
		}
		
		model.addAttribute("goodsList", goodsList.get("goodsList"));
		model.addAttribute("page", goodsList.get("page"));
		log.debug(TeamColor.KDH + "searchWord : " + searchWord + TeamColor.RESET); // debug
		model.addAttribute("searchWord", searchWord);
		
		return "staff/getGoodsListByStaff";
	}
	
	// 김동현
	// removeGoods - 판매중지
	@GetMapping("/staff/removeGoods")
	public String removeGoods(Goods goods) {
		
		// log.debug(TeamColor.KDH + "goodsStatuts : " + goods.getGoodsStatus() + TeamColor.RESET); // debug
		goods.setGoodsStatus("판매중지");
		// log.debug(TeamColor.KDH + "goodsStatuts : " + goods.getGoodsStatus() + TeamColor.RESET); // debug
		
		// log.debug(TeamColor.KDH + "removeGoodsNo : " + goods.getGoodsNo() + TeamColor.RESET); // debug
		int removeGoodsRow = goodsService.removeGoods(goods);
		if(removeGoodsRow == 0) {
			return "redirect:/staff/getGoodsListByStaff";
		}
		
		return "redirect:/staff/getGoodsListByStaff";
	}
	
	// 김동현
	// addGoods Form
	@GetMapping("/staff/addGoods")
	public String addGoods(Model model) {
		
		// Category List
		List<Category> categoryList = categoryService.getCategoryList();
		
		log.debug(TeamColor.KDH + "categoryList : " + categoryList + TeamColor.RESET); // debug
		model.addAttribute("categoryList", categoryList);
		
		return "staff/addGoods";
	}
	
	// 김동현
	//addGoods Action
	@PostMapping("/staff/addGoods")
	public String addGoods(Model model, GoodsForm goodsForm, HttpSession session) {
		
		
//		/*
//		log.debug("actorForm.getFirstName() : " + actorForm.getFirstName());
//		log.debug("actorForm.getLastName() : " + actorForm.getLastName());
//		log.debug("actorForm.getActorFile() : " + actorForm.getActorFile());
//		if(actorForm.getActorFile() != null) {
//			log.debug("actorForm.getActorFile().size() : " + actorForm.getActorFile().size());
//		}
//		*/
//		List<MultipartFile> list = actorForm.getActorFile();
//		
//		// 배우 정보만 입력하고 사진은 첨부 안했을 때
//		if(list == null || list.isEmpty()) {
//			String path = null;	
//			actorService.addActor(actorForm, path);
//			return "redirect:/on/actorList";
//		}
//		
//		// 이미지파일은 *.jpg or *png만 가능
//		for(MultipartFile f : list) { 
//			if(!(f.getContentType().equals("image/jpeg") || f.getContentType().equals("image/png"))) {
//				model.addAttribute("imageMsg", "jpeg, png 파일만 입력이 가능합니다");
//				return "on/addActor";
//			} 
//		}
//		
//		String path = session.getServletContext().getRealPath("/upload/");
//		actorService.addActor(actorForm, path);
		
		goodsService.addGoods(goodsForm);
		
		return "redirect:/getGoodsListByStaff";
	}

	
	// home : 메인 페이지 상품 리스트 출력
	@GetMapping("/home")
	public String home(Page page, Model model, @RequestParam(required = false) String searchWord, @RequestParam(required = false) String category) {
		
		log.debug( TeamColor.KMJ + "GET[GoodsController - home]" + TeamColor.RESET );
		log.debug( TeamColor.KMJ + "searchWord : " + searchWord + TeamColor.RESET );
		
		// categoryList 가져오기
		List<Category> categoryList = categoryService.getCategoryList();
		
		log.debug(TeamColor.KMJ + "categoryList : " + categoryList + TeamColor.RESET );
		model.addAttribute("categoryList", categoryList);

		// 선택된 category
		log.debug(TeamColor.KMJ + "category : " + category + TeamColor.RESET );	
		
		// split으로 ',' 분리 후 배열에 담아 쿼리에 어떻게 조회할 것인지..?
		

		// 상품리스트 
		Map<String, Object> goodsList = goodsService.getGoodsList(page, searchWord);
		
		log.debug( TeamColor.KMJ + "page : " + page.toString() + TeamColor.RESET );
		
		model.addAttribute("goodsList", goodsList.get("goodsList"));
		model.addAttribute("page", goodsList.get("page"));
		model.addAttribute("searchWord", searchWord);

		return "off/home";
	}
	
	
	
	
	
	
	
	
	
	
	
}
