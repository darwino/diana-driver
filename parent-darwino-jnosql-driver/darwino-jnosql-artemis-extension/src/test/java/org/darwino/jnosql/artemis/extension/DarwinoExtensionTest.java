/**
 * Copyright © 2017 Jesse Gallagher
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

import javax.inject.Inject;

import org.darwino.jnosql.artemis.extension.runner.WeldJUnit4Runner;
import org.junit.Test;
import org.junit.runner.RunWith;

@SuppressWarnings("nls")
@RunWith(WeldJUnit4Runner.class)
public class DarwinoExtensionTest {
	@Inject
    private PersonRepositoryAsync personRepositoryAsync;

    @Inject
    private PersonRepository personRepository;

	@Test
    public void shouldSaveOrientDB() {
        Person person = new Person("Ada", 10);
        personRepository.deleteById(person.getName());
        personRepositoryAsync.deleteById(person.getName());
    }
}