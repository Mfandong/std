package org.std.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService(name="TestWebService", //暴露服务名称
	targetNamespace = "http://TestWebService.webservice.std.org/" //命名空间，一般是接口的包的倒序
)
public interface TestWebService {
	
	@WebMethod
	@WebResult(name="String", targetNamespace = "")
	String sendMessage(@WebParam(name="username")String username);
}
