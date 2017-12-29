package org.darwino.jnosql.diana.driver;

import org.darwino.jnosql.diana.driver.app.AppDatabaseDef;
import org.darwino.jnosql.diana.driver.app.j2ee.AppJ2EEApplication;
import org.darwino.jnosql.diana.driver.app.j2ee.AppPlugin;
import org.junit.BeforeClass;

import com.darwino.commons.Platform;
import com.darwino.commons.json.JsonJavaFactory;
import com.darwino.jre.application.DarwinoJreApplication;
import com.darwino.jsonstore.LocalJsonDBServer;

public abstract class AbstractDarwinoAppTest {

	@BeforeClass
	public static void setUpDarwinoApp() throws Exception {
		try {
			Platform.registerPlugin(AppPlugin.class);
			DarwinoJreApplication app = AppJ2EEApplication.create(null);
			app.initDatabase(AppDatabaseDef.DATABASE_NAME, LocalJsonDBServer.DEPLOY_FORCE);
			app.getLocalJsonDBServer().setJsonFactory(JsonJavaFactory.LinkedMapFactory.instance);
		} catch (Throwable t) {
			t.printStackTrace();
			throw t;
		}
	}
}
