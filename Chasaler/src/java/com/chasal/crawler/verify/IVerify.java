package com.chasal.crawler.verify;

public interface IVerify {
	
	/**
	 * 处理验证码的总的函数入口
	 * @param url 需要验证的网页链接
	 * @param html 需要验证的网页源文件
	 * @return 返回提交验证跳转之后的链接
	 */
	public abstract String handleVerifyCode(String url, String html);

	/**
	 * 获取输入验证码之后跳转网页的源文件编码
	 * @return
	 */
	public abstract String getHopHtmlEncoding();
	
	/**
	 * 判断该链接是否需要处理
	 * 
	 * @param url
	 * @return
	 */
	public abstract boolean isSuitable(String url);
}