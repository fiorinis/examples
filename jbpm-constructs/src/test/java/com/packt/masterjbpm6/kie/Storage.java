package com.packt.masterjbpm6.kie;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.persistence.map.EnvironmentBuilder;
import org.drools.persistence.map.KnowledgeSessionStorage;
import org.drools.persistence.map.KnowledgeSessionStorageEnvironmentBuilder;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

public class Storage {

	private SimpleKnowledgeSessionStorage storage;

	@Before
	public void initStorage() {
		storage = new SimpleKnowledgeSessionStorage();
	}

	@Test
	public void testStorageWithTimer() throws InterruptedException {
		KieSession session = getSession(null);
		int id = session.getId();

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
		Assert.assertTrue(facts >= 1);
		session.dispose();

		Thread.sleep(2000);

		session = getSession(id);
		// addRule(session, rule);
		session.fireAllRules();

		Assert.assertTrue(session.getFactHandles().size() > facts);
		session.dispose();
	}

	private KieSession getSession(Integer i) {
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
			ksessions.put(storedObject.getId(), storedObject);
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

		public Integer getNextStatefulKnowledgeSessionId() {
			return ksessions.size() + 1;
		}

		public void lock(SessionInfo sessionInfo) {

		}

		public void lock(WorkItemInfo workItemInfo) {
			// TODO Auto-generated method stub

		}
	}
}