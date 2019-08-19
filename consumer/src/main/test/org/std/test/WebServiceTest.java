package org.std.test;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.std.webservice.TestWebService;

public class WebServiceTest {

	public static void main(String[] args) {
//		//1、动态客户端
//		JaxWsDynamicClientFactory jwdcf = JaxWsDynamicClientFactory.newInstance();
//		Client client = jwdcf.createClient("http://localhost:8088/services/testWebService?wsdl");
//		Object[] obj = new Object[0];
//		try {
//			obj = client.invoke("sendMessage", "zs");
//			System.out.println(obj[0].toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//2、代理工厂
		String address = "http://localhost:8088/services/testWebService?wsdl";
		JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
		factoryBean.setAddress(address);
		factoryBean.setServiceClass(TestWebService.class);
		TestWebService webServiceTest = (TestWebService)factoryBean.create();
		String sendMessage = webServiceTest.sendMessage("李四");
		System.out.println(sendMessage);
	}
}
