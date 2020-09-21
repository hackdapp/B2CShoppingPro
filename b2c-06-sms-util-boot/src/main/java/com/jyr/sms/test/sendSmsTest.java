package com.jyr.sms.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class sendSmsTest {
	
	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	
	
	@RequestMapping("/sendsms")
	public void sendSms(){
		Map map=new HashMap<>();
		map.put("PhoneNumbers", "18301521502");
		map.put("SignName", "北京联创广汇");	
		map.put("TemplateCode", "SMS_165108546");
		map.put("TemplateParam", "{\"code\":\"827316\"}");
		jmsMessagingTemplate.convertAndSend("smsjiang",map);
	}

}
