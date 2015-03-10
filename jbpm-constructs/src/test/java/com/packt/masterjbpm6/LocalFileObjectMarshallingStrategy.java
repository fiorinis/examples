package com.packt.masterjbpm6;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Id;

import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalFileObjectMarshallingStrategy implements
		ObjectMarshallingStrategy {
	private static Logger log = LoggerFactory
			.getLogger(LocalFileObjectMarshallingStrategy.class);
	private static String parentFolder = System.getProperty("java.io.tmpdir");

	private ObjectMarshallingStrategyAcceptor acceptor;

	public LocalFileObjectMarshallingStrategy() {
		acceptor = new ClassObjectMarshallingStrategyAcceptor();
	}

	public LocalFileObjectMarshallingStrategy(
			ObjectMarshallingStrategyAcceptor acceptor) {
		this.acceptor = acceptor;
	}

	public LocalFileObjectMarshallingStrategy(String targetPath,
			ObjectMarshallingStrategyAcceptor acceptor) {
		this(acceptor);
		parentFolder = targetPath;
	}

	public boolean accept(Object object) {
		return isEntity(object);
	}

	public void write(ObjectOutputStream os, Object object) throws IOException {
		Object id = getClassIdValue(object);
		if (id == null) {
			// save
			id = getClassIdValue(object);
			File newf = new File("");

		} else {
			// replace
			// since this is invoked by marshaller it's safe to call flush
			// and it's important to be flushed so subsequent unmarshall
			// operations
			// will get update content especially when merged
		}
		os.writeUTF(object.getClass().getCanonicalName());
		os.writeObject(id);
	}

	public Object read(ObjectInputStream is) throws IOException,
			ClassNotFoundException {
		String canonicalName = is.readUTF();
		Object id = is.readObject();

		return null;
	}

	public byte[] marshal(Context context, ObjectOutputStream os, Object object)
			throws IOException {
		Object id = getClassIdValue(object);
		// EntityManager em = emf.createEntityManager();
		// if (id == null) {
		// em.persist(object);
		// id = getClassIdValue(object);
		// } else {
		// em.merge(object);
		// // since this is invoked by marshaller it's safe to call flush
		// // and it's important to be flushed so subsequent unmarshall
		// // operations
		// // will get update content especially when merged
		// em.flush();
		// }

		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(buff);
		oos.writeUTF(object.getClass().getCanonicalName());
		oos.writeObject(id);
		oos.close();
		return buff.toByteArray();
	}

	public Object unmarshal(Context context, ObjectInputStream ois,
			byte[] object, ClassLoader classloader) throws IOException,
			ClassNotFoundException {
		DroolsObjectInputStream is = new DroolsObjectInputStream(
				new ByteArrayInputStream(object), classloader);
		String canonicalName = is.readUTF();
		Object id = is.readObject();

		return Class.forName(canonicalName);
	}

	public Context createContext() {
		// no need for context
		return null;
	}

	public static Serializable getClassIdValue(Object o) {
		Class<? extends Object> varClass = o.getClass();
		Serializable idValue = null;
		try {
			do {
				Field[] fields = varClass.getDeclaredFields();
				for (int i = 0; i < fields.length && idValue == null; i++) {
					Field field = fields[i];
					Id id = field.getAnnotation(Id.class);
					if (id != null) {
						try {
							idValue = callIdMethod(
									o,
									"get"
											+ Character.toUpperCase(field
													.getName().charAt(0))
											+ field.getName().substring(1));
						} catch (NoSuchMethodException e) {
							idValue = (Serializable) field.get(o);
						}
					}
				}
			} while ((varClass = varClass.getSuperclass()) != null
					&& idValue == null);
			if (idValue == null) {
				varClass = o.getClass();
				do {
					Method[] methods = varClass.getMethods();
					for (int i = 0; i < methods.length && idValue == null; i++) {
						Method method = methods[i];
						Id id = method.getAnnotation(Id.class);
						if (id != null) {
							idValue = (Serializable) method.invoke(o);
						}
					}
				} while ((varClass = varClass.getSuperclass()) != null
						&& idValue == null);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage());
		}
		return idValue;
	}

	private static Serializable callIdMethod(Object target, String methodName)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return (Serializable) target.getClass()
				.getMethod(methodName, (Class[]) null)
				.invoke(target, new Object[] {});
	}

	private static boolean isEntity(Object o) {
		Class<? extends Object> varClass = o.getClass();
		do {
			Field[] fields = varClass.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					return true;
				}
			}
		} while ((varClass = varClass.getSuperclass()) != null);
		varClass = o.getClass();
		do {
			Method[] methods = varClass.getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				Id id = method.getAnnotation(Id.class);
				if (id != null) {
					return true;
				}
			}
		} while ((varClass = varClass.getSuperclass()) != null);

		return false;
	}

}
