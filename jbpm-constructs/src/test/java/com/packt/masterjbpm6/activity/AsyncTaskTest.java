package com.packt.masterjbpm6.activity;

import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class AsyncTaskTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "asynctaskprocess.bpmn" };

	public AsyncTaskTest() {
		super(PU_NAME);
		setProcessResources(processResources);
	}

	@Test
	public void testAsyncTask() {
		ProcessInstance processInstance = ksession
				.startProcess("asynctaskprocess");
		waitUserInput("type something to end ");
	}

}