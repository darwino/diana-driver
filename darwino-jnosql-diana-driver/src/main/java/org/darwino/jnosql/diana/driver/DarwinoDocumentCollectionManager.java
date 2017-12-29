package org.darwino.jnosql.diana.driver;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.Cursor;

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

}