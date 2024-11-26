package com.example.mall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.mall.service.GoodsService;
import com.example.mall.util.TeamColor;
import com.example.mall.vo.Goods;
import com.example.mall.vo.Page;

import jakarta.servlet.http.HttpSession;



@Controller
@Slf4j
public class GoodsController {
	
	@Autowired GoodsService goodsService;
	
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
		
		// 후기 작성 가능 회원 확인 (해당 상품의 구매이력이 있으면서 후기를 작성하지 않은 회원 + 모든 staff)
		List<Map<String, Object>> eligibleReviewersListMap = goodsService.getEligibleReviewers(goodsNo, loginCustomer);
		String eligibleReviewer = (String)eligibleReviewersListMap.get(0).get("customerEmail");
		
		log.debug(TeamColor.KMJ + "eligibleReviewer : " + eligibleReviewer + TeamColor.RESET );
		
		model.addAttribute("goodsNo", goodsNo);
		model.addAttribute("goods", goods);
		model.addAttribute("boardList", boardList);
		model.addAttribute("eligibleReviewer", eligibleReviewer);
		
		return "off/getGoodsOne";
	}

	
	// home : 메인 페이지 상품 리스트 출력
	@GetMapping("/home")
	public String home(Page page, Model model, @RequestParam(required = false) String searchWord) {
		
		log.debug( TeamColor.KMJ + "GET[GoodsController - home]" + TeamColor.RESET );
		log.debug( TeamColor.KMJ + "searchWord : " + searchWord + TeamColor.RESET );

		// 상품리스트 
		Map<String, Object> goodsList = goodsService.getGoodsList(page, searchWord);
		
		log.debug( TeamColor.KMJ + "page : " + page.toString() + TeamColor.RESET );
		
		model.addAttribute("goodsList", goodsList.get("goodsList"));
		model.addAttribute("page", goodsList.get("page"));
		model.addAttribute("searchWord", searchWord);

		return "off/home";
	}
	
	
	
	
	
	
	
	
}
