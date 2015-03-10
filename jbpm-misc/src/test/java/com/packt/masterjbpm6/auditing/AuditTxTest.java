package com.packt.masterjbpm6.auditing;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.junit.Test;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class AuditTxTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "auditTxProcess.bpmn2" };

	private static final String PROCESS_ID = "auditTxProcess";

	EntityManager em;
	EntityManagerFactory emf;

	public AuditTxTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		setDsProperties(new String[] { "/localAPP.properties",
				DEFAULT_PROPERTY_FILE });

	}

	@Override
	public EntityManager haveAppScopedEM() {
		return em;
	}

	@Test
	public void testAuditTxCommit() {

		AuditEntity audit = new AuditEntity();
		audit.setDesc("startAudit1");

		UserTransaction ut;
		try {
			ut = (UserTransaction) new InitialContext()
					.lookup("java:comp/UserTransaction");
			ut.begin();
			em.joinTransaction();
			em.persist(audit);
			// start process
			ProcessInstance pi = ksession.startProcess(PROCESS_ID);
			AuditEntity auditproc = new AuditEntity();
			auditproc.setDesc("Audit1:process started");
			em.persist(auditproc);
			ut.commit();

			ProcessInstanceLog log = super.getLogService().findProcessInstance(
					pi.getId());
			assertNotNull(log);

		} catch (Exception exc) {

			em.getTransaction().rollback();
		}

	}

	@Test
	public void testAuditTxRollback() {

		AuditEntity audit = new AuditEntity();
		audit.setDesc("startAudit2");

		em.getTransaction().begin();
		em.persist(audit);
		em.getTransaction().commit();

		em.getTransaction().begin();

		// start process
		ProcessInstance pi = ksession.startProcess(PROCESS_ID);
		AuditEntity auditproc = new AuditEntity();
		auditproc.setDesc("Audit2: process started");
		em.persist(auditproc);
		try {
			((String) null).equalsIgnoreCase("null");
			em.getTransaction().commit();
		} catch (Exception exc) {

			em.getTransaction().rollback();
		}
		ProcessInstanceLog log = super.getLogService().findProcessInstance(
				pi.getId());
		assertNull(log);
	}
}
