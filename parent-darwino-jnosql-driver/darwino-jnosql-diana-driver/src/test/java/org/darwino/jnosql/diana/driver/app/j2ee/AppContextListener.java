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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.darwino.jnosql.diana.driver.app.AppDatabaseDef;

import com.darwino.commons.Platform;
import com.darwino.commons.json.JsonException;
import com.darwino.commons.tasks.TaskProgress;
import com.darwino.commons.tasks.scheduler.TaskScheduler;
import com.darwino.j2ee.application.AbstractDarwinoContextListener;
import com.darwino.j2ee.application.BackgroundServletSynchronizationExecutor;
import com.darwino.j2ee.application.DarwinoJ2EEApplication;
import com.darwino.platform.events.builder.EventBuilderFactory;
import com.darwino.platform.events.builder.StaticEventBuilder;
import com.darwino.platform.events.jsonstore.JsonStoreChangesTrigger;
import com.darwino.platform.persistence.JsonStorePersistenceService;

@SuppressWarnings("nls")
public class AppContextListener extends AbstractDarwinoContextListener {
	
	public static final boolean HAS_TRIGGERS 	= false;
	public static final boolean HAS_TASKS 		= false;

	private BackgroundServletSynchronizationExecutor syncExecutor; 
	private EventBuilderFactory triggers;
	
	public AppContextListener() {
	}
	
	@Override
	protected DarwinoJ2EEApplication createDarwinoApplication(ServletContext context) throws JsonException {
		return AppJ2EEApplication.create(context);
	}
	
	@Override
	protected void initAsync(ServletContext servletContext, TaskProgress progress) throws JsonException {
		super.initAsync(servletContext, progress);
		
		// Initialize the replication asynchronously so the database is properly deployed before it starts
		initReplication(servletContext, progress);
		
		if(HAS_TRIGGERS) {
			// Enable triggers for notifications
			initTriggers(servletContext, progress);
		}
			
		if(HAS_TASKS) {
			// Enable scheduled tasks
			initTasks(servletContext, progress);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if(syncExecutor!=null) {
			syncExecutor.stop();
			syncExecutor = null;
		}
		if(HAS_TASKS) {
			final TaskScheduler scheduler = Platform.getService(TaskScheduler.class);
			scheduler.removeAllScheduledTasks();
		}

		super.contextDestroyed(sce);
	}

	protected void initReplication(ServletContext servletContext, TaskProgress progress) throws JsonException {
 		// Define these to enable the background replication with another server 
		syncExecutor = new BackgroundServletSynchronizationExecutor(getApplication(),servletContext);
		syncExecutor.putPropertyValue("dwo-sync-database",AppDatabaseDef.DATABASE_NAME);
		syncExecutor.start();
	}


	protected void initTriggers(ServletContext servletContext, TaskProgress progress) throws JsonException {
		// Install the triggers
		// This trigger monitors the document changes in the default database
		// A store, or a query, can be specified as well. By default, the _local store is not processed
		// One can also use JsonStoreTrigger to simply select by query and do not care about the dates
		StaticEventBuilder triggerList = new StaticEventBuilder();
		triggerList.add(new JsonStoreChangesTrigger()
				.scheduler("10s")
				.database(AppDatabaseDef.DATABASE_NAME)
				//.store(Database.STORE_DEFAULT)
				.maxEntries(10) // For demo purposes, only process the last 10 docs...
			);
		
		// Use a persistence service for the dates
		JsonStorePersistenceService svc = new JsonStorePersistenceService()
				.database(AppDatabaseDef.DATABASE_NAME)
				.category("trigger");
		triggers = new EventBuilderFactory(triggerList,svc);
		triggers.install();
	}

	protected void initTasks(ServletContext servletContext, TaskProgress progress) throws JsonException {

	}
}
