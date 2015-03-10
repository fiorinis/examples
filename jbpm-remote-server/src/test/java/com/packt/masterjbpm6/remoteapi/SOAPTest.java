package com.packt.masterjbpm6.remoteapi;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.remote.services.ws.wsdl.generated.CommandWebServiceClient;
import org.kie.remote.services.ws.wsdl.generated.CommandWebServiceException;
import org.kie.services.client.api.command.exception.RemoteApiException;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsRequest;
import org.kie.services.client.serialization.jaxb.impl.JaxbCommandsResponse;

public class SOAPTest extends Assert {

	public static URL commandWsdlUrl;
	public static String deploymentId = "com.packt.masterjbpm6:pizzadelivery:1.0";
	public static String processID = "com.packt.masteringjbpm6.pizzadelivery";

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

	@Test
	public void startProcess() {

		CommandWebServiceClient client = new CommandWebServiceClient(
				commandWsdlUrl, new QName(
						"http://services.remote.kie.org/6.1.0.1/command",
						"CommandService"));

		try {
			Command<?> command = new StartProcessCommand(processID);
			JaxbCommandsRequest request = new JaxbCommandsRequest(deploymentId,
					command);

			JaxbCommandsResponse response = null;
			try {
				response = client.getCommandServicePort().execute(request);
			} catch (CommandWebServiceException e) {
				e.printStackTrace();
			}

			assertNotNull(response.getProcessInstanceId() > 0);

		} catch (RemoteApiException remoteException) {
			remoteException.printStackTrace();
		}

	}
}
