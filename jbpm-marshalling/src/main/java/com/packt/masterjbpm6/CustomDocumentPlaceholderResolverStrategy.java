package com.packt.masterjbpm6;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.persistence.EntityManagerFactory;

import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.kie.api.runtime.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDocumentPlaceholderResolverStrategy extends
		JPAPlaceholderResolverStrategy {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomDocumentPlaceholderResolverStrategy.class);

	public CustomDocumentPlaceholderResolverStrategy(EntityManagerFactory emf) {
		super(emf);
	}

	public CustomDocumentPlaceholderResolverStrategy(Environment env) {
		super(env);
	}

	public boolean accept(Object object) {
		boolean accept = super.accept(object);
		if (accept) {
			logger.info(String.format("accepted %s object: %s", object
					.getClass().getName(), object));
		}
		return accept;
	}

	public void write(ObjectOutputStream os, Object object) throws IOException {
		logger.info(String.format("write %s object: %s", object.getClass()
				.getName(), object));
		super.write(os, object);
	}

	public Object read(ObjectInputStream is) throws IOException,
			ClassNotFoundException {
		Object readobj = super.read(is);

		logger.info(String.format("read %s object: %s", readobj.getClass()
				.getName(), readobj));
		return readobj;
	}

	public byte[] marshal(Context context, ObjectOutputStream os, Object object)
			throws IOException {
		logger.info(String.format("marshal %s object: %s; context=%s", object
				.getClass().getName(), object,
				context != null ? context.toString() : "NULL"));
		return super.marshal(context, os, object);

	}

	public Object unmarshal(Context context, ObjectInputStream ois,
			byte[] object, ClassLoader classloader) throws IOException,
			ClassNotFoundException {

		Object unmarshalled = super
				.unmarshal(context, ois, object, classloader);
		logger.info(String.format("unmarshal %s object: %s; context=%s",
				unmarshalled.getClass().getName(), unmarshalled,
				context != null ? context.toString() : "NULL"));
		return unmarshalled;
	}
}
