package com.jyr.user.service.impl;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jyr.mapper.TbUserMapper;
import com.jyr.pojo.TbUser;
import com.jyr.pojo.TbUserExample;
import com.jyr.pojo.TbUserExample.Criteria;
import com.jyr.user.service.UserService;

import entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private TbUserMapper tbUserMapper;
	
	@Autowired
	private RedisTemplate<String , Object> redisTemplate;	
	
	@Autowired
	private JmsTemplate jmsTemplate;	
	
	@Autowired
	private Destination smsDestination;	

	@Value("${template_code}")
	private String template_code;
	
	@Value("${sign_name}")
	private String sign_name;
	
	
	/**
	 * 根据用户ID查用户基本信息
	 */
	public TbUser findUserByUserId(String userId) {
		
		
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(userId);
		List<TbUser> list = tbUserMapper.selectByExample(example);
		TbUser tbUser = list.get(0);
//		System.out.println("user:"+tbUser);
		return tbUser;
	}


	/**
	 * 生成短信验证码
	 */
	public void createSmsCode(final String phone){		
		//生成6位随机数
		final String code =  (long) (Math.random()*1000000)+"";
		System.out.println("验证码："+code);
		//存入缓存
		redisTemplate.boundHashOps("smscode").put(phone, code);
		//发送到activeMQ		
		jmsTemplate.send(smsDestination, new MessageCreator() {			
			@Override
			public Message createMessage(Session session) throws JMSException {	
				
								
				MapMessage mapMessage = session.createMapMessage();			
				mapMessage.setString("PhoneNumbers", phone);//手机号
				mapMessage.setString("SignName", "北京联创广汇");//模板编号
				mapMessage.setString("TemplateCode", "SMS_172883334");//签名				
				Map m=new HashMap<>();
				m.put("code", code);				
				mapMessage.setString("TemplateParam", JSON.toJSONString(m));//参数
				return mapMessage;
			}
		});				
	}

	
	/**
	 * 判断验证码是否正确
	 */
	public boolean  checkSmsCode(String phone,String code){
		//得到缓存中存储的验证码
		String sysCode = (String) redisTemplate.boundHashOps("smscode").get(phone);
		if(sysCode==null){
			return false;
		}
		if(!sysCode.equals(code)){
			return false;
		}
		return true;		
	}

	


	@Autowired
	private TbUserMapper userMapper;
	
	/**
	 * 查询全部
	 */
	@Override
	public List<TbUser> findAll() {
		return userMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbUser> page=   (Page<TbUser>) userMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbUser user) {
		user.setCreated(new Date());//创建日期
		user.setUpdated(new Date());//修改日期
		String password = DigestUtils.md5Hex(user.getPassword());//对密码加密
		user.setPassword(password);
		userMapper.insert(user);		
	}


	
	/**
	 * 修改
	 */
	@Override
	public void update(TbUser user,boolean isUpdatePassword){
		if(isUpdatePassword) {//如果是修改密码
			user.setUpdated(new Date());//修改日期
			String password = DigestUtils.md5Hex(user.getPassword());//对密码加密
			user.setPassword(password);
			userMapper.updateByPrimaryKey(user);
		}else{
			user.setUpdated(new Date());//修改日期
			userMapper.updateByPrimaryKey(user);
		}

	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbUser findOne(Long id){
		return userMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			userMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbUser user, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		if(user!=null){			
						if(user.getUsername()!=null && user.getUsername().length()>0){
				criteria.andUsernameLike("%"+user.getUsername()+"%");
			}
			if(user.getPassword()!=null && user.getPassword().length()>0){
				criteria.andPasswordLike("%"+user.getPassword()+"%");
			}
			if(user.getPhone()!=null && user.getPhone().length()>0){
				criteria.andPhoneLike("%"+user.getPhone()+"%");
			}
			if(user.getEmail()!=null && user.getEmail().length()>0){
				criteria.andEmailLike("%"+user.getEmail()+"%");
			}
	
		}
		
		Page<TbUser> page= (Page<TbUser>)userMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
