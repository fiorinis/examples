package com.packt.masterjbpm6.kie;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.io.Resource;
import org.kie.scanner.MavenRepository;

/**
 * 
 * @author simo
 * 
 *         NOTE: @see KieTest for settings
 * @see KieTest
 */
public class KieBuilderTest extends KieTest {

	/**
	 * 
	 * create and deploy a Kie Module with a Kie module Dependency into both the
	 * system Maven repository and the Kie repository
	 * 
	 * NOTE: update variable "dependencyKieModule" to point a valid JAR module
	 * (if you went through Chapter 4 examples you should already have that
	 * installed in your Kie Maven repo).
	 */

	@Test
	public void testBuilderWithModels() {

		String dependencyKieModule = KieRepo
				+ "com/packt/masterjbpm6/pizzadelivery/1.0/pizzadelivery-1.0.jar";

		ReleaseId newModuleReleaseid = ks.newReleaseId("com.packt.masterjbpm6",
				"pizzaDeliveryNew", "1.0-SNAPSHOT");

		// check dependency exist
		// assertTrue(new File(KieRepo + dependencyKieModule).exists());

		// create the KIE module model
		KieModuleModel moduleModel = ks.newKieModuleModel();
		// create the KieBase model
		KieBaseModel kieBaseModel = moduleModel.newKieBaseModel("KBase");
		// create the KieSession model
		KieSessionModel ksession1 = kieBaseModel.newKieSessionModel("KSession")
				.setDefault(true);
		KieFileSystem kfs = ks.newKieFileSystem();
		// generate pom.xml file
		kfs.generateAndWritePomXML(newModuleReleaseid);
		// write the <kmodule> xml file
		kfs.writeKModuleXML(moduleModel.toXML());

		// When your fileset is ready, pass the KileFileSystem (content) to the
		// builder
		KieBuilder kieBuilder = ks.newKieBuilder(kfs);

		// add dependencies (here we put jar Files as Resources but you
		// can use one or more KieModule too)
		Resource dependencyRes = ks.getResources().newFileSystemResource(
				new File(dependencyKieModule));

		kieBuilder.setDependencies(dependencyRes);

		// We can now perform the “build”: the build compiles all module
		// knowledge packages and Java classes, validate the configuration files
		// (pom.xml, kmodule.xml) and finally installs the module in the local
		// KIE repository.

		kieBuilder.buildAll();
		if (kieBuilder.getResults().hasMessages(Level.ERROR)) {
			for (Message message : kieBuilder.getResults().getMessages()) {
				System.out.println(message);
			}
		}
		assertFalse(kieBuilder.getResults().hasMessages(Level.ERROR));

		KieModule kiemodule = kieBuilder.getKieModule();
		File pomfile = new File(System.getProperty("java.io.tmpdir")
				+ "/tmp_pom.xml");

		// deploy to Kie Maven repository
		ks.getRepository().addKieModule(kiemodule);

		assertNotNull(ks.getRepository().getKieModule(newModuleReleaseid));
		// deploy to Maven system repository
		try {
			FileWriter pomwriter = new FileWriter(pomfile);
			pomwriter.write(new String(kfs.read("pom.xml")).toCharArray());
			pomwriter.close();
			MavenRepository repository = MavenRepository.getMavenRepository();
			repository.deployArtifact(kiemodule.getReleaseId(),
					(InternalKieModule) kiemodule, pomfile);
			assertNotNull(repository.resolveArtifact(newModuleReleaseid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
