package org.darwino.jnosql.artemis.extension;

import java.util.List;

import org.jnosql.artemis.document.DocumentTemplate;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

public interface DarwinoTemplate extends DocumentTemplate {
	/**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(String jsqlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the JSQL query with params and then returns the result
     *
     * @param jsqlQuery the query
     * @param params    the params
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(JsqlCursor jsqlQuery, JsonObject params) throws NullPointerException;

    /**
     * Executes the JSQL query and then returns the result
     *
     * @param jsqlQuery the query
     * @return the query result
     * @throws NullPointerException when either jsqlQuery or params are null
     */
    <T> List<T> jsqlQuery(String jsqlQuery) throws NullPointerException;
	
	/**
     * Searches in Darwino using Full Text Search
     *
     * @param <T>   the type
     * @param query the query to be used
     * @return the elements from the query
     * @throws NullPointerException when either the query or index are null
     */
	<T> List<T> search(String query) throws NullPointerException;
}
