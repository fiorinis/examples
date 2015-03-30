package com.packt.masterjbpm6.kie;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

/**
 * 
 * @author simo
 * 
 *        NOTE: @see KieTest for settings
 * @see KieTest
 */
public class KieContainerTest extends KieTest {

	/**
	 * create a KieContainer from a Kie Module jar
	 */

	@Test
	public void testKieContainerFromArtifact() {
		ReleaseId releaseId = ks.newReleaseId("com.packt.masterjbpm6",
				"pizzadelivery", "1.0");

		KieContainer kieContainer = ks.newKieContainer(releaseId);
		assertTrue(kieContainer.verify().getMessages().size() == 0);
	}

	/**
	 * create the KieContainer from the classpath: get the Lane process from the
	 * kbase named "kbaseLane"
	 */
	@Test
	public void testKieContainerFromClasspath() {
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbaseLane");
		assertNotNull(kbase.getProcess("lane"));
		assertTrue(kContainer.verify().getMessages().size() == 0);
	}

	/**
	 * update a KieContainer to another Kie artifact GAV
	 */
	@Test
	public void testKieContainerUpdate() {
		ReleaseId releaseId = ks.newReleaseId("com.packt.masterjbpm6",
				"pizzadelivery", "1.0");
		KieContainer kieContainer = ks.newKieContainer(releaseId);

		ReleaseId newReleaseId = ks.newReleaseId("com.packt.masterjbpm6",
				"pizzaDeliveryNew", "1.0-SNAPSHOT");
		// update the container with the KIE module identified by its GAV
		Results result = kieContainer.updateToVersion(newReleaseId);
		assertFalse(result.hasMessages(Level.ERROR));
		assertFalse(kieContainer.verify().hasMessages(Level.ERROR));
	}
}
