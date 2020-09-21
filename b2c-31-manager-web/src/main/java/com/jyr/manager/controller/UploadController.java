package com.jyr.manager.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jyr.pojogroup.Goods;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

import entity.Result;
import util.FastDFSClient;
import util.IdWorker;

/**
 * 文件上传Controller
 * 
 * @author Administrator
 *
 */
@RestController
public class UploadController {

	@Value("${FILE_SERVER_URL}")
	private String FILE_SERVER_URL;// 文件服务器地址
	
	@Value("${FILE_LOCAL_CACHE_URL}")
	private String FILE_LOCAL_CACHE_URL;// 文件本地缓存地址

	@RequestMapping("/upload")
	public Result upload( MultipartFile file) throws  Exception{	
		
		String localPath = FILE_LOCAL_CACHE_URL+file.getOriginalFilename();
		File localFile = new File(localPath);
		if(!localFile.getParentFile().exists()) {
			localFile.mkdir();
		}
		file.transferTo(localFile);
		try {
			// 实例化一个Jersey
			Client client = new Client();
			// 保存图片服务器的请求路径
			IdWorker iw = new IdWorker();
			long nextId = iw.nextId();
			String remotePath = FILE_SERVER_URL + nextId;
			// 设置请求路径
			WebResource resource = client.resource(remotePath);
			// 读取图片到内存,将其变成二进制数组
			byte[] readFileToByteArray = FileUtils.readFileToByteArray(localFile);
			// 发送post get put
			resource.put(String.class, readFileToByteArray);
			return new Result(true, remotePath);
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
	}	
}
