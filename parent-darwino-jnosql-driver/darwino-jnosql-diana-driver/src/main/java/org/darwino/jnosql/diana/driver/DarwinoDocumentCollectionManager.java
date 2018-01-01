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

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;
import com.darwino.jsonstore.JsqlCursor;

import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentEntity;

import java.util.List;

/**
 * The Darwino implementation of {@link DocumentCollectionManager}
 */
public interface DarwinoDocumentCollectionManager extends DocumentCollectionManager {


    /**
     * Executes the query with params and then returns the result
     *
     * @param query the query
     * @param params    the params
     * @return the query result
     */
    List<DocumentEntity> query(String query, JsonObject params);

    /**
     * Executes the query and then returns the result
     *
     * @param query the query
     * @return the query result
     */
    List<DocumentEntity> query(String query);

    /**
     * Executes the query and then returns the result
     *
     * @param cursor the cursor
     * @return the query result
     */
    List<DocumentEntity> query(Cursor cursor);

    /**
     * Searches in Darwino using Full Text Search
     *
     * @param query the query to be used
     * @return the elements from the query
     */
    List<DocumentEntity> search(String query);

    /**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    List<DocumentEntity> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    List<DocumentEntity> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the JSQL query and then returns the result
     *
     * @param jsqlQuery the query
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    List<DocumentEntity> jsqlQuery(String jsqlQuery) throws NullPointerException;
}