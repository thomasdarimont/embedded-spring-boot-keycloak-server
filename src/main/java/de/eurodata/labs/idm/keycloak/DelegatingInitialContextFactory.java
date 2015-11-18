package de.eurodata.labs.idm.keycloak;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;

public class DelegatingInitialContextFactory implements InitialContextFactory {

	static Context context = new FixedContext();

	@Override
	public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
		return context;
	}
}
