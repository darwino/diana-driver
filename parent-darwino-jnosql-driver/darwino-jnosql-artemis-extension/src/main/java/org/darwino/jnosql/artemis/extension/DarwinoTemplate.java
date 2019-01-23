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
package org.darwino.jnosql.artemis.extension;

import com.darwino.jsonstore.JsqlCursor;
import org.jnosql.artemis.document.DocumentTemplate;

import java.util.Collection;
import java.util.List;

public interface DarwinoTemplate extends DocumentTemplate {
	/**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(String jsqlQuery, Object params);

    /**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(JsqlCursor jsqlQuery, Object params);

    /**
     * Executes the JSQL query and then returns the result
     *
     * @param jsqlQuery the query
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(String jsqlQuery);
	
	/**
     * Searches in Darwino using Full Text Search
     *
     * @param <T>   the type
     * @param query the query to be used
     * @return the elements from the query
     * @throws NullPointerException when either the query or index are null
     */
	<T> List<T> search(String query);
	
	/**
     * Searches in Darwino using Full Text Search
     *
     * @param <T>   the type
     * @param query the query to be used
     * @param orderBy the columns (and optional directions) to order the result by
     * @return the elements from the query
     * @throws NullPointerException when either the query or index are null
     */
	<T> List<T> search(String query, Collection<String> orderBy);

	/**
     * Executes a stored cursor from the Darwino database
     *
     * @param <T>   the type
     * @param cursorName the name of the stored cursor
     * @param params the param object to pass to the cursor execution
     * @return the elements from the query
     */
	<T> List<T> storedCursor(String cursorName, Object params);
}
