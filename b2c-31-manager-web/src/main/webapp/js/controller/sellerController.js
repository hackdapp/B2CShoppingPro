 //控制层 
app.controller('sellerController' ,function($scope,$controller   ,sellerService,orderService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
	//根据订单号查询订单
	$scope.findTbOrderByTbOrderId=function(tbOrderId){
		orderService.findTbOrderByTbOrderId(tbOrderId).success(
			function(response){
				$scope.tbOrder=response;
				console.log(response);
			}			
		);
	}
	
	
	$scope.ticketStatus = ["不开票","待开票","已开票","关闭"];
	
	//更改订单开票状态
	//invoice_type 0不开票；1开票；2已开票
	$scope.updateTbOrderTicketStatus=function(tbOrderId,invoiceType,status){
		orderService.updateTbOrderTicketStatus(tbOrderId,invoiceType).success(
			function(response){
				
				if(response.success){
//					$scope.reloadList();//刷新列表
//					$scope.selectIds=[];//清空ID集合
					
					$scope.findOrderByStatus(status);
				}else{
					alert(response.message)
				}	
			}			
		);
	}
	
	//更新订单状态
	$scope.updateTbOrderStatus=function(tbOrderId,status){
		orderService.updateTbOrderStatus(tbOrderId,status).success(
			function(response){
//				$scope.itemList=response;
//				console.log(response.message);
				if(response.success){
					$scope.findOrderByStatus(2);
				}else{
					alert(response.message)
				}
			}			
		);
	}
	
	//根据订单号查订单item
	$scope.findItemListBytbOrderId=function(tbOrderId){
		orderService.findItemListBytbOrderId(tbOrderId).success(
			function(response){
				$scope.itemList=response;
				console.log(response);
			}			
		);
	}
	
	
	
	//根据订单状态查订单
	$scope.findOrderByStatus=function(status){
		orderService.findOrderByStatus(status).success(
			function(response){
				$scope.orders=response;
//				console.log(response);
			}			
		);
	}
	
	
    $scope.updateStatus=function(sellerId,status){
		sellerService.updateStatus(sellerId,status).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
				}else{
					alert("失败");
				}				
			}
		);
	}

    
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		sellerService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		sellerService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		sellerService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=sellerService.update( $scope.entity ); //修改  
		}else{
			serviceObject=sellerService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		sellerService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		sellerService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
