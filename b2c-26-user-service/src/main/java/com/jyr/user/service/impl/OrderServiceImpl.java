package com.jyr.user.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jyr.mapper.TbOrderItemMapper;
import com.jyr.mapper.TbOrderMapper;
import com.jyr.pojo.TbOrder;
import com.jyr.pojo.TbOrderExample;
import com.jyr.pojo.TbOrderItem;
import com.jyr.pojo.TbOrderItemExample;
import com.jyr.pojo.TbOrderExample.Criteria;
import com.jyr.pojogroup.Cart;
import com.jyr.pojogroup.Order;
import com.jyr.user.service.OrderService;

import entity.PageResult;
import entity.Result;
import util.IdWorker;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl implements OrderService {
	

	@Autowired
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private IdWorker idWorker;
	
	@Autowired
	private JmsTemplate jmsTemplate;	
	
	@Autowired
	private Destination smsOrderDestination;	

//	@Value("${template_code_order}")
//	private String template_code;
//	
//	@Value("${sign_name}")
//	private String sign_name;
	
	
	/**
	 * 更改订单开票状态
	 * invoice_type 0不开票；1开票；2已开票
	 */
	public Result updateTbOrderTicketStatus(String tbOrderId,String status) {
		long ll = Long.parseLong(tbOrderId);
		try {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(ll);
			tbOrder.setInvoiceType(status);
			orderMapper.updateByPrimaryKey(tbOrder);
			return new Result(true, "开票状态更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"开票状态更新失败");
		}
	}
	
	
	/**
	 * 根据订单号查询订单
	 */
	public TbOrder findTbOrderByTbOrderId(String tbOrderId) {
		return orderMapper.selectByPrimaryKey(Long.parseLong(tbOrderId));
	}
	
	
	/**
	 * 更新订单状态
	 */
	public Result updateTbOrderStatus(String tbOrderId,String status) {
		long ll = Long.parseLong(tbOrderId);
		try {
			TbOrder tbOrder = orderMapper.selectByPrimaryKey(ll);
			tbOrder.setStatus(status);
			orderMapper.updateByPrimaryKey(tbOrder);
			
			//发短信
			if("2".equals(status)) {//如果已付款
				BigDecimal payment = tbOrder.getPayment();
				//发送短信
				System.out.println("买家已付款，请查看订单和货款，确认无误后及时发货，订单号："+tbOrderId+";货款金额："+payment.toString());
				//发送到activeMQ		
				jmsTemplate.send(smsOrderDestination, new MessageCreator() {			
					@Override
					public Message createMessage(Session session) throws JMSException {			
						MapMessage mapMessage = session.createMapMessage();			
						mapMessage.setString("PhoneNumbers", "18301521502");//手机号
						mapMessage.setString("SignName", "北京联创广汇");//模板编号
						mapMessage.setString("TemplateCode", "SMS_172883248");//签名				
						Map m=new HashMap<>();
						m.put("tbOrderId", tbOrderId);				
						m.put("payment", payment.toString());				
						mapMessage.setString("TemplateParam", JSON.toJSONString(m));//参数
						return mapMessage;
					}
				});	
			}
			
			return new Result(true, "更新成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false,"更新失败");
		}
	}
	
	
	public List<TbOrderItem> findItemListBytbOrderId(Long tbOrderId){
		TbOrderItemExample example = new TbOrderItemExample();
		com.jyr.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();
		criteria.andOrderIdEqualTo(tbOrderId);
		List<TbOrderItem> list = orderItemMapper.selectByExample(example);
		return list;
	}
	
	
	
	//根据订单状态查订单
	public List<Order>findOrderByStatus(String status){
		List<Order> list = new ArrayList<Order>();
		TbOrderExample example = new TbOrderExample();
//		example.setOrderByClause("order_id desc");//逆顺序排序
		example.setOrderByClause("create_time desc");//逆顺序排序
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(status);
		List<TbOrder> tbOrderList = orderMapper.selectByExample(example);
		for( TbOrder tbOrder : tbOrderList) {
			Long orderId = tbOrder.getOrderId();
			TbOrderItemExample example2 = new TbOrderItemExample();
			com.jyr.pojo.TbOrderItemExample.Criteria criteria2 = example2.createCriteria();
			criteria2.andOrderIdEqualTo(orderId);
			List<TbOrderItem> tbOrderItemList = orderItemMapper.selectByExample(example2);
			
			Order order = new Order();
			order.setTbOrder(tbOrder);
			order.setItemList(tbOrderItemList);
			order.setTbOrderId(orderId.toString());
			list.add(order);
		}
		return list;
	}

	/**
	 * 根据用户ID查询订单
	 */
	public List<Order> findOrderByUserId(String userId) {

//		System.out.println("userId:"+userId);
		List<Order> list = new ArrayList<Order>();

		TbOrderExample example = new TbOrderExample();
		example.setOrderByClause("create_time desc");//逆顺序排序
		Criteria criteria = example.createCriteria();
		criteria.andUserIdEqualTo(userId);
		List<TbOrder> tbOrderlist = orderMapper.selectByExample(example);

		for (TbOrder tbOrder : tbOrderlist) {
			Long orderId = tbOrder.getOrderId();
//			System.out.println("orderId="+orderId);
			TbOrderItemExample exampleItem = new TbOrderItemExample();
			com.jyr.pojo.TbOrderItemExample.Criteria createCriteria = exampleItem.createCriteria();
			createCriteria.andOrderIdEqualTo(orderId);
			List<TbOrderItem> tbOrderItemList = orderItemMapper.selectByExample(exampleItem);
			Order order = new Order();
			order.setTbOrderId(""+orderId);
			order.setTbOrder(tbOrder);
			order.setItemList(tbOrderItemList);
			list.add(order);
		}

		return list;
	}

	/**
	 * 查询全部
	 */
	@Override
	public List<TbOrder> findAll() {

		return orderMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */

//	@Autowired
//	private IdWorker idWorker;

	/**
	 * 增加订单，返回订单号
	 */
	@Override
	public Long add(TbOrder order) {

		idWorker = new IdWorker();
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
		Cart cart = cartList.get(0);
		long orderId = idWorker.nextId();
		TbOrder tborder = new TbOrder();// 新创建订单对象
		tborder.setOrderId(orderId);// 订单ID
		tborder.setUserId(order.getUserId());// 用户名
		tborder.setPaymentType(order.getPaymentType());// 支付类型
		tborder.setStatus("1");// 状态：未付款
		tborder.setCreateTime(new Date());// 订单创建日期
		tborder.setUpdateTime(new Date());// 订单更新日期
		tborder.setReceiverAreaName(order.getReceiverAreaName());// 地址
		tborder.setReceiverMobile(order.getReceiverMobile());// 手机号
		tborder.setReceiver(order.getReceiver());// 收货人
		tborder.setSourceType(order.getSourceType());// 订单来源
		
		tborder.setInvoiceType(order.getInvoiceType());// 是否开票
		tborder.setSellerId(order.getSellerId());// 税号
		tborder.setBuyerMessage(order.getBuyerMessage());// 抬头
		// 循环购物车明细
		double money = 0;
		for (TbOrderItem orderItem : cart.getOrderItemList()) {
			orderItem.setId(idWorker.nextId());
			orderItem.setOrderId(orderId);// 订单ID
			orderItem.setSellerId(cart.getSellerId());
			money += orderItem.getTotalFee().doubleValue();// 金额累加
			orderItemMapper.insert(orderItem);
		}
		tborder.setPayment(new BigDecimal(money));
		orderMapper.insert(tborder);
		redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		return tborder.getOrderId();

		/*
		 * IdWorker idWorker = new IdWorker(); //得到购物车数据 List<Cart> cartList =
		 * (List<Cart>)redisTemplate.boundHashOps("cartList").get( order.getUserId() );
		 * 
		 * for(Cart cart:cartList){ long orderId = idWorker.nextId();
		 * System.out.println("sellerId:"+cart.getSellerId()); TbOrder tborder=new
		 * TbOrder();//新创建订单对象 tborder.setOrderId(orderId);//订单ID
		 * tborder.setUserId(order.getUserId());//用户名
		 * tborder.setPaymentType(order.getPaymentType());//支付类型
		 * tborder.setStatus("1");//状态：未付款 tborder.setCreateTime(new Date());//订单创建日期
		 * tborder.setUpdateTime(new Date());//订单更新日期
		 * tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
		 * tborder.setReceiverMobile(order.getReceiverMobile());//手机号
		 * tborder.setReceiver(order.getReceiver());//收货人
		 * tborder.setSourceType(order.getSourceType());//订单来源
		 * tborder.setSellerId(cart.getSellerId());//商家ID //循环购物车明细 double money=0;
		 * for(TbOrderItem orderItem :cart.getOrderItemList()){
		 * orderItem.setId(idWorker.nextId()); orderItem.setOrderId( orderId );//订单ID
		 * orderItem.setSellerId(cart.getSellerId());
		 * money+=orderItem.getTotalFee().doubleValue();//金额累加
		 * orderItemMapper.insert(orderItem); } tborder.setPayment(new
		 * BigDecimal(money)); orderMapper.insert(tborder); }
		 * redisTemplate.boundHashOps("cartList").delete(order.getUserId());
		 */

	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbOrder order) {
		orderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbOrder findOne(Long id) {
		return orderMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			TbOrderItemExample example = new TbOrderItemExample();
			com.jyr.pojo.TbOrderItemExample.Criteria criteria = example.createCriteria();
			criteria.andOrderIdEqualTo(id);
			orderItemMapper.deleteByExample(example);
			orderMapper.deleteByPrimaryKey(id);
		}
	}

	@Override
	public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbOrderExample example = new TbOrderExample();
		Criteria criteria = example.createCriteria();

		if (order != null) {
			if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
				criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
			}
			if (order.getPostFee() != null && order.getPostFee().length() > 0) {
				criteria.andPostFeeLike("%" + order.getPostFee() + "%");
			}
			if (order.getStatus() != null && order.getStatus().length() > 0) {
				criteria.andStatusLike("%" + order.getStatus() + "%");
			}
			if (order.getShippingName() != null && order.getShippingName().length() > 0) {
				criteria.andShippingNameLike("%" + order.getShippingName() + "%");
			}
			if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
				criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
			}
			if (order.getUserId() != null && order.getUserId().length() > 0) {
				criteria.andUserIdLike("%" + order.getUserId() + "%");
			}
			if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
				criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
			}
			if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
				criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
			}
			if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
				criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
			}
			if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
				criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
			}
			if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
				criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
			}
			if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
				criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
			}
			if (order.getReceiver() != null && order.getReceiver().length() > 0) {
				criteria.andReceiverLike("%" + order.getReceiver() + "%");
			}
			if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
				criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
			}
			if (order.getSourceType() != null && order.getSourceType().length() > 0) {
				criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
			}
			if (order.getSellerId() != null && order.getSellerId().length() > 0) {
				criteria.andSellerIdLike("%" + order.getSellerId() + "%");
			}

		}

		Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

}
