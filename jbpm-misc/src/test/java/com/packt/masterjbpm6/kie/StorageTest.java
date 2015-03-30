package com.packt.masterjbpm6.kie;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.SessionConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.persistence.map.EnvironmentBuilder;
import org.drools.persistence.map.KnowledgeSessionStorage;
import org.drools.persistence.map.KnowledgeSessionStorageEnvironmentBuilder;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;

import bitronix.tm.TransactionManagerServices;

import com.packt.masterjbpm6.pizza.model.Order;
import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

/**
 * 
 * @author simo
 * 
 */
public class StorageTest extends PacktJUnitBaseTestCase {

	// resources must be fully qualified since they're processed from the
	// ResourceFactory.newClassPathResource and not the KBase
	public static final String[] processResources = new String[] { "rule/rule.bpmn", };
	public static final String[] rules = new String[] { "rule/rule.drl", };

	private static SimpleKnowledgeSessionStorage storage = new SimpleKnowledgeSessionStorage();
	private static KieServices ks = KieServices.Factory.get();

	public StorageTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);
	}

	/**
	 * create use and save the KieSession. after disposing it, reload the
	 * session from the store and execute the test again.
	 */
	@Test
	public void testRuleWithStorageServer() {

		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kbase.newKieSession();

		long id = ksession.getIdentifier();
		KieStoreServices storeservice = ks.getStoreServices();
		Environment env = EnvironmentFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, super.getEmf());
		env.set(EnvironmentName.TRANSACTION_MANAGER,
				TransactionManagerServices.getTransactionManager());
		ksession = storeservice.newKieSession(kbase, null, env);
		testRule(ksession);
		id = ksession.getIdentifier();
		ksession.dispose();
		KieSession loadedsession = storeservice.loadKieSession(id, kbase, null,
				env);
		assertEquals(id, loadedsession.getIdentifier());
		ksession = loadedsession;
		testRule(ksession);
	}

	@Test
	public void testStorageWithTimer() throws InterruptedException {
		KieSession session = getSession(null);
		long id = session.getIdentifier();

		String rule = "package test\n";
		rule += "import erdf.poc.cep.*;\n";
		rule += "rule \"test\"\n";
		rule += "timer (cron:0/2 * * * * ?)\n";
		rule += "when\n";
		rule += "then\n";
		rule += "insert(\"test\"+System.currentTimeMillis());\n";
		rule += "end";
		// addRule(session, rule);

		session.fireAllRules();
		Thread.sleep(2000);
		session.fireAllRules();

		final int facts = session.getFactHandles().size();
		assertTrue(facts >= 1);
		session.dispose();

		Thread.sleep(2000);

		session = getSession(id);
		// addRule(session, rule);
		session.fireAllRules();

		assertTrue(session.getFactHandles().size() > facts);
		session.dispose();
	}

	private KieSession getSession(Long i) {
		final Environment env = KnowledgeBaseFactory.newEnvironment();
		EnvironmentBuilder envBuilder = new KnowledgeSessionStorageEnvironmentBuilder(
				storage);
		env.set(EnvironmentName.TRANSACTION_MANAGER,
				envBuilder.getTransactionManager());
		env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
				envBuilder.getPersistenceContextManager());
		KieStoreServices storeS = KieServices.Factory.get().getStoreServices();
		final KieBase kieBase = KnowledgeBaseFactory.newKnowledgeBase();
		if (i != null) {
			return storeS.loadKieSession(i, kieBase, null, env);
		}
		return storeS.newKieSession(kieBase, null, env);
	}

	private static class SimpleKnowledgeSessionStorage implements
			KnowledgeSessionStorage {

		public Map<Integer, SessionInfo> ksessions = new HashMap<Integer, SessionInfo>();
		public Map<Long, WorkItemInfo> workItems = new HashMap<Long, WorkItemInfo>();

		public SessionInfo findSessionInfo(Integer id) {
			return ksessions.get(id);
		}

		public void saveOrUpdate(SessionInfo storedObject) {
			ksessions.put(storedObject.getId().intValue(), storedObject);
		}

		public void saveOrUpdate(WorkItemInfo workItemInfo) {
			workItems.put(workItemInfo.getId(), workItemInfo);
		}

		public Long getNextWorkItemId() {
			return new Long(workItems.size() + 1);
		}

		public WorkItemInfo findWorkItemInfo(Long id) {
			return workItems.get(id);
		}

		public void remove(WorkItemInfo workItemInfo) {
			workItems.remove(workItemInfo.getId());
		}

		public Long getNextStatefulKnowledgeSessionId() {
			return new Long(ksessions.size() + 1);
		}

		public void lock(SessionInfo sessionInfo) {

		}

		public void lock(WorkItemInfo workItemInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public SessionInfo findSessionInfo(Long arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public void testRule(KieSession ksession) {
		StringBuffer orderdesc = new StringBuffer();
		ksession.setGlobal("newnote", orderdesc);
		Order order = new Order();
		order.setCost(200);
		order.setNote("");
		ksession.setGlobal("orderglobal", order);
		FactHandle handle = ksession.insert(order);
		ProcessInstance processInstance = ksession.startProcess("rule");
		ksession.fireAllRules();

		Order ordermodified = (Order) ksession.getGlobal("orderglobal");
		assertEquals("URGENT", ordermodified.getNote());
	}

}