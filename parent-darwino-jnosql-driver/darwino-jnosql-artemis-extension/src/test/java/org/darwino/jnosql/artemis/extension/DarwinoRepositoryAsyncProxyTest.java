package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.artemis.extension.DarwinoRepositoryAsyncProxy;
import org.darwino.jnosql.artemis.extension.DarwinoTemplateAsync;
import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.jnosql.artemis.Converters;
import org.jnosql.artemis.DynamicQueryException;
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
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@SuppressWarnings("nls")
@RunWith(WeldJUnit4Runner.class)
public class DarwinoRepositoryAsyncProxyTest {

	private DarwinoTemplateAsync template;

    @Inject
    private ClassRepresentations classRepresentations;

    @Inject
    private Reflections reflections;

    @Inject
    private Converters converters;

    private PersonAsyncRepository personRepository;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void setUp() {
        this.template = Mockito.mock(DarwinoTemplateAsync.class);

        DarwinoRepositoryAsyncProxy handler = new DarwinoRepositoryAsyncProxy(template,
                classRepresentations, PersonAsyncRepository.class, reflections, converters);


        personRepository = (PersonAsyncRepository) Proxy.newProxyInstance(PersonAsyncRepository.class.getClassLoader(),
                new Class[]{PersonAsyncRepository.class},
                handler);
    }



    @Test
    public void shouldUpdate() {
        ArgumentCaptor<Person> captor = ArgumentCaptor.forClass(Person.class);
        Person person = new Person("Ada", 12);
        template.update(person);
        verify(template).update(captor.capture());
        Person value = captor.getValue();
        assertEquals(person, value);
    }


    @Test(expected = DynamicQueryException.class)
    public void shouldReturnError() {
        personRepository.findByName("Ada");
    }



    @SuppressWarnings({ "unchecked", "unused" })
	@Test
    public void shouldFindNoCallback() {
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        JsonObject params = JsonObject.of("name", "Ada");
        personRepository.queryName("Ada");
        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where form='Person' and name= ?"), captor.capture(),
                any(Consumer.class));

        JsonObject value = captor.getValue();
        assertEquals("Ada", value.getString("name"));
    }

    @Test
    public void shouldFindByNameFromJsql() {
        Consumer<List<Person>> callBack = p -> {
        };

        JsonObject params = JsonObject.of("name", "Ada");
        personRepository.queryName("Ada", callBack);

        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where form='Person' and name=?"), Mockito.eq(params), Mockito.eq(callBack));

    }

    interface PersonAsyncRepository extends DarwinoRepositoryAsync<Person, String> {

        Person findByName(String name);


        @JSQL("select _unid unid from _default where form='Person' and name= ?")
        void queryName(@Param("name") String name);

        @JSQL("select _unid unid from _default where form='Person' and name=?")
        void queryName(@Param("name") String name, Consumer<List<Person>> callBack);
    }
}
