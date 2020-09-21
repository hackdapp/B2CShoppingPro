package com.jyr.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.jyr.pojo.TbGoods;
import com.jyr.pojo.TbGoodsDesc;
import com.jyr.pojo.TbItem;

public class Goods implements Serializable{
private TbGoods goods;//商品SPU
	private TbGoodsDesc goodsDesc;//商品扩展
	private List<TbItem> itemList;//商品SKU列表	
	
	public Goods() {
		super();
	}
	
	public Goods(TbGoods goods, TbGoodsDesc goodsDesc, List<TbItem> itemList) {
		super();
		this.goods = goods;
		this.goodsDesc = goodsDesc;
		this.itemList = itemList;
	}
	public TbGoods getGoods() {
		return goods;
	}
	public void setGoods(TbGoods goods) {
		this.goods = goods;
	}
	public TbGoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(TbGoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<TbItem> getItemList() {
		return itemList;
	}
	public void setItemList(List<TbItem> itemList) {
		this.itemList = itemList;
	}



	
}

