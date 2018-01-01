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
 * Includes code derived from the JNoSQL Diana Couchbase driver, copyright
 * Otavio Santana and others and available from:
 *
 * https://github.com/eclipse/jnosql-diana-driver/tree/master/couchbase-driver
 */
package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.JsqlCursor;

import java.util.List;
import java.util.function.Consumer;

/**
 * The Couchbase interface of {@link DocumentCollectionManagerAsync}
 */
public interface DarwinoDocumentCollectionManagerAsync extends DocumentCollectionManagerAsync {


    /**
     * Executes the query with params
     *
     * @param query the query
     * @param params    the params
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void query(String query, JsonObject params, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;

    /**
     * Executes the query and then processes the result
     *
     * @param query the query
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void query(String query, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;

    /**
     * Executes the query and then processes the result
     *
     * @param cursor the query
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void query(Cursor cursor, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;

    /**
     * Searches in Darwino using Full Text Search
     *
     * @param query the query to be used
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void search(String query, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;
    
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
	void jsqlQuery(String jsqlQuery, JsonObject params, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException;

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
	void jsqlQuery(JsqlCursor jsqlQuery, JsonObject params, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException;

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
	void jsqlQuery(String jsqlQuery, Consumer<List<DocumentEntity>> callback) throws NullPointerException, ExecuteAsyncQueryException;

}