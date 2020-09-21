package com.jyr.sms.other;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
/**
 * 消息监听类
 * @author Administrator
 */
@Component
public class SmsListener {
	@Autowired
	private SendSmsUtils smsUtil;
	
	@JmsListener(destination="sms")
	public void sendSms(Map<String,String> map){		
		try {
//			System.out.println(map);
			smsUtil.sendSmsCode(
					map.get("PhoneNumbers"), 
					map.get("SignName"),
					map.get("TemplateCode"),
					map.get("TemplateParam")  );					 	
		} catch (Exception e) {
			e.printStackTrace();			
		}		
	}
	
	@JmsListener(destination="smsOrder")
	public void sendSmsOrder(Map<String,String> map){		
		try {
//			System.out.println(map);
			smsUtil.sendSmsCode(
					map.get("PhoneNumbers"), 
					map.get("SignName"),
					map.get("TemplateCode"),
					map.get("TemplateParam")  );					 	
		} catch (Exception e) {
			e.printStackTrace();			
		}		
	}
	
	
}

