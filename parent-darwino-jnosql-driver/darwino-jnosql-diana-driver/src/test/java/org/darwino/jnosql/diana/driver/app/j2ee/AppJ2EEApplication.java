/**
 * Copyright © 2017-2019 Jesse Gallagher
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
package org.darwino.jnosql.diana.driver.app.j2ee;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;

import org.darwino.jnosql.diana.driver.app.AppManifest;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.platform.ManagedBeansService;
import com.darwino.config.jsonstore.JsonDbSqlite;
import com.darwino.j2ee.application.DarwinoJ2EEApplication;
import com.darwino.jsonstore.meta.DatabaseCustomizer;
import com.darwino.jsonstore.sql.impl.full.JsonDb;
import com.darwino.platform.DarwinoManifest;
import com.darwino.sql.drivers.DBDriver;
import com.darwino.sqlite.JreInstall;

/**
 * J2EE application.
 * 
 * @author Philippe Riand
 */
public class AppJ2EEApplication extends DarwinoJ2EEApplication {
	
	public static DarwinoJ2EEApplication create(ServletContext context) throws JsonException {
		if(!DarwinoJ2EEApplication.isInitialized()) {
			AppJ2EEApplication app = new AppJ2EEApplication(
					context,
					new AppManifest(new AppJ2EEManifest())
			);
			app.init();
		}
		return DarwinoJ2EEApplication.get();
	}
	
	private JsonDb jsonDb;
	
	protected AppJ2EEApplication(ServletContext context, DarwinoManifest manifest) {
		super(context,manifest);
	}
	
	@Override
	public String[] getConfigurationBeanNames() {
		return new String[] {getManifest().getConfigId(),ManagedBeansService.LOCAL_NAME,ManagedBeansService.DEFAULT_NAME};
	}
	
	@Override
	protected DatabaseCustomizer findDatabaseCustomizerFactory(DBDriver driver, String dbName) {
		return new AppDatabaseCustomizer(driver); 
	}

	
	@Override
	protected JsonDb getConnectionBean() {
		if(this.jsonDb == null) {
			try {
				JreInstall.init();
				
				JsonDbSqlite result = new JsonDbSqlite();
				File dbFile = File.createTempFile("jnosql", "db"); //$NON-NLS-1$ //$NON-NLS-2$
				dbFile.deleteOnExit();
				result.setFile(dbFile.getAbsolutePath());
				this.jsonDb = result;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return this.jsonDb;
	}
}
