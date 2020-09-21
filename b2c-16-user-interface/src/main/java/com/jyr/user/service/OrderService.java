package com.jyr.user.service;
import java.util.List;

import com.jyr.pojo.TbOrder;
import com.jyr.pojo.TbOrderItem;
import com.jyr.pojogroup.Order;

import entity.PageResult;
import entity.Result;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {
	
	/**
	 * 更改订单开票状态
	 */
	public Result updateTbOrderTicketStatus(String tbOrderId,String status);
	
	
	/**
	 * 根据订单号查询订单
	 */
	public TbOrder findTbOrderByTbOrderId(String tbOrderId) ;
		
	
	/**
	 * 更新订单状态
	 */
	public Result updateTbOrderStatus(String tbOrderId,String status);
	
	/**
	 * 根据订单号查订单item
	 */
	public List<TbOrderItem> findItemListBytbOrderId(Long tbOrderId);
	
	/**
	 * 根据订单状态查订单
	 */
	
	public List<Order>findOrderByStatus(String status);
	
	/**
	 * 根据用户ID查询订单列表
	 */
	public List<Order> findOrderByUserId(String userId);

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加订单,返回订单号
	*/
	public Long add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long [] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum,int pageSize);
	
}
