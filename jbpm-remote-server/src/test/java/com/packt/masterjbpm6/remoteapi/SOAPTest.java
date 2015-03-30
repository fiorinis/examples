package com.packt.masterjbpm6.remoteapi;

import java.net.URL;

import javax.xml.namespace.QName;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.remote.client.api.RemoteWebserviceClientBuilder;
import org.kie.remote.client.api.exception.RemoteApiException;
import org.kie.remote.client.jaxb.JaxbCommandsRequest;
import org.kie.remote.client.jaxb.JaxbCommandsResponse;
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
			commandWsdlUrl = SOAPTest.class.getClassLoader().getResource(
					"CommandService.wsdl");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Before
	public void initWsCommandClient() {
		RemoteWebserviceClientBuilder<RemoteWebserviceClientBuilder, CommandWebService> wsEngineBuilder = RemoteRuntimeEngineFactory
				.newCommandWebServiceClientBuilder()
				.addDeploymentId(deploymentId).addServerUrl(commandWsdlUrl);
		client = wsEngineBuilder.buildBasicAuthClient();
		assertNotNull(client);
	}

	@Test
	public void startProcess() {
		try {
			Command<?> command = new StartProcessCommand(processID);
			JaxbCommandsRequest request = new JaxbCommandsRequest(deploymentId,
					command);

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
