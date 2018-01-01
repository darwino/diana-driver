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
