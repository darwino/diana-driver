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

import java.util.List;

import org.darwino.jnosql.diana.driver.app.AppBasePlugin;

import com.darwino.commons.platform.beans.ManagedBeansExtension;
import com.darwino.commons.platform.impl.PluginImpl;
import com.darwino.commons.platform.properties.PropertiesExtension;
import com.darwino.commons.security.acl.UserService;
import com.darwino.j2ee.platform.DefaultWebBeanExtension;
import com.darwino.j2ee.platform.DefaultWebPropertiesExtension;
import com.darwino.j2ee.servlet.authentication.AuthenticationService;

@SuppressWarnings("nls")
public class AppPlugin extends PluginImpl {
	
	public AppPlugin() {
		super("J2EE Application");
	}

	@Override
	public void findExtensions(Class<?> serviceClass, List<Object> extensions) {
		AppBasePlugin.findExtensions(serviceClass, extensions);
		
		if(serviceClass==AuthenticationService.class) {
			// User authentication service
			// This service grabs the currently authenticated user from the server context
			// By default it uses the J2EE Principal as provided by the application server, but
			// this can be changed to use alternate authentication methods
			//extensions.add(new AuthenticationService() {
			//	@Override
			//	protected boolean forceHttpSession() {
			//		// We have to set this property to ensure that the basic authentication is kept even
			//		// when the client hits a non protected resource.
			//		return true;
			//	}
			//});
		} else if(serviceClass==UserService.class) {
			// User service
			// This service can point to an LDAP server or a static directory
			// The default uses a bean like bellow
			//extensions.add(new UserServiceBeanDelegate());
		} else if(serviceClass==ManagedBeansExtension.class) {
			// Ok, default web entries - can be replaced by something more accurate
			extensions.add(new DefaultWebBeanExtension());
		} else if(serviceClass==PropertiesExtension.class) {
			// Ok, default web entries - can be replaced by something more accurate
			extensions.add(new DefaultWebPropertiesExtension());
		}
		
		super.findExtensions(serviceClass, extensions);
	}
}
