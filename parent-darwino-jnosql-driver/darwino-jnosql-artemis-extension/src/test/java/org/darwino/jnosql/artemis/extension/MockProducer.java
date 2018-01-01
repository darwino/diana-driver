package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;
import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManagerAsync;
import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.mockito.Mockito;

import javax.enterprise.inject.Produces;

@SuppressWarnings("nls")
public class MockProducer {


	@Produces
    public DarwinoDocumentCollectionManager getManager() {
        DarwinoDocumentCollectionManager manager = Mockito.mock(DarwinoDocumentCollectionManager.class);
        DocumentEntity entity = DocumentEntity.of("Person");
        entity.add(Document.of("name", "Ada"));
        Mockito.when(manager.insert(Mockito.any(DocumentEntity.class))).thenReturn(entity);
        return manager;
    }

    @Produces
    public DarwinoDocumentCollectionManagerAsync getManagerAsync() {
        return Mockito.mock(DarwinoDocumentCollectionManagerAsync.class);
    }
}