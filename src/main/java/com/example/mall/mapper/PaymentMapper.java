package com.example.mall.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.example.mall.vo.Customer;
import com.example.mall.vo.Orders;
import com.example.mall.vo.Payment;

// Author : 김은서
@Mapper
public interface PaymentMapper {
	
	// /customer/addPayment 에서 사용 AutoIncreasement 인 PaymentNo selectKey로 파라미터 전달
	Integer insertPayment(Payment payment);
	
	// /customer/addPayment 에서 사용, insertPayment 반환값(PaymentNo) 대상으로 orders 인스턴스 생성 / 파라미터 : cartNo, paymentNo
	Integer insertOrders(Map<String, Integer> orders);
	
	// payment 기준 리스트 출력시 사용 (결제번호 리스트 간략 출력)
	// 파라미터값(Customer)에 customerEmail가 있으면 /customer/getPaymentList 에서 사용
	// 파라미터값(Customer)에 customerEmail가 없으면 /staff/getPaymentListByStaff 에서 사용
	List<Payment> paymentList(Customer customer);
	
	// /customer/getPaymentList & /staff/getPaymentListByStaff 
	// paymentNo에 연결된 order 정보와 goods/category 정보 리스트 출력시 사용 (결제번호 별 상세보기)
	List<Map<String,Object>> selectPayInfoListByPaymentNo(Integer paymentNo);	
}
