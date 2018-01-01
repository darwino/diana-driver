package org.darwino.jnosql.artemis.extension;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.document.DocumentEntityConverter;
import org.jnosql.artemis.document.DocumentEventPersistManager;
import org.jnosql.artemis.document.DocumentWorkflow;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.darwino.commons.json.JsonObject;
import com.darwino.jsonstore.JsqlCursor;

import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("nls")
@RunWith(WeldJUnit4Runner.class)
public class DefaultDarwinoTemplateTest {
	@Inject
    private DocumentEntityConverter converter;

    @Inject
    private DocumentWorkflow flow;

    @Inject
    private DocumentEventPersistManager persistManager;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Converters converters;

    private DarwinoDocumentCollectionManager manager;

    private DarwinoTemplate template;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void setup() {
        manager = Mockito.mock(DarwinoDocumentCollectionManager.class);
        Instance instance = Mockito.mock(Instance.class);
        when(instance.get()).thenReturn(manager);
        template = new DefaultDarwinoTemplate(instance, converter, flow, persistManager, classRepresentations, converters);

        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("_id", "Ada"));
        entity.add(Document.of("age", 10));

        when(manager.search(any(String.class))).thenReturn(singletonList(entity));

    }

    @Test
    public void shouldFindN1ql() {
        JsonObject params = JsonObject.of("name", "Ada");
        template.jsqlQuery("select _unid unid from _default where form='Person' and name = ?", params);
        Mockito.verify(manager).jsqlQuery("select _unid unid from _default where form='Person' and name = ?", params);
    }

    @Test
    public void shouldFindN1qlStatment() {
        JsqlCursor statement = Mockito.mock(JsqlCursor.class);
        JsonObject params = JsonObject.of("name", "Ada");
        template.jsqlQuery(statement, params);
        Mockito.verify(manager).jsqlQuery(statement, params);
    }


    @Test
    public void shouldFindN1ql2() {
        template.jsqlQuery("select _unid unid from _default where form='Person' and name = ?");
        Mockito.verify(manager).jsqlQuery("select _unid unid from _default where form='Person' and name = ?");
    }

    @Test
    public void shouldSearch() {
        List<Person> people = template.search("");

        assertFalse(people.isEmpty());
        assertEquals(1, people.size());
        Person person = people.get(0);

        assertEquals("Ada", person.getName());
    }
}
