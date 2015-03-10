package com.packt.masterjbpm6.pizzadelivery;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.test.JBPMHelper;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import bitronix.tm.resource.jdbc.lrc.LrcXADataSource;

public class ProcessBAM {

	public static final String PU_NAME = "localjbpm-persistenceunit";
	public static final String DATASOURCE_NAME = "jdbc/localjbpm-ds";
	private static final String PROPERTIES_FILE = "/localJBPM.properties";

	private static Properties properties;

	public static KieSession ksession = null;
	public static TaskService taskService = null;
	public static RuntimeManager manager = null;
	public static RuntimeEngine engine = null;

	// nino: order handler
	// maria: store manager
	// mario & luigi: pizzamakers
	// salvatore: pizzaboy

	public enum humans {
		nino, maria, mario, luigi, salvatore
	};

	static Executor humansPool = Executors
			.newFixedThreadPool(humans.values().length);

	static final int PROCESSNUM = 10;
	static final int MAX_TASK_PER_HUMAN = PROCESSNUM;
	static int pizzamade = 0;

	public static void startHuman(final String actorId,
			final CountDownLatch startSignal, final CountDownLatch doneSignal) {
		Runnable r = new Runnable() {
			public void run() {
				int taskcount = 0;
				final String username = actorId;
				try {
					startSignal.await();
					while (true) {
						int waitfor = 1000 * (new Random().nextInt(1) + 1);
						System.out
								.println(String
										.format("%s is waiting for %d before checking for work",
												username, waitfor));
						Thread.sleep(waitfor);
						boolean taskdone = getFirstTaskAndComplete(actorId);
						boolean workDone = false;
						if (isAPizzaboy(username)
								|| isAPizzaOrderHandler(username)) {
							taskcount += taskdone ? 1 : 0;
							workDone = taskcount == MAX_TASK_PER_HUMAN;
						} else if (isAPizzaMaker(username)) {
							pizzamade += taskdone ? 1 : 0;
							workDone = pizzamade == MAX_TASK_PER_HUMAN;
							System.out.println("pizza made=" + pizzamade);
						} else if (isAManager(username)) {
							taskcount += taskdone ? 1 : 0;
							workDone = taskcount == 2 * MAX_TASK_PER_HUMAN;
						}

						if (workDone) {
							System.out.println(String.format(
									"%s is done with work for now.", username));
							break;
						}
					}
					doneSignal.countDown();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		humansPool.execute(r);

	}

	public static boolean isAPizzaboy(String actorId) {
		return actorId.equals(humans.salvatore.toString());
	}

	public static boolean isAPizzaOrderHandler(String actorId) {
		return actorId.equals(humans.nino.toString());
	}

	public static boolean isAPizzaMaker(String actorId) {
		return actorId.equals(humans.mario.toString())
				|| actorId.equals(humans.luigi.toString());
	}

	public static boolean isAManager(String actorId) {
		return actorId.equals(humans.maria.toString());
	}

	public static void main(String args[]) {
		getProperties();
		initWf();

		CountDownLatch startHumanWorkerSignal = new CountDownLatch(1);
		CountDownLatch doneHumanWorkerSignal = new CountDownLatch(
				humans.values().length);

		// start human workers
		for (Object human : humans.values()) {
			startHuman(human.toString(), startHumanWorkerSignal,
					doneHumanWorkerSignal);
		}
		// let'em go
		startHumanWorkerSignal.countDown();

		// create incoming pizza orders
		long processid = 0;
		for (int t = 0; t < PROCESSNUM; t++) {
			processid = startProcess();
		}
		// wait
		try {
			doneHumanWorkerSignal.await();
			System.out.println("...all tasks completed.");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeWF();
		System.exit(0);

	}

	private static boolean getFirstTaskAndComplete(String username) {
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				username, "en-UK");
		TaskSummary task = null;
		boolean taskdone = false;
		if (list.size() > 0) {
			task = list.get(0);
			System.out.println(String.format("%s is executing task '%s' ",
					username, task.getName()));
			try {
				taskService.start(task.getId(), username);
				boolean isLuigi = username.equals(humans.luigi.toString());
				// make luigi slower in making pizzas
				int waitfor = 1000 * (new Random().nextInt(3) + (isLuigi ? 6
						: 1));
				System.out.println(String.format(
						"%s is waiting for %d before completing task '%s' ",
						username, waitfor, task.getName()));
				Thread.sleep(waitfor);
				taskService.complete(task.getId(), username, null);
				taskdone = true;

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (PermissionDeniedException pexc) {
				System.out.println(String.format(
						"whops! %s tried to start an already started '%s'",
						username, task.getName()));
			}

		}
		return taskdone;
	}

	private static RuntimeManager createRuntimeManager(KieBase kbase) {
		setupDataSource();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory(properties.getProperty(
						"persistence.persistenceunit.name", PU_NAME));

		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory
				.get().newDefaultBuilder().persistence(true)
				.entityManagerFactory(emf).knowledgeBase(kbase)
				.userGroupCallback(new MyUserCallback());

		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(
				builder.get(), "com.packt.masteringjbpm6.pizzadelivery");
	}

	private static Properties getProperties() {
		properties = new Properties();
		try {
			properties.load(JBPMHelper.class
					.getResourceAsStream(PROPERTIES_FILE));
		} catch (Throwable t) {
			// do nothing, use defaults
		}
		return properties;

	}

	public static long startProcess() {

		org.kie.api.runtime.process.ProcessInstance pi = ksession
				.startProcess("com.packt.masteringjbpm6.pizzadelivery");

		System.out.println(String.format("started process %s with ID=%s",
				pi.getProcessId(), pi.getId()));
		return pi.getId();
	}

	public static PoolingDataSource setupDataSource() {

		PoolingDataSource pds = new PoolingDataSource();
		pds.setUniqueName(properties.getProperty("persistence.datasource.name",
				DATASOURCE_NAME));
		pds.setClassName(LrcXADataSource.class.getName());
		pds.setMaxPoolSize(5);
		pds.setAllowLocalTransactions(true);
		pds.getDriverProperties().put("user",
				properties.getProperty("persistence.datasource.user", "sa"));
		pds.getDriverProperties().put("password",
				properties.getProperty("persistence.datasource.password", ""));
		pds.getDriverProperties().put(
				"url",
				properties.getProperty("persistence.datasource.url",
						"jdbc:h2:tcp://localhost/~/jbpm-db;MVCC=TRUE"));
		pds.getDriverProperties().put(
				"driverClassName",
				properties.getProperty(
						"persistence.datasource.driverClassName",
						"org.h2.Driver"));
		pds.init();
		return pds;
	}

	public static void initWf() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase();

		manager = createRuntimeManager(kbase);
		engine = manager.getRuntimeEngine(null);
		ksession = engine.getKieSession();
		taskService = engine.getTaskService();
	}

	public static void closeWF() {
		manager.disposeRuntimeEngine(engine);
	}

}