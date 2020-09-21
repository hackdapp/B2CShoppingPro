//购物车服务层
app.service('orderService',function($http){
	
	//根据订单号查询订单
	this.findTbOrderByTbOrderId=function(tbOrderId){
		return $http.get('../order/findTbOrderByTbOrderId.do?tbOrderId='+tbOrderId);
	}
	
	//更改订单开票状态
	//invoice_type 0不开票；1开票；2已开票
	this.updateTbOrderTicketStatus=function(tbOrderId,status){
		return $http.get('../order/updateTbOrderTicketStatus.do?tbOrderId='+tbOrderId+"&status="+status);
	}
	
	
	//更新订单状态
	this.updateTbOrderStatus=function(tbOrderId,status){
		return $http.get('../order/updateTbOrderStatus.do?tbOrderId='+tbOrderId+"&status="+status);
	}
	
	//根据订单号查订单item
	this.findItemListBytbOrderId=function(tbOrderId){
		return $http.get('../order/findItemListBytbOrderId.do?tbOrderId='+tbOrderId);
	}
	
	
	 // 根据订单状态查订单
	this.findOrderByStatus=function(status){
		return $http.get('../order/findOrderByStatus.do?status='+status);
	}
	
	
	//保存订单
	this.submitOrder=function(order){
		return $http.post('http://localhost:9036/order/add.do',order);		
	}

	//判断用户是否登陆
	this.getUserName=function(){
		return $http.get('cart/getUserName.do');
	}
	
	//购物车列表
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');
	}
	
	//添加商品到购物车
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	//求合计数
	this.sum=function(cartList){
		var totalValue={totalNum:0,totalMoney:0 };
			
		for(var i=0;i<cartList.length ;i++){
			var cart=cartList[i];//购物车对象
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem=  cart.orderItemList[j];//购物车明细
				totalValue.totalNum+=orderItem.num;//累加数量
				totalValue.totalMoney+=orderItem.totalFee;//累加金额				
			}			
		}
		return totalValue;
		
	}
	
	//获取当前登录账号的收货地址
	this.findAddressList=function(){
		return $http.get('address/findListByLoginUser.do');
	}
	
	//提交订单
	this.submitOrder=function(order){
		return $http.post('order/add.do',order);		
	}
	
	
});