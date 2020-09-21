 //控制层 
app.controller('userController' ,function($scope,$controller,userService,loginService){	
	//部署时，2处跳转需要修改
	
	
	//搜索跳转
	$scope.search=function(){
		location.href="http://192.168.0.200/b2c-33-search-web-0.0.1-SNAPSHOT/search.html#?keywords="+$scope.keywords;
//		location.href="http://localhost:9033/search.html#?keywords="+$scope.keywords;
	}
	//首页跳转
	$scope.jumpWelcome=function(){
		location.href="http://192.168.0.200/b2c-32-content-web-0.0.1-SNAPSHOT/"
//			location.href="http://localhost:9032/"
	}
	
	//根据用户ID查询订单
//	findOrderByUserId=function(userId){
//		userService.findOrderByUserId(userId).success(
//			function(response){		
//				$scope.order=response;
//			}
//		);		
//	}
	
	
	$scope.showName=function(){
		loginService.showName().success(
			function(response){		
//				console.log(response.loginName)
				$scope.loginName=response.loginName;
			}
		);	
		
	
	}	
	
	//发送验证码
	$scope.sendCode=function(){
		if($scope.entity.phone==null){
			alert("请输入手机号！");
			return ;
		}		
		userService.sendCode($scope.entity.phone).success(
			function(response){
				alert(response.message);								
			}				
		);
	}

	
	//保存 
	$scope.reg=function(){		
//		alert($scope.smscode)
			userService.add( $scope.entity, $scope.smscode).success(
			function(response){	
				alert(response.message);
				if(response.success){
//					location.href = "home-index.html";
					location.href = "/b2c-35-cart-web-0.0.1-SNAPSHOT/cart.html";
				}
			}		
		);				
	}

})