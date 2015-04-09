package com.packt.masterjbpm6.remoteapi;

import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.remote.client.api.RemoteWebserviceClientBuilder;
import org.kie.remote.client.api.exception.RemoteApiException;
import org.kie.remote.client.jaxb.JaxbCommandsRequest;
import org.kie.remote.client.jaxb.JaxbCommandsResponse;
import org.kie.remote.jaxb.gen.GetTaskAssignedAsPotentialOwnerCommand;
import org.kie.remote.services.ws.command.generated.CommandWebService;
import org.kie.remote.services.ws.command.generated.CommandWebServiceException;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;

public class SOAPTest extends Assert {

	public static URL commandWsdlUrl;
	public static String deploymentId = "com.packt.masterjbpm6:pizzadelivery:1.0";
	public static String processID = "com.packt.masteringjbpm6.pizzadelivery";

	CommandWebService client;

	@BeforeClass
	public static void setup() {
		try {

			commandWsdlUrl = new URL(
					"http://localhost:8080/jbpm-console/CommandService?WSDL");
			// SOAPTest.class.getClassLoader().getResource(
			// "CommandService.wsdl");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Before
	public void initWsCommandClient() {
		RemoteWebserviceClientBuilder<RemoteWebserviceClientBuilder, CommandWebService> wsEngineBuilder = RemoteRuntimeEngineFactory
				.newCommandWebServiceClientBuilder()
				.addDeploymentId(deploymentId).addServerUrl(commandWsdlUrl)
				.addUserName("admin").addPassword("admin");
		client = wsEngineBuilder.buildBasicAuthClient();
		assertNotNull(client);
	}

	@Test
	public void startProcess() {
		try {
			GetTaskAssignedAsPotentialOwnerCommand gettask = new GetTaskAssignedAsPotentialOwnerCommand();
			gettask.setUserId("admin");

			JaxbCommandsRequest request = new JaxbCommandsRequest(deploymentId,
			// startProcessCommand
					gettask);
			JaxbCommandsResponse response = null;
			try {
				response = client.execute(request);
			} catch (CommandWebServiceException e) {
				e.printStackTrace();
			}

			assertNotNull(response.getProcessInstanceId() > 0);

		} catch (RemoteApiException remoteException) {
			remoteException.printStackTrace();
		}

	}
}
