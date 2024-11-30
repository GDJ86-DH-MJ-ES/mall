<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>      
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script>
	$(document).ready(function() {
		// 비밀번호 확인 함수 (true / false 반환)
		function checkPw() {
			if($("#checkPw").val() === null || $("#checkPw").val().trim() === '') {
				alert("비밀번호를 입력해주세요.");
				return false;
			}
			if(!($("#prePw").val() === $("#checkPw").val())) {
				alert("비밀번호가 틀렸습니다");
				return false;
			} 
			return true;
		}
		
		// 비밀번호 변경 : (1) 사용자가 입력한 비밀번호가 맞는지 확인 (2) 맞다면 비밀번호 변경 URL로 이동
		$("#modifyCustomerPwBtn").click(function() {
			// (1) 비밀번호 확인
			if(!checkPw()) {
				return;
			}
			
			// (2) 비밀번호 변경 폼 이동
			$('#checkPwForm').attr('action', '${pageContext.request.contextPath}/customer/postModifyCustomerPw');
			$('#checkPwForm').submit(); // 폼 제출
		});
	
		// 회원 탈퇴 : (1) Confirm으로 정말로 탈퇴할건지 다시 묻기 (2) 사용자가 입력한 비밀번호가 맞는지 확인 (3) 회원 탈퇴 URL로 이동 
		$("#removeCustomerBtn").click(function() {
			
			// (1) Confirm으로 정말로 탈퇴할건지 다시 묻기
			let checkDecision = confirm('정말로 탈퇴 하시겠습니까?');
			
			if(!checkDecision) {
				return;
			}
			
			// (2) 비밀번호 확인
			if(!checkPw()) {
				return;
			}
			
			// (3)회원 탈퇴 URL로 이동 
			$('#checkPwForm').attr('action', '${pageContext.request.contextPath}/customer/removeCustomer');
			$('#checkPwForm').submit(); // 폼 제출
			
	   	});
		
		// 만약, 회원수정, 회원탈퇴 쿼리가 실패했다면( = #formResult != null), alert창 출력
		if($("#formResult").val()) {
			alert($("#formResult").val());
		}
		
	});
	</script>
	<style>

	</style>
<meta charset="UTF-8">
<title>${fn:substringBefore(customer.customerEmail, "@")}</title>

</head>
<body>
<div class="row">
	<!-- leftbar -->
	<div class="col-sm-2 p-0">
		<div >
			<c:import url="/WEB-INF/view/inc/customerLeftMenu.jsp"></c:import>
		</div>
	</div>
	
	<!-- main -->
	<div class="col-sm-10 p-0">
		<!-- header -->
		<div>
			<c:import url="/WEB-INF/view/inc/header.jsp"></c:import>
		</div>
					
		<!-- main -->
		<div style="margin-left: 80px; margin-top: 30px;">
			<h3>Personal_Info</h3>
		</div>
		<div class="d-flex flex-column flex-md-row p-4 gap-4 py-md-4 align-items-center" style="margin-left: 110px;">
	  		<div class="card" style="width: 1000px;">
				<div style="margin-left: 80px; margin-top: 30px;">
					<h3>${fn:substringBefore(customer.customerEmail, "@")} 님 안녕하세요.</h3>
				</div>
				
				<div class="card-body d-flex flex-column flex-md-row p-4 gap-4 py-md-2 align-items-center" style="margin-left: 110px; margin-top: 25px;">
		                <div class="mb-3 mt-3">
		                	 <form id="checkPwForm" method="post">
            	 		        <div class="row" style="margin-top:5px; width: 535px;">
				                	<!-- 고객의 기본 정보 출력 -->
				                    <label class="form-label">Email :</label> 
				                    <input name="customerEmail" type="text" class="form-control" value="${customer.customerEmail}" readonly>
				                    <label class="form-label">Gender :</label> 
				                    <input type="text" class="form-control" value="${customer.customerGender}" readonly>
				                    <label class="form-label">Birth :</label> 
				                    <input type="text" class="form-control" value="${customer.customerBirth}" readonly>
		                   			 <!-- 비밀번호가 맞는지 확인 및 쿼리 실행결과가 1이 아닐때(쿼리실행실패 = 비밀번호 불일치), alert 창 출력 -->
				                    <label class="form-label">PassWord :</label> 
				                    <input id="checkPw" name="checkPw" type="password" class="form-control" placeholder="비밀번호 확인">
                		        </div>
				                    <!-- 원래 비밀번호 -->
				                    <input id="prePw" name="prePw" type="hidden" value="${customer.customerPw}">

				                    <button id="modifyCustomerPwBtn" type="button" class="btn btn-sm btn-outline-primary" style="margin-right: 7px;">비밀번호 변경</button>
				                    <button id="removeCustomerBtn" type="button" class="btn btn-sm btn-outline-danger" style="margin-right: 7px;">회원 탈퇴</button>

			                    	<input id="formResult" type="hidden" value="${alertFailedMsg}">
		                      </form>
		                </div>
    			</div>
    			
	  		</div>
		</div>
	</div>
</div>
<!-- footer -->

</body>
</html>