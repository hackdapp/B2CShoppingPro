//品牌服务层
app.service('brandService',function($http){
	//下拉品牌列表数据
	this.selectOptionList=function(){
		return $http.get('../brand/selectOptionList.do');
	}
	
	
	//读取列表数据绑定到表单中
	this.findAll=function(){
		return $http.get('../brand/findAll.do');		
	}

	//分页服务
	this.findPage=function(page,rows){
		console.log('brandService')
		return $http.get('../brand/findPage.do?page='+page+'&rows='+rows);		
	}
	
	//保存服务 
	this.save=function(methodName,entity){
			return $http.post('../brand/'+ methodName +'.do',entity )
	}

	

	//查询单个服务 
	this.findOne=function(id){
		return $http.get('../brand/findOne.do?id='+id)				
	}
		 
	//批量删除服务 
	this.dele=function(selectIds){			
			//获取选中的复选框			
			return $http.get('../brand/delete.do?ids='+$scope.selectIds)
	}
		
	//条件查询服务 
	this.search=function(page,rows,searchEntity){
		console.log('brandService')
		return $http.post('../brand/search.do?page='+page+"&rows="+rows, searchEntity)			
	}
	



	
});
