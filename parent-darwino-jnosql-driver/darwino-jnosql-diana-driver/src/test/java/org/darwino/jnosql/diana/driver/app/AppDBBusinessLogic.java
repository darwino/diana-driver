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
package org.darwino.jnosql.diana.driver.app;

import com.darwino.jsonstore.extensions.DefaultExtensionRegistry;
import com.darwino.jsonstore.impl.DarwinoInfCursorFactory;
import com.darwino.jsonstore.local.DefaultDatabaseACLFactory;

/**
 * Database Business logic - event handlers.
 * 
 * @author Philippe Riand
 */
public  class AppDBBusinessLogic extends DefaultExtensionRegistry {
	
	@SuppressWarnings("unused")
	public AppDBBusinessLogic() {
		// Add here the database events to register to the JSON store
//		registerDocumentEvents("<My Database Id>", "<My Store Id>", new DocumentEvents() {
//			@Override
//			public void querySaveDocument(Document doc) throws JsonException {
//			}
//		});
		
		// Use a query factory
		setQueryFactory(new DarwinoInfCursorFactory(getClass()));
		
		// Default database ACL factory read the design element coming from Domino
		if(false) {
			setDatabaseACLFactory(new DefaultDatabaseACLFactory());
		}
	}
}
