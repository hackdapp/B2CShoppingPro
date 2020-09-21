app.controller('payController' ,function($scope ,$location,payService,orderService,cartService){
	//部署时，3处跳转需要修改
	
	
	//更改订单开票状态
	//invoice_type 0不开票；1开票；2已开票
	$scope.updateTbOrderTicketStatus=function(tbOrderId,status){
		orderService.updateTbOrderTicketStatus(tbOrderId,status).success(
			function(response){
				alert(response.message)
//				$scope.itemList=response;
//				console.log(response.message);
//				if(response.success){
//					$scope.findOrderByStatus(2);
//				}else{
//					alert(response.message)
//				}
			}			
		);
	}
	
	
	// 判断用户是否登陆
	$scope.getUserName = function() {
		cartService.getUserName().success(function(response) {
			$scope.uname=response.message
		});
	}
	
		//查看订单
	$scope.showOrder=function(){
		location.href="/b2c-36-user-web-0.0.1-SNAPSHOT/home-index.html"
//			location.href="http://192.168.0.200/b2c-36-user-web-0.0.1-SNAPSHOT/home-index.html"
//			location.href="http://localhost:9036/home-index.html"
	}
	
	//首页跳转
	$scope.jumpWelcome=function(){
		location.href="http://192.168.0.200/b2c-32-content-web-0.0.1-SNAPSHOT/"
//			location.href="http://localhost:9032/"
	}
	
	
	//取消支付
	$scope.cancolPay=function(){
		location.href="payfail.html";
	}
	//更新订单状态
	$scope.updateTbOrderStatus=function(tbOrderId,status){
		orderService.updateTbOrderStatus(tbOrderId,status).success(
			function(response){
//				$scope.itemList=response;
//				console.log(response.message);
				if(response.success){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					alert(response.message)
				}
			}			
		);
	}
	
//	$scope.createNative=function(){
//		payService.createNative().success(
//			function(response){
//				
//				//显示订单号和金额
//				$scope.money= (response.total_fee/100).toFixed(2);
//				$scope.out_trade_no=response.out_trade_no;
//				
//				//生成二维码
//				 var qr=new QRious({
//					    element:document.getElementById('qrious'),
//						size:250,
//						value:response.code_url,
//						level:'H'
//			     });
//				 
//				 queryPayStatus();//调用查询
//				
//			}	
//		);	
//	}
	
	
	
		
	$scope.createNative=function(){
		$scope.out_trade_no =  $location.search()['orderId'];
//		alert("out_trade_no:"+$scope.out_trade_no)
		
		payService.createNative($scope.out_trade_no).success(
			function(response){
				
//				//显示订单号和金额
				$scope.money= (response.total_fee);
//				$scope.money= (response.total_fee/100).toFixed(2);
//				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:"www.baidu.com",
						level:'H'
			     });
				
			}	
		);	
	}
	
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){
				if(response.success){
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						$scope.createNative();//重新生成二维码
					}else{
						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
	//重新付款
//	"reStartPay(order.tbOrderId)"
	$scope.reStartPay=function(tbOrderId){
//		location.href = "pay.html#?orderId="+tbOrderId;
		location.href = "http://192.168.0.200/b2c-35-cart-web-0.0.1-SNAPSHOT/pay.html#?orderId="+tbOrderId;
	}
	
});