/**
 * Copyright © 2017 Jesse Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Includes code derived from the JNoSQL Diana Couchbase driver and Artemis
 * extensions, copyright Otavio Santana and others and available from:
 *
 * https://github.com/eclipse/jnosql-diana-driver/tree/master/couchbase-driver
 * https://github.com/eclipse/jnosql-artemis-extension/tree/master/couchbase-extension
 */
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
