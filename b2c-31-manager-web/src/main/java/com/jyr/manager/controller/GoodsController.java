package com.jyr.manager.controller;
import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jyr.page.service.ItemPageService;
import com.jyr.pojo.TbGoods;
import com.jyr.pojo.TbItem;
import com.jyr.pojogroup.Goods;
import com.jyr.sellergoods.service.GoodsService;

import entity.PageResult;
import entity.Result;
/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference(timeout=10000)
	private GoodsService goodsService;
	
	@Reference
	ItemPageService itemPageService;
	
	@Autowired
	private Destination queueSolrDestination;//用于发送solr导入的消息
	
	@Autowired
	private Destination queueTextDestinationDele;//用于删除solr
	
	@Autowired
	private Destination topicPageDeleteDestination;//用于删除静态网页的消息


	@Autowired
	private JmsTemplate jmsTemplate;


	
	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids,String status){
		try {
			goodsService.updateStatus(ids, status);						
			//按照SPU ID查询 SKU列表(状态为1)		
			if(status.equals("1")){//审核通过
				List<TbItem> itemList = goodsService.findItemListByGoodsIdIsDelete(ids);						
				//调用搜索接口实现数据批量删除
				if(itemList.size()>0){				
//					itemSearchService.importList(itemList);		
					
					final String jsonString = JSON.toJSONString(itemList);		
					jmsTemplate.send(queueSolrDestination, new MessageCreator() {	//pinyougou_queue_solr
						@Override
						public Message createMessage(Session session) throws JMSException {							
								return session.createTextMessage(jsonString);
						}
					});		
				}else{
					System.out.println("没有明细数据");
				}				
				//静态页生成
				for(Long goodsId:ids){
					itemPageService.genItemHtml(goodsId);
				}				
			}					
			return new Result(true, "修改状态成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改状态失败");
		}
	}


	
	
	@RequestMapping("/searchByName")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		//获取商家ID
		//String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		//添加查询条件 
		//goods.setSellerId(sellerId);		
		return goodsService.findPage(goods, page, rows);		
	}
	
	@RequestMapping("/search")
	public PageResult search(int page, int rows  ){
		return goodsService.findPage(page, rows);		
	}


	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取商家ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			System.out.println("sellerId:"+sellerId);
			//添加查询条件 
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
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
	public Goods findOne(@RequestParam(value="id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			
//			
//			//删除详细页和solr记录
//				List<TbItem> itemList = goodsService.findItemListByGoodsIdIsDelete(ids);						
//				//调用搜索接口实现数据批量导入
//				if(itemList.size()>0){				
////					itemSearchService.importList(itemList);		
//					
//					final String jsonString = JSON.toJSONString(itemList);		
//					jmsTemplate.send(queueTextDestinationDele, new MessageCreator() {	
//						@Override
//						public Message createMessage(Session session) throws JMSException {							
//								return session.createTextMessage(jsonString);
//						}
//					});		
//				}else{
//					System.out.println("没有明细数据");
//				}				
//				//静态页生成
//				for(Long goodsId:ids){
//					itemPageService.genItemHtml(goodsId);
//				}	
				
				//删除详细页和solr记录
//				List<TbItem> itemList = goodsService.findItemListByGoodsIdIsDelete(ids);						
				//调用搜索接口实现数据批量导入
				if(ids.length>0){				
//					itemSearchService.importList(itemList);		
					
					final String jsonString = JSON.toJSONString(ids);		
					jmsTemplate.send(queueTextDestinationDele, new MessageCreator() {	
						@Override
						public Message createMessage(Session session) throws JMSException {							
								return session.createTextMessage(jsonString);
						}
					});		
				}else{
					System.out.println("没有明细数据");
				}				
				//静态页删除
				itemPageService.deleteItemHtml(ids);
				for(Long goodsId:ids){
					//itemPageService.genItemHtml(goodsId);
				}	
			
			
			goodsService.delete(ids);
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
	
	
}
