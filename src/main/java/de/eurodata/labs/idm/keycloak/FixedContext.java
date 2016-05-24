package de.eurodata.labs.idm.keycloak;

import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicReference;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class FixedContext implements Context {

	AtomicReference<DataSource> dataSource = new AtomicReference<>();
	AtomicReference<EmbeddedCacheManager> cacheManager = new AtomicReference<>();

	@Override
	public Object lookup(Name name) throws NamingException {

		if (name.toString().contains("datasources/KeycloakDS")) {
			return dataSource.get();
		}
		
		if (name.toString().contains("infinispan/Keycloak")) {
			return cacheManager.get();
		}

		throw new NamingException("not found");
	}

	@Override
	public Object lookup(String name) throws NamingException {

		if (name.contains("datasources/KeycloakDS")) {
			dataSource.compareAndSet(null, new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build());
			return dataSource.get();
		}

		if (name.contains("infinispan/Keycloak")) {

            ConfigurationBuilder cacheConfigurationBuilder = new ConfigurationBuilder();
            cacheConfigurationBuilder.invocationBatching().enable();
            cacheConfigurationBuilder.transaction().lockingMode(LockingMode.PESSIMISTIC);

            Configuration config = cacheConfigurationBuilder.build() ;
            DefaultCacheManager cacheManager = new DefaultCacheManager(config);

            this.cacheManager.compareAndSet(null, cacheManager);
			return this.cacheManager.get();
		}

		System.out.println(getClass().getName() + " lookup: " + name);

		throw new NamingException("not found: " + name);
	}

	@Override
	public void bind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebind(Name name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rebind(String name, Object obj) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unbind(String name) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(Name oldName, Name newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(String oldName, String newName) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroySubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroySubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub

	}

	@Override
	public Context createSubcontext(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context createSubcontext(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(Name name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object lookupLink(String name) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NameParser getNameParser(Name name) throws NamingException {
		return null;
	}

	@Override
	public NameParser getNameParser(String name) throws NamingException {
		return new NameParser() {

			@Override
			public Name parse(String name) throws NamingException {
				return new CompositeName(name);
			}
		};
	}

	@Override
	public Name composeName(Name name, Name prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String composeName(String name, String prefix) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addToEnvironment(String propName, Object propVal) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object removeFromEnvironment(String propName) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Hashtable<?, ?> getEnvironment() throws NamingException {
		
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for(StackTraceElement ste : stackTrace){
			if(ste.getClassName().equals("org.springframework.jndi.JndiLocatorDelegate") && 
			ste.getMethodName().equals("isDefaultJndiEnvironmentAvailable")){
				throw new NamingException("avoid creation of a JndiPropertySource by Spring");
			}
		}
		
		return null;
	}

	@Override
	public void close() throws NamingException {
	}

	@Override
	public String getNameInNamespace() throws NamingException {
		return null;
	}

}
