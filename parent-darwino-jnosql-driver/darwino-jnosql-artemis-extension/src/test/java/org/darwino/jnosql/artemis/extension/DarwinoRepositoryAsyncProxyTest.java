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
import org.jnosql.artemis.Param;
import org.jnosql.artemis.document.DocumentRepositoryAsyncProducer;
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
public class DarwinoRepositoryAsyncProxyTest extends AbstractDarwinoAppTest {

	private DarwinoTemplateAsync template;
	@Inject
	private DocumentRepositoryAsyncProducer producer;
    private PersonAsyncRepository personRepository;


    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
    public void setUp() {
        this.template = Mockito.mock(DarwinoTemplateAsync.class);
        PersonAsyncRepository personAsyncRepository = producer.get(PersonAsyncRepository.class, template);

        DarwinoRepositoryAsyncProxy handler = new DarwinoRepositoryAsyncProxy(template, personAsyncRepository);


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



    @SuppressWarnings({ "unchecked", "unused" })
	@Test
    public void shouldFindNoCallback() {
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        JsonObject params = JsonObject.of("name", "Ada");
        personRepository.queryName("Ada");
        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where $.form='Person' and $.name=:name"), captor.capture(),
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

        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where $.form='Person' and $.name=:name"), Mockito.eq(params), Mockito.eq(callBack));

    }

    interface PersonAsyncRepository extends DarwinoRepositoryAsync<Person, String> {

        Person findByName(String name);


        @JSQL("select _unid unid from _default where $.form='Person' and $.name=:name")
        void queryName(@Param("name") String name);

        @JSQL("select _unid unid from _default where $.form='Person' and $.name=:name")
        void queryName(@Param("name") String name, Consumer<List<Person>> callBack);
    }
}
