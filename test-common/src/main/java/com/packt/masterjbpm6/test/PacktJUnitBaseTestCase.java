package com.packt.masterjbpm6.test;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.drools.core.event.DefaultProcessEventListener;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.test.JBPMHelper;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Before;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jdbc.lrc.LrcXADataSource;

public class PacktJUnitBaseTestCase extends JbpmJUnitBaseTestCase {

	public static final String PU_NAME = "localjbpm-persistenceunit";
	public static final String DATASOURCE_NAME = "jdbc/jbpm-ds";

	// private static Properties properties;

	public static KieSession ksession = null;
	public static TaskService taskService = null;
	public static RuntimeEngine engine = null;
	private String[] processResources = null;
	private String[] rules = null;
	private String[] dsProperties = null;
	private UserGroupCallback usergroupcallback = null;
	protected RuntimeManager rtm;
	public static String DEFAULT_PROPERTY_FILE = "/localJBPM.properties";

	private static final Logger logger = LoggerFactory
			.getLogger(PacktJUnitBaseTestCase.class);

	public UserGroupCallback getUsergroupcallback() {
		return usergroupcallback;
	}

	public void setUsergroupcallback(UserGroupCallback usergroupcallback) {
		this.usergroupcallback = usergroupcallback;
	}

	private ProcessEventListener defaultEventListener = new DefaultProcessEventListener() {
		@Override
		public void afterNodeLeft(ProcessNodeLeftEvent event) {
			logger.info("After node left {}", event.getNodeInstance()
					.getNodeName());
		}

		@Override
		public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
			logger.info("After node triggered {}", event.getNodeInstance()
					.getNodeName());
		}

		@Override
		public void beforeNodeLeft(ProcessNodeLeftEvent event) {
			logger.info("Before node left {}", event.getNodeInstance()
					.getNodeName());
		}

		@Override
		public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
			logger.info("Before node triggered {}", event.getNodeInstance()
					.getNodeName());
		}
	};
	private ObjectMarshallingStrategy[] strategies;

	private static Properties getProperties(String filename) {
		Properties properties = new Properties();
		try {
			properties.load(JBPMHelper.class.getResourceAsStream(filename));
		} catch (Throwable t) {
			// do nothing, use defaults
		}
		return properties;

	}

	@Override
	public PoolingDataSource setupPoolingDataSource() {
		PoolingDataSource pds = null;
		if (dsProperties == null || dsProperties.length == 0) {
			dsProperties = new String[] { DEFAULT_PROPERTY_FILE };
		}
		for (String propname : dsProperties) {
			Properties props = getProperties(propname);

			pds = new PoolingDataSource();
			pds.setUniqueName(props.getProperty("persistence.datasource.name",
					DATASOURCE_NAME));
			pds.setClassName(LrcXADataSource.class.getName());
			pds.setMaxPoolSize(5);
			pds.setAllowLocalTransactions(true);
			pds.getDriverProperties().put("user",
					props.getProperty("persistence.datasource.user", "sa"));
			pds.getDriverProperties().put("password",
					props.getProperty("persistence.datasource.password", ""));
			pds.getDriverProperties()
					.put("url",
							props.getProperty("persistence.datasource.url",
									"jdbc:h2:tcp://localhost/~/jbpm-62;MVCC=TRUE"));
			pds.getDriverProperties().put(
					"driverClassName",
					props.getProperty("persistence.datasource.driverClassName",
							"org.h2.Driver"));
			pds.init();
		}
		return pds;
	}

	@Before
	public void initWf() {
		try {
			rtm = super.createRuntimeManager(processResources);
			engine = super.getRuntimeEngine();
			ksession = engine.getKieSession();
			taskService = engine.getTaskService();
			ksession.addEventListener(defaultEventListener);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setEventListener() {

	}

	public EntityManager haveAppScopedEM() {
		return null;
	}

	public ObjectMarshallingStrategy[] haveStrategies(EntityManagerFactory emf) {
		return null;
	}

	// override to replace the default usergroupcallback
	@Override
	protected RuntimeManager createRuntimeManager(Strategy strategy,
			Map<String, ResourceType> resources, String identifier) {
		if (manager != null) {
			throw new IllegalStateException(
					"There is already one RuntimeManager active");
		}
		// add drools rules
		if (rules != null) {
			for (String rule : rules) {
				resources.put(rule, ResourceType.DRL);
			}
		}

		RuntimeEnvironmentBuilder builder = null;
		if (!setupDataSource) {
			builder = RuntimeEnvironmentBuilder.Factory
					.get()
					.newEmptyBuilder()
					.addConfiguration("drools.processSignalManagerFactory",
							DefaultSignalManagerFactory.class.getName())
					.addConfiguration(
							"drools.processInstanceManagerFactory",
							DefaultProcessInstanceManagerFactory.class
									.getName());
		} else if (sessionPersistence) {
			builder = RuntimeEnvironmentBuilder.Factory
					.get()
					.newDefaultBuilder()
					.entityManagerFactory(super.getEmf())
					.addConfiguration("drools.workItemHandlers",
							"MyWorkItemHandlers.conf");

		} else {
			builder = RuntimeEnvironmentBuilder.Factory.get()
					.newDefaultInMemoryBuilder();
		}
		if (usergroupcallback == null) {
			usergroupcallback = new MyUserCallback();
		}

		builder.userGroupCallback(usergroupcallback);
		builder.addEnvironmentEntry(
				EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
				haveStrategies(super.getEmf()));

		if (null != haveAppScopedEM()) {
			builder.addEnvironmentEntry(
					EnvironmentName.APP_SCOPED_ENTITY_MANAGER,
					haveAppScopedEM());
		}
		for (Map.Entry<String, ResourceType> entry : resources.entrySet()) {
			builder.addAsset(
					ResourceFactory.newClassPathResource(entry.getKey()),
					entry.getValue());
		}

		return createRuntimeManager(strategy, resources, builder.get(),
				identifier);
	}

	public PacktJUnitBaseTestCase() {
		this(true);

	}

	public PacktJUnitBaseTestCase(boolean persistence) {
		super(persistence, persistence);

	}

	public PacktJUnitBaseTestCase(String puName) {
		super(true, true, puName);
	}

	public void setProcessResources(String... processResource) {
		this.processResources = processResource;
	}

	protected Object getProcessVarFromInstance(ProcessInstance process,
			String processVarName) {
		WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
		Object var = instance.getVariable(processVarName);
		return var;

	}

	protected void sendSignal(String signalid, Object eventdata) {
		ksession.signalEvent(signalid, eventdata);
	}

	protected void sendSignal(long processid, String signalid, Object eventdata) {
		ksession.signalEvent(signalid, eventdata, processid);
	}

	protected boolean performFirstTaskOnList(String actorid) {
		return performFirstTaskOnList(actorid, null);
	}

	protected int getNumTasksAdminOnList() {
		List<TaskSummary> list = taskService
				.getTasksAssignedAsBusinessAdministrator("Administrator",
						"en-UK");
		return (list != null ? list.size() : 0);
	}

	protected int getNumTasksOnList(String actorid) {
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				actorid, "en-UK");
		return (list != null ? list.size() : 0);
	}

	protected TaskSummary getFirstTaskOnList(String actorid) {
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				actorid, "en-UK");
		if (list.size() > 0) {
			TaskSummary task = list.get(0);
			return task;
		}
		return null;
	}

	protected boolean performFirstTaskOnList(String actorid,
			Map<String, Object> params) {
		TaskSummary task = getFirstTaskOnList(actorid);
		boolean result = false;
		if (task != null) {
			try {
				System.out.println(actorid + " is executing task "
						+ task.getName());
				taskService.start(task.getId(), actorid);
				taskService.complete(task.getId(), actorid, params);
				result = true;
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		return result;
	}

	public void assertTaskStatus(long taskid, String user, Status expectedstatus) {
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				user, "en-UK");
		boolean found = false;
		for (TaskSummary ts : list) {
			if (ts.getId() == taskid) {
				int result = ts.getStatus().compareTo(expectedstatus);
				found = true;
				assertEquals(0, result);
				break;
			}
		}
		if (!found) {
			fail();
		}
	}

	protected TaskSummary getFirstTaskByNameOnList(String taskname,
			String actorid) {

		TaskSummary task = null;
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				actorid, "en-UK");
		for (TaskSummary ts : list) {
			if (ts.getName().equals(taskname)) {

				task = ts;
				break;
			}
		}
		return task;

	}

	protected boolean performByTaskNameOnList(String taskname, String actorid) {
		TaskSummary task = getFirstTaskByNameOnList(taskname, actorid);
		boolean result = false;

		if (task != null) {
			try {
				System.out.println(actorid + " is executing task "
						+ task.getName());
				taskService.start(task.getId(), actorid);
				taskService.complete(task.getId(), actorid, null);
				result = true;
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		return result;
	}

	public void waitUserInput(String msg) {
		Utils.waitUserInput(msg);
	}

	public void waitUserInput() {
		Utils.waitUserInput("please type anything to end the test");
	}

	public void assertProcessVarValue(ProcessInstance processInstance,
			String varName, Object varValue) {
		String actualValue = getProcessVarValue(processInstance, varName);
		assertEquals("Variable " + varName + " value mismatch!", varValue,
				actualValue);
	}

	public String getProcessVarValue(ProcessInstance processInstance,
			String varName) {
		String actualValue = null;
		if (sessionPersistence) {
			List<? extends VariableInstanceLog> log = getLogService()
					.findVariableInstances(processInstance.getId(), varName);
			if (log != null && !log.isEmpty()) {
				actualValue = log.get(log.size() - 1).getValue();
			}
		} else {
			Object value = ((WorkflowProcessInstanceImpl) processInstance)
					.getVariable(varName);
			if (value != null) {
				actualValue = value.toString();
			}
		}
		return actualValue;
	}

	public String[] getRules() {
		return rules;
	}

	public void setRules(String[] rules) {
		this.rules = rules;
	}

	public String[] getDsProperties() {
		return dsProperties;
	}

	public void setDsProperties(String[] dsProperties) {
		this.dsProperties = dsProperties;
	}

}
