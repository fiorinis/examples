package com.packt.masterjbpm6.kie;

import java.io.IOException;
import java.io.InputStream;

import org.drools.core.io.impl.UrlResource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;

public class KieTest {

	public static void main(String[] args) {
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
}
