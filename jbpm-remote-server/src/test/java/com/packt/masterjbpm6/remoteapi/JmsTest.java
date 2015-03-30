package com.packt.masterjbpm6.remoteapi;

import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.remote.client.api.RemoteJmsRuntimeEngineBuilder;
import org.kie.remote.client.api.RemoteJmsRuntimeEngineFactory;
import org.kie.services.client.api.RemoteRuntimeEngineFactory;
import org.kie.services.client.api.command.RemoteRuntimeEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JmsTest extends Assert {

	public static String serverUrl = "127.0.0.1";
	public static String deploymentId = "com.packt.masterjbpm6:pizzadelivery:1.0";
	public static String user = "nino";
	public static String password = "nino";
	public static String jms_user = "admin";
	public static String jms_password = "admin";

	public static String processID = "com.packt.masteringjbpm6.pizzadelivery";

	private static final Logger logger = LoggerFactory.getLogger(JmsTest.class);

	RemoteRuntimeEngine engine;

	@Before
	public void initRemoteRuntimeEngineF() {
		InitialContext remoteInitialContext = getRemoteInitialContext(
				serverUrl, jms_user, jms_password);
		int maxTimeoutSecs = 10;

		String connectionFactoryString = System.getProperty(
				"connection.factory", "jms/RemoteConnectionFactory");
		ConnectionFactory connectionfactory = null;
		Queue responseQ = null;
		try {
			// "jms/queue/KIE.RESPONSE"
			// jms/queue/NapoliRQ
			responseQ = (Queue) remoteInitialContext
					.lookup("jms/queue/NapoliRQ");
		} catch (NamingException e2) {
			e2.printStackTrace();
		}
		try {
			connectionfactory = (ConnectionFactory) remoteInitialContext
					.lookup(connectionFactoryString);

			// setUpListener(connectionfactory, responseQ);
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		RemoteJmsRuntimeEngineBuilder jmsEngineBuilder = RemoteRuntimeEngineFactory
				.newJmsBuilder().addDeploymentId(deploymentId)
				.addRemoteInitialContext(remoteInitialContext)
				.addUserName(jms_user).addPassword(jms_password)
				.addConnectionFactory(connectionfactory)
				.addTimeout(maxTimeoutSecs);
		// use custom response Queue instead of the default
		// KIE.RESPONSE queue
		if (responseQ != null) {
			jmsEngineBuilder.addResponseQueue(responseQ);
		}

		RemoteJmsRuntimeEngineFactory engineFactory = jmsEngineBuilder
				.buildFactory();
		assertNotNull(engineFactory);
		engine = engineFactory.newRuntimeEngine();
		assertNotNull(engine);
	}

	@Test
	public void startProcess() {

		KieSession ksession = engine.getKieSession();
		for (int t = 0; t < 5; t++) {
			ProcessInstance pi = ksession.startProcess(processID);
			logger.info("created process instance. ID=" + pi.getId());
		}
		// need SSL
		// TaskService taskService = engine.getTaskService();
		// List<TaskSummary> tasks =
		// taskService.getTasksAssignedAsPotentialOwner(
		// user, "en-UK");
		// taskService.start(tasks.get(0).getId(), user);

	}

	private static InitialContext getRemoteInitialContext(
			String jbossServerHostName, String jmsuser, String jmspassword) {
		Properties initialProps = new Properties();
		initialProps.setProperty(InitialContext.INITIAL_CONTEXT_FACTORY,
				"org.jboss.naming.remote.client.InitialContextFactory");

		// WILDFLY only.
		initialProps.setProperty(InitialContext.PROVIDER_URL,
				"http-remoting://" + jbossServerHostName + ":8080");
		// jBOSS up to 7 uses remote: and 4447 port.
		// initialProps.setProperty(InitialContext.PROVIDER_URL,
		// "remote://" + jbossServerHostName + ":4447");

		initialProps.setProperty(InitialContext.SECURITY_PRINCIPAL, jmsuser);
		initialProps.setProperty(InitialContext.SECURITY_CREDENTIALS,
				jmspassword);
		for (Object keyObj : initialProps.keySet()) {
			String key = (String) keyObj;
			System.setProperty(key, (String) initialProps.get(key));
		}
		// Create the remote InitialContext instance
		try {
			return new InitialContext(initialProps);
		} catch (NamingException e) {
			throw new RuntimeException("Unable to create "
					+ InitialContext.class.getSimpleName(), e);
		}

	}

	private void setUpListener(ConnectionFactory connectionfactory,
			Queue responseQ) {
		if (responseQ == null || connectionfactory == null) {
			return;
		}
		Connection connection = null;
		Session session = null;
		try {
			connection = connectionfactory.createConnection(jms_user,
					jms_password);
			session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);

			MessageConsumer respConsumer = session.createConsumer(responseQ);
			respConsumer.setMessageListener(new MessageListener() {
				public void onMessage(Message arg0) {
					System.out.println(arg0);
					// connection.close();
					// session.close();
				}
			});

		} catch (JMSException e) {
			e.printStackTrace();
		} finally {
		}
	}
}
