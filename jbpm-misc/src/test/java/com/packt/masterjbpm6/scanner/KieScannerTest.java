package com.packt.masterjbpm6.scanner;

import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.eclipse.aether.artifact.Artifact;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.MavenRepository;

import com.packt.masterjbpm6.test.PacktJUnitBaseTestCase;

/**
 * 
 * @author simo
 * 
 */
public class KieScannerTest extends PacktJUnitBaseTestCase {

	// must be in the classpath
	public static final String[] processResources = new String[] {};

	private static final String PROCESS_ID = "scannerprocess";

	private static final String PROCESS_DEF_OLD = "C:/Users/simo/git/masterjbpm6/jbpm-misc/src/test/resources/scannerProcess.bpmn2";
	private static final String PROCESS_DEF_NEW = "C:/Users/simo/git/masterjbpm6/jbpm-misc/src/test/resources/scannerProcess2.bpmn2";
	private static ReleaseId releaseId_snapshot = null;

	private static final String POM_XML_TMPFILE = "c:/temp/pom.xml";

	private static final String PROCESS_ID_2 = "scannerprocesstask";
	private static final String PROCESS_2_DEF_OLD = "C:/Users/simo/git/masterjbpm6/jbpm-misc/src/test/resources/scannerProcessTask.bpmn2";
	private static final String PROCESS_2_DEF_NEW = "C:/Users/simo/git/masterjbpm6/jbpm-misc/src/test/resources/scannerProcessTask2.bpmn2";
	private static ReleaseId releaseId_snapshot_2 = null;

	public KieScannerTest() {
		super(PU_NAME);
		setProcessResources(processResources);
		KieServices ks = KieServices.Factory.get();
		releaseId_snapshot = ks.newReleaseId("com.packt.masterjbpm6",
				"scannerProcess", "1.0-SNAPSHOT");
		releaseId_snapshot_2 = ks.newReleaseId("com.packt.masterjbpm6",
				"scannerProcessTask", "1.0-SNAPSHOT");
	}

	private void buildAndDeployModule(String procsrc, ReleaseId gav) {
		KieServices ks = KieServices.Factory.get();
		// create the KIE module model
		KieModuleModel kproj = ks.newKieModuleModel();
		// create the KieBase model
		KieBaseModel kieBaseModel = kproj.newKieBaseModel("defaultKieBase")
				.setDefault(true).addPackage("*");
		// create the KieSession model
		KieSessionModel ksession1 = kieBaseModel.newKieSessionModel("KSession")
				.setDefault(true);
		// create the pom.xml
		KieFileSystem kfs = ks.newKieFileSystem();
		// generate pom.xml file
		String pom = getPom(gav);
		kfs.writePomXML(pom);
		// write the <kmodule> xml file
		kfs.writeKModuleXML(kproj.toXML());
		// get handle to repository

		KieBuilder kieBuilder = ks.newKieBuilder(kfs);
		// add the process definition that shall be updated
		Resource scannerprocessold = ks.getResources().newFileSystemResource(
				procsrc);
		kfs.write("src/main/resources/process.bpmn2", scannerprocessold);
		List<Message> messages = kieBuilder.buildAll().getResults()
				.getMessages();
		assertTrue(messages.isEmpty());
		InternalKieModule kieModule = (InternalKieModule) kieBuilder
				.getKieModule();

		MavenRepository repository = getMavenRepository();

		try {
			String POM_FILEPATH = POM_XML_TMPFILE;
			FileWriter writer = new FileWriter(POM_FILEPATH);

			writer.write(pom);
			writer.flush();
			writer.close();
			File pomfile = new File(POM_FILEPATH);
			// deploy system maven repo
			repository.deployArtifact(gav, kieModule, pomfile);
			MavenRepository repo = MavenRepository.getMavenRepository();
			Artifact module = repo.resolveArtifact(gav);
			assertTrue(gav + " must be installed in Maven repo.",
					module != null);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		// deployJar(ks, module);
	}

	private static KieModule deployJar(KieServices ks, byte[] jar) {
		// Deploy jar into the repository
		Resource jarRes = ks.getResources().newByteArrayResource(jar);
		KieModule km = ks.getRepository().addKieModule(jarRes);
		return km;
	}

	private String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
		String pom = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
				+ "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n"
				+ "  <modelVersion>4.0.0</modelVersion>\n" + "\n"
				+ "  <groupId>" + releaseId.getGroupId() + "</groupId>\n"
				+ "  <artifactId>" + releaseId.getArtifactId()
				+ "</artifactId>\n" + "  <version>" + releaseId.getVersion()
				+ "</version>\n" + "\n";
		if (dependencies != null && dependencies.length > 0) {
			pom += "<dependencies>\n";
			for (ReleaseId dep : dependencies) {
				pom += "<dependency>\n";
				pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
				pom += "  <artifactId>" + dep.getArtifactId()
						+ "</artifactId>\n";
				pom += "  <version>" + dep.getVersion() + "</version>\n";
				pom += "</dependency>\n";
			}
			pom += "</dependencies>\n";
		}
		pom += "</project>";
		return pom;
	}

	@Test
	public void testScannerSameSessionAfterUpdate() {
		buildAndDeployModule(PROCESS_2_DEF_OLD, releaseId_snapshot_2);
		KieServices ks = KieServices.Factory.get();
		KieContainer kieContainer = ks.newKieContainer(releaseId_snapshot_2);

		KieSession session = kieContainer.newKieSession();
		session.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());

		ProcessInstance pi = session.startProcess(PROCESS_ID_2);

		buildAndDeployModule(PROCESS_2_DEF_NEW, releaseId_snapshot_2);

		KieScanner scanner = ks.newKieScanner(kieContainer);
		scanner.scanNow();

		// signal (old process definition)
		session.signalEvent("Signal_2", null);
		assertTrue(pi.getState() == ProcessInstance.STATE_COMPLETED);

		KieSession newsession = kieContainer.newKieSession();
		newsession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		ProcessInstance pi2 = newsession.startProcess(PROCESS_ID_2);
		waitUserInput();
		assertTrue(pi2.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testScannerUpdateNewSession() {
		buildAndDeployModule(PROCESS_DEF_OLD, releaseId_snapshot);
		KieServices ks = KieServices.Factory.get();
		KieContainer kieContainer = ks.newKieContainer(releaseId_snapshot);

		KieSession session = kieContainer.newKieSession();

		Map<String, Object> params = new HashMap<String, Object>();
		List<String> data = new ArrayList<String>();
		params.put("data", data);
		ProcessInstance pi = session.startProcess(PROCESS_ID, params);
		assertTrue(pi.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(1, data.size());
		assertEquals("data1", data.get(0));

		buildAndDeployModule(PROCESS_DEF_NEW, releaseId_snapshot);

		KieScanner scanner = ks.newKieScanner(kieContainer);
		scanner.scanNow();

		data.clear();
		params.clear();
		params.put("data", data);

		ProcessInstance pi2 = session.startProcess(PROCESS_ID, params);
		assertTrue(pi2.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(1, data.size());
		assertEquals("data2", data.get(0));
		data.clear();
	}

}
