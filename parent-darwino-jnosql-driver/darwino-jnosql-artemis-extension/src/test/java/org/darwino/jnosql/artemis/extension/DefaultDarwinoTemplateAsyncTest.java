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

import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassMappings;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.Mockito.when;

@SuppressWarnings("nls")
@RunWith(WeldJUnit4Runner.class)
public class DefaultDarwinoTemplateAsyncTest extends AbstractDarwinoAppTest {

	@Inject
    private DocumentEntityConverter converter;

    private DarwinoDocumentCollectionManagerAsync managerAsync;

    private DarwinoTemplateAsync templateAsync;

    @Inject
    private ClassMappings mappings;

    @Inject
    private Converters converters;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void setUp() {
        managerAsync = Mockito.mock(DarwinoDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        templateAsync = new DefaultDarwinoTemplateAsync(converter, instance, mappings, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @SuppressWarnings("unchecked")
	@Test
    public void shouldFind() {
        String query = "select * from _default where $.name=:name";
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.of("name", "Ada");
        templateAsync.jsqlQuery(query, params, callBack);
        Mockito.verify(managerAsync).jsqlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));

    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldFindStatement() {
//    		String db = DarwinoApplication.get().getManifest().getDatabases()[0];
//    		JsqlCursor cursor = DarwinoContext.get().getSession().openJsqlCursor().database(db);
    		JsqlCursor query = Mockito.mock(JsqlCursor.class);
        Consumer<List<Person>> callBack = p -> {
        };
        JsonObject params = JsonObject.of("name", "Ada");
        templateAsync.jsqlQuery(query, params, callBack);
        Mockito.verify(managerAsync).jsqlQuery(Mockito.eq(query), Mockito.eq(params), Mockito.any(Consumer.class));
    }

    @SuppressWarnings("unchecked")
	@Test
    public void shouldFind1() {
        String query = "select _unid unid from _default where $.form='Person' and $.name=:name";
        Consumer<List<Person>> callBack = p -> {
        };
        templateAsync.jsqlQuery(query, callBack);
        Mockito.verify(managerAsync).jsqlQuery(Mockito.eq(query), Mockito.any(Consumer.class));

    }

}
