package org.darwino.jnosql.diana.driver.app.j2ee;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.security.acl.UserContextFactory;
import com.darwino.commons.security.acl.impl.UserImpl;
import com.darwino.j2ee.application.DarwinoJ2EEContext;
import com.darwino.jre.application.DarwinoJreApplication;
import com.darwino.platform.DarwinoContext;
import com.darwino.platform.DarwinoContextFactory;

public class AppContextFactory implements DarwinoContextFactory {

	public AppContextFactory() {
	}

	@Override
	public DarwinoContext find() {
		try {
			return new DarwinoJ2EEContext(DarwinoJreApplication.get(), null, null, new UserImpl(), new UserContextFactory(), null, DarwinoJreApplication.get().getLocalJsonDBServer().createSystemSession(null));
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

}
