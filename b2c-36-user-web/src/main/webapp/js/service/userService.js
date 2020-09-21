//服务层
app.service('userService',function($http){
	
	//更新订单状态
	this.updateTbOrderStatus=function(tbOrderId,status){
		return $http.get('./order/updateTbOrderStatus.do?tbOrderId='+tbOrderId+"&status="+status);
	}
	
	//根据订单ID删除订单
	this.delOrderByOrderId=function(orderId){
		return  $http.get('./order/delOrderByOrderId.do?orderId='+orderId );
	}
	
	
	//根据用户ID查询订单
	this.findOrderByUserId=function(userId){
		return  $http.get('./order/findOrderByUserId.do?userId='+userId );
	}
	
	//根据用户ID查询地址
	this.findAddrByUserId=function(userId){
		return  $http.get('./address/findAddrByUserId.do?userId='+userId );
	}
	
	
	//根据用户ID查用户基本信息
	this.findUserByUserId=function(userId){
		return  $http.get('./user/findUserByUserId.do?userId='+userId );
	}
	

	
	//增加 
	this.add=function(entity,smscode){
		return  $http.post('./user/add.do?smscode='+smscode ,entity );
	}
	//发送验证码
	this.sendCode=function(phone){
		return $http.get("./user/sendCode.do?phone="+phone);
	}
	    	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('./user/findAll.do');		
	}
	//分页 
	this.findPage=function(page,rows){
		return $http.get('./user/findPage.do?page='+page+'&rows='+rows);
	}
	//查询实体
	this.findOne=function(id){
		return $http.get('./user/findOne.do?id='+id);
	}
	//修改 
	this.update=function(entity,isUpdatePassword){
		return  $http.post('./user/update.do?isUpdatePassword='+isUpdatePassword,entity);
	}
	//删除
	this.dele=function(ids){
		return $http.get('./user/delete.do?ids='+ids);
	}
	//搜索
	this.search=function(page,rows,searchEntity){
		return $http.post('./user/search.do?page='+page+"&rows="+rows, searchEntity);
	}  
	
	
	
});
