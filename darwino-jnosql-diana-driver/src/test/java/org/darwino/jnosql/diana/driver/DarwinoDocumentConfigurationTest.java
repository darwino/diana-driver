package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class DarwinoDocumentConfigurationTest extends AbstractDarwinoAppTest  {

    @Test
    public void shouldCreateDocumentCollectionManagerFactoryByMap() {

        DarwinoDocumentConfiguration configuration = new DarwinoDocumentConfiguration();
        DocumentCollectionManagerFactory managerFactory = configuration.get();
        assertNotNull(managerFactory);
    }

    @Test
    public void shouldCreateDocumentCollectionManagerFactoryByFile() {
        DarwinoDocumentConfiguration configuration = new DarwinoDocumentConfiguration();
        DocumentCollectionManagerFactory managerFactory = configuration.get();
        assertNotNull(managerFactory);
    }
}