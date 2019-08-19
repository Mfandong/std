package org.std.webservice;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

@WebService(serviceName = "TestWebService", //与接口中指定的name保持一致
	targetNamespace = "http://TestWebService.webservice.std.org/", //与接口中的命名空间保持一致
	endpointInterface = "org.std.webservice.TestWebService"
)
@Component
public class TestWebServiceImpl implements TestWebService{

	@Override
	public String sendMessage(String username) {
		return "hello " + username;
	}

}
