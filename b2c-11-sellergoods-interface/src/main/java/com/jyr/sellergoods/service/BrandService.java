package com.jyr.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.jyr.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	// 查询品牌
	public List<TbBrand> findAll();

	// 返回分页列表
	public PageResult findPage(int pageNum, int pageSize);

	// 增加品牌
	public void add(TbBrand brand);
	
	//查询一个品牌
	public TbBrand findOne(Long id);

	// 修改品牌
	public void update(TbBrand brand);
	
	//批量删除
	public void delete(Long [] ids);
	
	//条件查询
	public PageResult findPage(TbBrand brand, int pageNum,int pageSize);
	
	/**
	 * 品牌下拉框数据
	 */
	List<Map> selectOptionList();

}
