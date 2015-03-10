package com.packt.masterjbpm6.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.packt.masterjbpm6.pizza.model.Order;

@Path(RestResource.CONTEXT)
public class RestResource {

	public final static String CONTEXT = "/pizzarestservice";

	@GET
	@Produces("text/plain")
	public String get(@QueryParam("param") String param) {

		return "default GET " + (param != null ? " " + param : "");
	}

	@POST
	@Path("/order")
	@Consumes("application/xml")
	@Produces("application/xml")
	public Order postOrder(Order order) {
		order.setNote("POST Order note was:" + order.getNote());
		return order;
	}

}
