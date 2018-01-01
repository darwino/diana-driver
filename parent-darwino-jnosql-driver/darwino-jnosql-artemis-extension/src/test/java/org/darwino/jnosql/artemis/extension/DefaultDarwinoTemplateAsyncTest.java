package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.reflection.ClassRepresentations;
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
public class DefaultDarwinoTemplateAsyncTest {

	@Inject
    private DocumentEntityConverter converter;

    private DarwinoDocumentCollectionManagerAsync managerAsync;

    private DarwinoTemplateAsync templateAsync;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void setUp() {
        managerAsync = Mockito.mock(DarwinoDocumentCollectionManagerAsync.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(managerAsync);

        templateAsync = new DefaultDarwinoTemplateAsync(converter, instance, classRepresentations, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        entity.add(Document.of("age", 10));
    }


    @SuppressWarnings("unchecked")
	@Test
    public void shouldFind() {
        String query = "select * from Person where name = ?";
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
        String query = "select _unid unid from _default where form='Person' and name = ?";
        Consumer<List<Person>> callBack = p -> {
        };
        templateAsync.jsqlQuery(query, callBack);
        Mockito.verify(managerAsync).jsqlQuery(Mockito.eq(query), Mockito.any(Consumer.class));

    }

}
