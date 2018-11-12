package com.war4.thirdParty.alipay.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *版本：3.3
 *日期：2012-08-10
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
	
 *提示：如何获取安全校验码和合作身份者ID
 *1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 *3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”

 *安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 *解决方法：
 *1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 *2、更换浏览器或电脑，重新登录查询。
 */

public class AlipayConfig {
	
	//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	// 合作身份者ID，以2088开头由16位纯数字组成的字符串
	public static String partner = "2088521158726685";
	
	// 交易安全检验码，由数字和字母组成的32位字符串
	// 如果签名方式设置为“MD5”时，请设置该参数
	public static String key = "nuomn7e8z4lws6ifjmuxrgem1rbiw5gb";
	
    // 商户的私钥
    // 如果签名方式设置为“0001”时，请设置该参数,目前还有问题，只能用md5
	public static String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAKq2gCxusX2LS6UW+creybl2kSdGyuohw8pADCdfIoc1w4j7UBvW/ZedzBZnaEfQ+XTf9tigRdpy+/zYfcVmmMBpSR335gUoRnT4yY5ll/bwCS3pVmEnmFP9s+7dA0eaf0rWt1hyLqRq/s2sJEKAGVpy+4wwYIXlDdJrxOIAnqHXAgMBAAECgYEAhijGsiSL+E3wNN3JkVVk0wwLJwLLlRTT+a473vU0/W2TLaPTYQ2kXfL04OaDXW0tnlwkcjoCq5qRzjOVI/1tLQApKxYWhCnHMK5U8lTWnwVr5deMfIKxCtKvMAPKIIyKMCUdn1VBbQzY8wCWkSYgzGH/VAmJEp1tQzWJsJRF1QECQQDgY4WfLnZslSjrK5GFd4LFhVVlJwE6sqs5iv95v659keZ9rQzEcPEyVcbHn1a0+2MpBHCx9H8WOjHfi81ogEefAkEAwsMu5XkoJA5fRX9JaYGeGM46UYOnbMo8CIEJ2w/iKnkjiT7DCR1cTlduoQg2fIiuB6kPhUaWj9dcnfkcFHfayQJBALRIYcBuRSmighjIjYfNaJ0oLl8yAnPVv2phOjfogNpU0fphgiOGltOZRTd31uNcIuR+s8jWpxEFurJgEa4HewMCQG07Ms4YiV7PgLdlkDLQM28OGr8k632kXs8WMATgf6P6lOegf5pZETAO8Uj3ZjeqIJDDTKUe1cqOqq8ey5TfM6ECQQCn10PdVw1emlbdBS3oCbZETUExjp2pCLAUGaWWI4haNz+HNXveChkMdn32ScewmDac3K2iXhnb/b/kKd4uYi12";

    // 支付宝的公钥
    // 如果签名方式设置为“0001”时，请设置该参数,目前还有问题，只能用md5
	public static String ali_public_key ="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
	//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
	

	// 调试用，创建TXT日志文件夹路径
	public static String log_path = "D:\\";

	// 字符编码格式 目前支持  utf-8
	public static String input_charset = "utf-8";
	
	// 签名方式，选择项：0001(RSA)、MD5
	public static String sign_type = "MD5";
	// 无线的产品中，签名方式为rsa时，sign_type需赋值为0001而不是RSA


	//skl
	//pid = 2088521158726685
	//默认加密： nuomn7e8z4lws6ifjmuxrgem1rbiw5gb
}
