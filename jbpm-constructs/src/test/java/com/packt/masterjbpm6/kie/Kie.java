package com.packt.masterjbpm6.kie;

import java.io.File;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import junit.framework.Assert;

import org.jbpm.process.audit.AbstractAuditLogger;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.logger.KieLoggers;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.scanner.MavenRepository;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

public class Kie extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] { "rule.bpmn", };
	public static final String[] rules = new String[] { "rule.drl", };

	public Kie() {
		super(PU_NAME);
		setProcessResources(processResources);
		setRules(rules);

	}

	@Test
	public void testAudit() {
		KieServices ks = KieServices.Factory.get();
		KieLoggers loggers = ks.getLoggers();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kbase.newKieSession();

		AbstractAuditLogger logger = AuditLoggerFactory.newJPAInstance();

	}

	@Test
	public void testLoggers() {
		KieServices ks = KieServices.Factory.get();
		KieLoggers loggers = ks.getLoggers();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieSession ksession = kbase.newKieSession();

		Map<String, Channel> channels = ksession.getChannels();
		KieRuntimeLogger logger = loggers.newFileLogger(ksession,
				"c:/temp/kielog.txt");
		ksession.signalEvent("signal", "signaldata");
		logger.close();

	}

	

	@Test
	public void testResourceJAR() {
		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		Resource res = ks
				.getResources()
				.newFileSystemResource(
						new File(
								"C:\\Users\\simo\\.m2\\repository\\com\\packt\\masterjbpm6\\pizzadelivery\\1.0\\pizzadelivery-1.0.jar"));
		kr.addKieModule(res);
		Assert.assertTrue(true);
	}

	@Test
	public void testResource() {
		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		Resource res = ks
				.getResources()
				.newFileSystemResource(
						new File(
								"C:\\Users\\simo\\git\\masterjbpm6\\jbpm-constructs\\target\\classes\\"));
		kr.addKieModule(res);
		Assert.assertTrue(true);
	}

	@Test
	public void testBuilderModule() {
		KieServices ks = KieServices.Factory.get();
		KieModuleModel kmodule = ks.newKieModuleModel();
		KieBaseModel kieBaseModel = kmodule.newKieBaseModel("KBase");
		kieBaseModel.setDefault(true)
				.setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
				.setEventProcessingMode(EventProcessingOption.STREAM);
		KieSessionModel ksession1 = kieBaseModel.newKieSessionModel("KSession")
				.setDefault(true)
				.setType(KieSessionModel.KieSessionType.STATEFUL)
				.setClockType(ClockTypeOption.get("realtime"));
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.writeKModuleXML(kmodule.toXML());
		KieBuilder kieBuilder = ks.newKieBuilder(kfs);
		kieBuilder.buildAll();

		KieModule module = kieBuilder.getKieModule();

		Assert.assertTrue(kieBuilder.buildAll().getResults().getMessages()
				.isEmpty());

		MavenRepository repository = MavenRepository.getMavenRepository();
		// repository.deployArtifact(releaseId, kJar1, kPom);

	}

	@Test
	public void testDefaultKieBase() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		KieResources res = ks.getResources();
		Assert.assertTrue(kContainer.verify().getMessages().size() == 0);
	}

	@Override
	public ObjectMarshallingStrategy[] haveStrategies(EntityManagerFactory emf) {
		// TODO Auto-generated method stub
		return null;
	}
}
