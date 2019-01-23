/**
 * Copyright © 2017-2018 Jesse Gallagher
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
package j2ee;

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
