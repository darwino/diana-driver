package org.darwino.jnosql.diana.driver;

import org.junit.Before;
import org.junit.Test;

import com.darwino.jsonstore.Database;

import static org.junit.Assert.*;

public class DarwinoDocumentCollectionManagerFactoryTest extends AbstractDarwinoAppTest {

	private DarwinoDocumentConfiguration configuration;

	@Before
	public void setUp() {
		configuration = new DarwinoDocumentConfiguration();

	}

	@Test
	public void shouldCreateEntityManager() {
		DarwinoDocumentCollectionManagerFactory factory = configuration.get();
		assertNotNull(factory.get(Database.STORE_DEFAULT));
	}

	@Test
	public void shouldCreateEntityManagerAsync() {
		DarwinoDocumentCollectionManagerFactory factory = configuration.getAsync();
		assertNotNull(factory.getAsync(Database.STORE_DEFAULT));
	}
}