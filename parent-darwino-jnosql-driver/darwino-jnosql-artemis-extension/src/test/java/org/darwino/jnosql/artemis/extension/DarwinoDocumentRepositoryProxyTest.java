package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.darwino.commons.json.JsonObject;

import javax.inject.Inject;
import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("nls")
@RunWith(WeldJUnit4Runner.class)
public class DarwinoDocumentRepositoryProxyTest {

	private DarwinoTemplate template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    private PersonRepository personRepository;


    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
    public void setUp() {
        this.template = Mockito.mock(DarwinoTemplate.class);

        DarwinoDocumentRepositoryProxy handler = new DarwinoDocumentRepositoryProxy(template,
                classRepresentations, PersonRepository.class, reflections, converters);

        when(template.insert(any(Person.class))).thenReturn(new Person());
        when(template.insert(any(Person.class), any(Duration.class))).thenReturn(new Person());
        when(template.update(any(Person.class))).thenReturn(new Person());
        personRepository = (PersonRepository) Proxy.newProxyInstance(PersonRepository.class.getClassLoader(),
                new Class[]{PersonRepository.class},
                handler);
    }


    @Test
    public void shouldFindAll() {
        personRepository.findAll();
        verify(template).jsqlQuery("select _unid unid from _default where form='Person'");
    }

    @Test
    public void shouldFindByNameJsql() {
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        personRepository.findByName("Ada");
        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where form='Person' and name = ?"), captor.capture());

        JsonObject value = captor.getValue();

        assertEquals("Ada", value.getString("name"));
    }

    interface PersonRepository extends DarwinoRepository<Person, String> {

        @JSQL("select _unid unid from _default where form='Person'")
        List<Person> findAll();

        @JSQL("select _unid unid from _default where form='Person' and name = ?")
        List<Person> findByName(@Param("name") String name);
    }
}
