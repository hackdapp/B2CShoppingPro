package com.jyr.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.jyr.pojo.TbOrder;
import com.jyr.pojo.TbOrderItem;

public class Order implements Serializable{
	
	private TbOrder tbOrder;
	private List<TbOrderItem> itemList;
	private String tbOrderId;
	
	
	
	public String getTbOrderId() {
		return tbOrderId;
	}


	public void setTbOrderId(String tbOrderId) {
		this.tbOrderId = tbOrderId;
	}


	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	public Order(TbOrder tbOrder, List<TbOrderItem> itemList) {
		super();
		this.tbOrder = tbOrder;
		this.itemList = itemList;
	}


	public TbOrder getTbOrder() {
		return tbOrder;
	}
	public void setTbOrder(TbOrder tbOrder) {
		this.tbOrder = tbOrder;
	}
	public List<TbOrderItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbOrderItem> itemList) {
		this.itemList = itemList;
	}


	@Override
	public String toString() {
		return "Order [tbOrder=" + tbOrder + ", itemList=" + itemList + "]";
	}

	
	

}
