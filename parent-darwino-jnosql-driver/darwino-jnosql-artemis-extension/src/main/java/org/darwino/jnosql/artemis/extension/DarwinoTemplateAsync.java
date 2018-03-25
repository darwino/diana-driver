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
package org.darwino.jnosql.artemis.extension;

import java.util.List;
import java.util.function.Consumer;

import org.jnosql.artemis.document.DocumentTemplateAsync;
import org.jnosql.diana.api.ExecuteAsyncQueryException;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

public interface DarwinoTemplateAsync extends DocumentTemplateAsync {
	/**
	 * Executes the JSQL query with params and then returns the result
	 *
	 * @param jsqlQuery
	 *            the query
	 * @param params
	 *            the params
	 * @param callback
	 *            the callback
	 * @throws NullPointerException
	 *             when either jsqlQuery or params are null
	 * @throws ExecuteAsyncQueryException an async error
	 */
	<T> void jsqlQuery(String jsqlQuery, JsonObject params, Consumer<List<T>> callback) throws NullPointerException, ExecuteAsyncQueryException;

	/**
	 * Executes the JSQL query with params and then returns the result
	 *
	 * @param jsqlQuery
	 *            the query
	 * @param params
	 *            the params
	 * @param callback
	 *            the callback
	 * @throws NullPointerException
	 *             when either jsqlQuery or params are null
	 * @throws ExecuteAsyncQueryException an async error
	 */
	<T> void jsqlQuery(JsqlCursor jsqlQuery, JsonObject params, Consumer<List<T>> callback) throws NullPointerException, ExecuteAsyncQueryException;

	/**
	 * Executes the JSQL query and then returns the result
	 *
	 * @param jsqlQuery
	 *            the query
	 * @param callback
	 *            the callback
	 * @throws NullPointerException
	 *             when either jsqlQuery or params are null
	 * @throws ExecuteAsyncQueryException an async error
	 */
	<T> void jsqlQuery(String jsqlQuery, Consumer<List<T>> callback) throws NullPointerException, ExecuteAsyncQueryException;

	/**
	 * Searches in Darwino using Full Text Search
	 *
	 * @param <T>
	 *            the type
	 * @param query
	 *            the query to be used
	 * @param callback
	 *            the callback
	 * @throws NullPointerException
	 *             when either the query or index are null
	 * @throws ExecuteAsyncQueryException an async error
	 */
	<T> void search(String query, Consumer<List<T>> callback) throws NullPointerException, ExecuteAsyncQueryException;
}
