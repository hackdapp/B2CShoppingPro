package com.jyr.manager.controller;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jyr.pojo.TbOrder;
import com.jyr.pojo.TbOrderItem;
import com.jyr.pojogroup.Order;
import com.jyr.user.service.OrderService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/order")
public class OrderController {

	@Reference
	private OrderService orderService;
	
	
	/**
	 * 根据订单号查询订单
	 */
	@RequestMapping("/findTbOrderByTbOrderId")
	public TbOrder findTbOrderByTbOrderId(String tbOrderId) {
		return orderService.findTbOrderByTbOrderId(tbOrderId);
	}
	
	/**
	 * 更改订单开票状态
	 * invoice_type 0不开票；1开票；2已开票
	 */
	@RequestMapping("/updateTbOrderTicketStatus")
	public Result updateTbOrderTicketStatus(String tbOrderId,String status) {
		return orderService.updateTbOrderTicketStatus(tbOrderId,status);
	}
	
	/**
	 * 更新订单状态
	 */
	@RequestMapping("/updateTbOrderStatus")
	public Result updateTbOrderStatus(String tbOrderId,String status){
		return orderService.updateTbOrderStatus(tbOrderId,status);
	}
	
	/**
	 * 根据订单号查订单item
	 */
	@RequestMapping("/findItemListBytbOrderId")
	public List<TbOrderItem> findItemListBytbOrderId(Long tbOrderId){
		return orderService.findItemListBytbOrderId(tbOrderId);
	}
	
	
	/**
	 * 根据订单状态查订单
	 */
	@RequestMapping("/findOrderByStatus")
	public List<Order> findOrderByStatus(String status) {
		return orderService.findOrderByStatus(status);
	}
	
	/**
	 * 根据订单号取消订单
	 */
	@RequestMapping("/delOrderByOrderId")
	public Result delOrderByOrderId(String orderId) {
		try {
			Long orderIdTemp = Long.parseLong(orderId);
			Long[] ll = {orderIdTemp};
			orderService.delete(ll);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	/**
	 * 根据用户ID查询订单
	 */
	@RequestMapping("/findOrderByUserId")
	public List<Order> findOrderByUserId(String userId) {
		
		return orderService.findOrderByUserId(userId);
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbOrder> findAll(){			
		return orderService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return orderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	/**
	 * 增加
	 * @param order
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbOrder order){
		//获取当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		order.setUserId(username);
		order.setSourceType("2");//订单来源  PC
		try {
			Long orderId = orderService.add(order);
			return new Result(true, orderId+"");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}

	
	/**
	 * 修改
	 * @param order
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbOrder order){
		try {
			orderService.update(order);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public TbOrder findOne(Long id){
		return orderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			orderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbOrder order, int page, int rows  ){
		return orderService.findPage(order, page, rows);		
	}
	
}
