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
package org.darwino.jnosql.diana.driver.app;

import com.darwino.commons.Platform;
import com.darwino.commons.json.JsonException;
import com.darwino.commons.util.StringUtil;
import com.darwino.jsonstore.Database;
import com.darwino.jsonstore.impl.DatabaseFactoryImpl;
import com.darwino.jsonstore.meta._Database;
import com.darwino.jsonstore.meta._FtSearch;
import com.darwino.jsonstore.meta._Store;

@SuppressWarnings("nls")
public class AppDatabaseDef extends DatabaseFactoryImpl {

	public static final int DATABASE_VERSION	= 1;
	public static final String DATABASE_NAME	= "swnhelpertest";
	
    public static final String[] DATABASES = new String[] {
    	DATABASE_NAME
    };
	
	// The list  of instances is defined through a property for the DB
	public static String[] getInstances() {
		//JsonArray a = new JsonArray(session.getDatabaseInstances(dbName));
		String inst = Platform.getProperty("swnhelper.instances");
		if(StringUtil.isNotEmpty(inst)) {
			return StringUtil.splitString(inst, ',', true);
		}
		return null;
	}	

	@Override
    public String[] getDatabases() throws JsonException {
		return DATABASES;
	}
	
	@Override
	public int getDatabaseVersion(String databaseName) throws JsonException {
		if(StringUtil.equalsIgnoreCase(databaseName, DATABASE_NAME)) {
			return DATABASE_VERSION;
		}
		return -1;
	}
	
	@Override
	public _Database loadDatabase(String databaseName) throws JsonException {
		if(StringUtil.equalsIgnoreCase(databaseName, DATABASE_NAME)) {
			return loadDatabase_swnhelper();
		}
		return null;
	}
	
	public _Database loadDatabase_swnhelper() throws JsonException {
		_Database db = new _Database(DATABASE_NAME, "Stars Without Number Helper", DATABASE_VERSION);

		db.setReplicationEnabled(true);
		
		{
			_Store _def = db.getStore(Database.STORE_DEFAULT);
			_def.setFtSearchEnabled(true);
			_FtSearch ft = (_FtSearch) _def.setFTSearch(new _FtSearch());
			ft.setFields("$");
		}

		return db;
	}
}
