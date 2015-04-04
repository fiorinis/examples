package com.packt.masterjbpm6.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(serviceName = "TestWebService")
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class TestWebService {

	private static final Logger logger = LoggerFactory
			.getLogger(TestWebService.class);

	@WebMethod(operationName = "ping")
	public String ping(@WebParam(name = "name") String name) {
		logger.info("ping {}", name);
		return "ping " + name;
	}

	@WebMethod(operationName = "addSmallOrder")
	public boolean addSmallOrder(@WebParam(name = "order") Order order) {
		logger.info(String.format("addSmallOrder:added order %s",
				order.toString()));
		return order.getCost() < 100;
	}

	@WebMethod(operationName = "addLargeOrder")
	public boolean addLargeOrder(@WebParam(name = "order") Order order) {
		logger.info(String.format("addLargeOrder: added order %s",
				order.toString()));
		return order.getCost() >= 100;
	}

}
