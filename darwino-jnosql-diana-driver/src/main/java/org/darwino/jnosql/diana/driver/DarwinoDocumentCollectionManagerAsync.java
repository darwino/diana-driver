package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.ExecuteAsyncQueryException;
import org.jnosql.diana.api.document.DocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.DocumentEntity;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;

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
     * Executes the query and then result que result
     *
     * @param query the query
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void query(String query, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;

    /**
     * Executes the n1qlquery  plain query and then result que result
     *
     * @param cursor the query
     * @param callback  the callback
     * @throws ExecuteAsyncQueryException an async error
     */
    void query(Cursor cursor, Consumer<List<DocumentEntity>> callback) throws ExecuteAsyncQueryException;

}