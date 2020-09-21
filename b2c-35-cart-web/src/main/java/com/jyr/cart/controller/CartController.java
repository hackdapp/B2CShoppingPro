package com.jyr.cart.controller;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.jyr.cart.service.CartService;
import com.jyr.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout = 10000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	/**
	 * 判断用户是否登陆
	 * 
	 * @return
	 */
	@RequestMapping("/getUserName")
	public Result getUserName() {
		// 当前登录人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		
		return new Result(true,username);
	}

	//获得购物车列表
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		
		 
		// 当前登录人账号
//		String username ="anonymousUser";
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
//		System.out.println("当前登录人：" + username);

		String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		if (username.equals("anonymousUser")) {// 如果未登录
			// 从cookie中提取购物车
//			System.out.println("从cookie中提取购物车");
			return cartList_cookie;

		} else {// 如果已登录
				// 获取redis购物车
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
//			System.out.println("cartService.findCartListFromRedis");
			if (cartList_cookie.size() > 0) {// 判断当本地购物车中存在数据
				// 得到合并后的购物车
				List<Cart> cartList = cartService.mergeCartList(cartList_cookie, cartList_redis);
				// 将合并后的购物车存入redis
				cartService.saveCartListToRedis(username, cartList);
				// 本地购物车清除
//				util.CookieUtil.deleteCookie(request, response, "cartList");
				Cookie cookie = new Cookie("cartList", null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
//				System.out.println("执行了合并购物车的逻辑");
				return cartList;
			}
			return cartList_redis;
		}

	}

	
	//gooditem加入购物车
//	@CrossOrigin(origins="http://localhost:9005")
	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin
	public Result addGoodsToCartList(Long itemId, Integer num) {
		// response.setHeader("Access-Control-Allow-Origin",
		// "http://localhost:9105");//可以访问的域(当此方法不需要操作cookie)
//		 response.setHeader("Access-Control-Allow-Credentials",
//		 "true");//如果操作cookie，必须加上这句话

		// 当前登录人账号
//		String name ="anonymousUser";
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
//		System.out.println("当前登录人：" + name);
		try {
			// 提取购物车
			List<Cart> cartList = findCartList();
			// 调用服务方法操作购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (name.equals("anonymousUser")) {// 如果未登录
				// 将新的购物车存入cookie
				String cartListString = JSON.toJSONString(cartList);
				//util.CookieUtil.setCookie(request, response, "cartList", cartListString, 3600 * 24, "UTF-8");
				
				//存cookie
				Cookie cookie = new Cookie("cartList", cartListString);
				cookie.setMaxAge(3600*24);
				response.addCookie(cookie);
				
//				System.out.println("向cookie存储购物车");
			} else {// 如果登录
				//删除cookies
				Cookie cookie = new Cookie("cartList", null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
				
				cartService.saveCartListToRedis(name, cartList);
			}
			return new Result(true, "存入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "存入购物车失败");
		}

	}

}
