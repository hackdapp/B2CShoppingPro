package com.jyr.cart.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jyr.pojo.TbOrder;
import com.jyr.pojogroup.Order;
import com.jyr.user.service.OrderService;
import com.jyr.user.service.WeixinPayService;

import entity.Result;
import util.IdWorker;

/**
 * 支付控制层
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/pay")
public class PayController {
	@Reference
	private  WeixinPayService weixinPayService;
	
	@Reference
	private OrderService orderService;
	
	/**
	 * 生成二维码
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/createNative")
	public Map createNative(Long out_trade_no){
		//查该订单金额后，继续支付
		TbOrder tbOrder = orderService.findOne(out_trade_no);
		BigDecimal payment = tbOrder.getPayment();
//		return weixinPayService.createNative(out_trade_no+"","1");		
		return weixinPayService.createNative(out_trade_no+"",payment.toString());		
	}
	
	/**
	 * 查询支付状态
	 * @param out_trade_no
	 * @return
	 */
	@RequestMapping("/queryPayStatus")
	public Result queryPayStatus(String out_trade_no){
		/*
		Result result=null;	
		int x=0;	
		while(true){
			//调用查询接口
			Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);
			if(map==null){//出错			
				result=new  Result(false, "支付出错");
				break;
			}			
			if(map.get("trade_state").equals("SUCCESS")){//如果成功				
				result=new  Result(true, "支付成功");
				break;
			}			
			try {
				Thread.sleep(3000);//间隔三秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
			
			x++;
			if(x>=100){
				result=new  Result(false, "二维码超时");
				break;
			}

		}
		return result;
		*/
		return null;
	}

}

