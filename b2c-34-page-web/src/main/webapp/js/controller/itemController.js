//��Ʒ��ϸҳ�����Ʋ㣩
app.controller('itemController',function($scope,$http,$location){
	//部署时，2处跳转需要修改
	
	
	//��������
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}	
	
	$scope.specificationItems={};//��¼�û�ѡ��Ĺ��
	//�û�ѡ����
	$scope.selectSpecification=function(name,value){	
		$scope.specificationItems[name]=value;
		searchSku();//��ȡsku
	}	
	//�ж�ĳ���ѡ���Ƿ��û�ѡ��
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}

	//����Ĭ��SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];		
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}


	//ƥ����������
	matchObject=function(map1,map2){		
		for(var k in map1){
			if(map1[k]!=map2[k]){
				return false;
			}			
		}
		for(var k in map2){
			if(map2[k]!=map1[k]){
				return false;
			}			
		}
		return true;		
	}


	//��ѯSKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=skuList[i];
				return ;
			}			
		}	
		$scope.sku={id:0,title:'--------',price:0};//���û��ƥ���		
	}

	
	//添加商品到购物车
	$scope.addToCart=function(){
//		alert('SKUID:'+$scope.sku.id );		
//		alert('NUM:'+$scope.num );		
		
				$http.get('http://192.168.0.200/b2c-35-cart-web-0.0.1-SNAPSHOT/cart/addGoodsToCartList.do?itemId='
//				$http.get('http://192.168.1.112:9035/cart/addGoodsToCartList.do?itemId='
				+$scope.sku.id+'&num='+$scope.num ,{'withCredentials':true} ).success(
					function(response){
						if(response.success){
							location.href='http://192.168.0.200/b2c-35-cart-web-0.0.1-SNAPSHOT/cart.html';//跳转到购物车页面					
//							location.href='http://192.168.1.112:9035/cart.html';//跳转到购物车页面					
						}else{
							alert(response.message);
						}					
					}						
				);	
		
	}



});
