package com.packt.masterjbpm6.rest;

import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class RestTest extends PacktJUnitBaseTestCase {

	private final static String SERVER_ROOT = "http://localhost:9998";
	private final static String serverURL = SERVER_ROOT + RestResource.CONTEXT;
	private static Server server;

	public static final String processResource = "rest.bpmn2";
	public static final String processId = "rest";

	public RestTest() {
		super();
		setProcessResources(processResource);
	}

	@BeforeClass
	public static void initializeRestServer() throws Exception {
		JAXRSServerFactoryBean bean = new JAXRSServerFactoryBean();
		bean.setResourceClasses(RestResource.class);
		bean.setProvider(new JAXBElementProvider());
		bean.setAddress(SERVER_ROOT);
		server = bean.create();
		server.start();
	}

	@AfterClass
	public static void destroy() throws Exception {
		if (server != null) {
			server.stop();
			server.destroy();
		}
	}

	@Test
	public void testRestProcess() {
		ksession.getWorkItemManager().registerWorkItemHandler("Rest",
				new RESTWorkItemHandler());
		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess(processId);
		System.out.println(pi.getId());
	}

	@Test
	public void testPOSTOperation() {
		RESTWorkItemHandler handler = new RESTWorkItemHandler();
		String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<order><cost>0.0</cost><delivery><delivered>false</delivered><retries>0</retries></delivery><deliveryFee>0.0</deliveryFee><note>POST Order note was:my note</note><report/></order>";

		WorkItemImpl workItem = new WorkItemImpl();
		workItem.setParameter("Url", serverURL + "/xml");
		workItem.setParameter("Method", "POST");
		workItem.setParameter("ContentType", "application/xml");
		workItem.setParameter("Content", "<order><note>my note</note></order>");

		WorkItemManager manager = new EmptyWorkItemManager(workItem);
		handler.executeWorkItem(workItem, manager);

		String result = (String) workItem.getResult("Result");
		assertNotNull("result null", result);
		assertEquals(expected, result);
		int responseCode = (Integer) workItem.getResult("Status");
		assertNotNull(responseCode);
		assertEquals(200, responseCode);
	}

	private class EmptyWorkItemManager implements WorkItemManager {

		private WorkItem workItem;

		EmptyWorkItemManager(WorkItem workItem) {
			this.workItem = workItem;
		}

		public void completeWorkItem(long id, Map<String, Object> results) {
			((WorkItemImpl) workItem).setResults(results);
		}

		public void abortWorkItem(long id) {
		}

		public void registerWorkItemHandler(String workItemName,
				WorkItemHandler handler) {
		}

	}
}
