/**
 * Copyright © 2017-2019 Jesse Gallagher
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
import org.jnosql.artemis.document.DocumentRepositoryProducer;
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
public class DarwinoDocumentRepositoryProxyTest extends AbstractDarwinoAppTest {

	private DarwinoTemplate template;

    @Inject
    private DocumentRepositoryProducer producer;

    private PersonRepository personRepository;


    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
    public void setUp() {
        this.template = Mockito.mock(DarwinoTemplate.class);

        DarwinoDocumentRepositoryProxy handler = new DarwinoDocumentRepositoryProxy(template,
                PersonRepository.class, producer.get(PersonRepository.class, template));

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
        verify(template).jsqlQuery("select _unid unid from _default where $.form='Person'");
    }

    @Test
    public void shouldFindByNameJsql() {
        ArgumentCaptor<JsonObject> captor = ArgumentCaptor.forClass(JsonObject.class);
        personRepository.findByName("Ada");
        verify(template).jsqlQuery(Mockito.eq("select _unid unid from _default where $.form='Person' and $.name=:name"), captor.capture());

        JsonObject value = captor.getValue();

        assertEquals("Ada", value.getString("name"));
    }

    interface PersonRepository extends DarwinoRepository<Person, String> {

        @JSQL("select _unid unid from _default where $.form='Person'")
        List<Person> findAll();

        @JSQL("select _unid unid from _default where $.form='Person' and $.name=:name")
        List<Person> findByName(@Param("name") String name);
    }
}
