package com.chasal.crawler.verify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.chasal.crawler.utils.HttpClientHelper;


public abstract class AbstractIVerify implements IVerify {

	protected Pattern PATTERN_PICTURE = null;
	protected String hopEncoding = null;// 输入验证码之后跳转得到的网页编码
	protected String verifyer = null;// 输入验证码之后跳转得到的网页编码
	
	private static final Logger logger = Logger.getLogger("verify");
	//private static int verifyTime = Integer.parseInt(ConfigHelper.getConfig().getVerify_code_time());
	private static int verifyTime = 10;
	public static PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
	public static DefaultHttpClient httpclient = new DefaultHttpClient(cm);

	public static CookieStore cookieStore = new BasicCookieStore();
	public static HttpContext localContext = new BasicHttpContext();
	public static String pictureRootPath=null;
	
	static {
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		File directory=new File("picture");
		if(!directory.isDirectory()){
			directory.mkdir();
		}
		pictureRootPath=directory.getAbsolutePath();
		
	}

	public String getVerifyCode(String url, String html) {

		String pictureUrl = getPictureUrl(html);
		if (pictureUrl == null) {
			logger.error("fail to get picture url!");
			return null;
		}
		String filePath= savePicture(pictureUrl);
		
		if (filePath == null) {
			return null;
		}
		
		String verifyCode = null;
		logger.info("send verify picture picture from " + url + " in file "+ filePath);
//		XMPPUtil.send(filePath);
//		
//		long old = System.currentTimeMillis();
//		while (true) {
//			try {
//				Thread.sleep(5 * 1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			Map<String, String> doneList = XMPPUtil.getDoneList();
//			if (doneList == null || doneList.size() == 0) {
//				continue;
//			}
//			verifyCode = doneList.get(filePath);
//			if (verifyCode != null) {
//				verifyCode = verifyCode.trim();
//				logger.info("successfully receives verifyCode="+verifyCode+", from " + url + " in file "+ filePath);
//				doneList.remove(filePath);
//				break;
//			}
//			// 60s 无结果，超时
//			if ((System.currentTimeMillis() - old) > verifyTime * 1000) {
//				logger.info("verifyCode running out of time! from " + url + " in file "+ filePath);
//				break;
//			}
		//}
		return verifyCode;
	}
	
	@Override
	public String handleVerifyCode(String url, String html) {

		String targetUrl = null;
		int count = 0;
		String hopUrl = null;
		String vUrl=url;
		String vHtml=html;
		while (count < 3) {
			count++;
			targetUrl = getTargetUrl(vUrl, vHtml);
			if (targetUrl == null) {
				continue;
			}
			hopUrl = submitVerifyCode(targetUrl,html);
			if (hopUrl != null && !isSuitable(hopUrl)) {
				logger.info("successfully verify！with url=" + vUrl);//验证成功直接退出循环
				return hopUrl;
			}
			if(hopUrl != null && isSuitable(hopUrl)){
			    //验证失败（验证码有误或超时），需要将vUrl，vHtml替换成失败之后再次返回的新验证页面，再进行下一次验证
				logger.info("verify failed！with url=" + vUrl+", retry times="+count);
				vUrl=hopUrl;
				vHtml=downloadHtml(vUrl, getHopHtmlEncoding());
			}
		}
		logger.info("give up verify url="+url+", after 2 times retry!");
		return null;
	}

	/**
	 * 提交验证码
	 * 
	 * @param curi
	 * @return 返回跳转之后的网页链接
	 */
	public String submitVerifyCode(String targetUrl,String html) {
		String hopUrl = null;
		hopUrl = HttpClientHelper.getRedirectUrl(targetUrl);
		return hopUrl;

	}

	/**
	 * 获取图片链接
	 * 
	 * @param html
	 * @return
	 */
	public String getPictureUrl(String html) {
		String pictureUrl = null;
		Matcher m = PATTERN_PICTURE.matcher(html);
		while (m.find()) {
			pictureUrl = m.group(1);
		}
		return pictureUrl;
	}

	@Override
	public String getHopHtmlEncoding() {
		// TODO Auto-generated method stub
		return hopEncoding;
	}

	/**
	 * 拼凑提交的链接
	 * 
	 * @param url
	 * @param html
	 * 验证码图片文件名：sogou-2af0df2e-8839-4dca-8c5c-57ca1aa47017-1358392328579.jpg
	 * 验证码图片文件命名规则：搜索引擎名称[sogou]+"-"+UUID[2af0df2e-8839-4dca-8c5c-57ca1aa47017]+"-"+系统时间[1358392328579]+".jpg"
	 * @return
	 */
	public abstract String getTargetUrl(String url, String html);
	
	protected  String savePicture(String pictureUrl) {
		String picturePath = null;
		HttpGet httpGet = new HttpGet(pictureUrl);
		httpGet.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet,localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		try {			
			    UUID uuid = UUID.randomUUID();
			    picturePath=pictureRootPath+"/"+verifyer+"-"+uuid+"-"+System.currentTimeMillis()+".jpg";
			    File storeFile = new File(picturePath);
		        FileOutputStream fileOutputStream = new FileOutputStream(storeFile);
		        FileOutputStream output = fileOutputStream;
		        InputStream in=entity.getContent();
		        int tmp=0;
		        while((tmp=in.read())!=-1){
		        	output.write(tmp);
		        }        
		        output.close();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			logger.error("save picture ParseException! with url="+pictureUrl+", error="+e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("save picture IOException! with url="+pictureUrl+", error="+e.toString());
		}
		httpGet.releaseConnection();
		if (picturePath == null) {
			logger.error("fail to save picture with url="+pictureUrl);
			return null;
		}
		if(!new File(picturePath).exists()){
			logger.error("picture does not exist! with path="+picturePath);
			return null;
		}
						
		return picturePath;
	}

	protected  String downloadHtml(String url, String encoding) {
		String html = null;
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
		HttpClientParams.setRedirecting(httpclient.getParams(), true);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet, localContext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		try {
			html = EntityUtils.toString(entity, encoding);
			EntityUtils.consume(entity);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpGet.releaseConnection();
		return html;
	}
	
}
