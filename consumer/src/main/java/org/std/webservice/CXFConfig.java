package org.std.webservice;

import javax.xml.ws.Endpoint;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CXFConfig {
	
	@Autowired
	private Bus bus;
	
	@Autowired
	private TestWebService testWebService;
	
	@Bean
	public Endpoint endpoint() {
		EndpointImpl endpointImpl = new EndpointImpl(bus, testWebService);
		endpointImpl.publish("/testWebService");
		return endpointImpl;
	}
}
