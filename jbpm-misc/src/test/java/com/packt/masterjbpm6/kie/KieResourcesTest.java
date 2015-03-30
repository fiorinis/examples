package com.packt.masterjbpm6.kie;

import java.io.File;

import org.junit.Test;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.io.Resource;

/**
 * 
 * @author simo
 * 
 *         NOTE: @see KieTest for settings
 * @see KieTest
 */
public class KieResourcesTest extends KieTest {

	/**
	 * load and add kie module to kie repository
	 */
	@Test
	public void testResourceJAR() {
		KieRepository kr = ks.getRepository();
		Resource res = ks
				.getResources()
				.newFileSystemResource(
						new File(
								MAVEN_REPO_HOME
										+ "/com/packt/masterjbpm6/pizzadelivery/1.0/pizzadelivery-1.0.jar"));
		KieModule module = kr.addKieModule(res);
		assertTrue(module != null);
		assertNotNull(module.getReleaseId().toExternalForm());
		System.out.println(module.getReleaseId().toExternalForm());

	}

	/**
	 * load and add kie module to kie repository from file system kmodule.xml
	 */
	@Test
	public void testResource() {
		KieRepository kr = ks.getRepository();
		Resource res = ks.getResources()
				.newFileSystemResource(
						new File(GIT_EXAMPLES_HOME
								+ "/jbpm-constructs/target/classes/"));
		KieModule module = kr.addKieModule(res);
		assertNotNull(module);
	}
}
