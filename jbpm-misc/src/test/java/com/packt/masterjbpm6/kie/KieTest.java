package com.packt.masterjbpm6.kie;

import java.io.IOException;
import java.io.InputStream;

import org.drools.core.io.impl.UrlResource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author simo
 * 
 *         NOTE: update constants according to your system settings
 */

public class KieTest extends Assert {
	public static String MAVEN_REPO_HOME = "c:/Users/simo/.m2/repository";
	public static String GIT_EXAMPLES_HOME = "c:/Users/simo/git/masteringjbpm6/examples";

	public final static String JBPM_HOME = "c:/dev/packt/jbpm-installer-62";
	public final static String KieRepo = JBPM_HOME + "/repositories/kie/";

	KieServices ks;

	@BeforeClass
	public void init() {
		ks = KieServices.Factory.get();
		logger.info("KieServices init");
	}

	@Test
	public void testEvaluation() {
		String url = "http://localhost:8080/jbpm-console/maven2/org.jbpm/Evaluation/1.1/Evaluation-1.1.jar";

		KieServices ks = KieServices.Factory.get();
		KieRepository kr = ks.getRepository();
		UrlResource urlResource = (UrlResource) ks.getResources()
				.newUrlResource(url);
		urlResource.setUsername("admin");
		urlResource.setPassword("admin");
		urlResource.setBasicAuthentication("enabled");
		InputStream is;
		try {
			is = urlResource.getInputStream();
			KieModule kModule = kr.addKieModule(ks.getResources()
					.newInputStreamResource(is));

			KieContainer kContainer = ks
					.newKieContainer(kModule.getReleaseId());

			kContainer.newStatelessKieSession();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static final Logger logger = LoggerFactory
			.getLogger(KieTest.class);

}
