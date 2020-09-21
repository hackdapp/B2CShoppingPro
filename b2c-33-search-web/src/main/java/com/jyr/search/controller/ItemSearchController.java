package com.jyr.search.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.jyr.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
	@Reference(timeout=6000)
	private ItemSearchService itemSearchService;
	
	@RequestMapping("/search")
	public Map<String, Object> search(@RequestBody Map searchMap ){
		Map<String, Object> map = new HashMap<String, Object>();	
		return  itemSearchService.search(searchMap);
	}	
}
