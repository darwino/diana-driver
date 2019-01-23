package org.darwino.jnosql.artemis.extension;

import org.darwino.jnosql.diana.driver.DarwinoDocumentCollectionManager;

public interface DarwinoTemplateProducer {
	/**
     * creates a {@link DarwinoTemplate}
     *
     * @param collectionManager the collectionManager
     * @return a new instance
     * @throws NullPointerException when collectionManager is null
     */
    DarwinoTemplate get(DarwinoDocumentCollectionManager collectionManager);
}
