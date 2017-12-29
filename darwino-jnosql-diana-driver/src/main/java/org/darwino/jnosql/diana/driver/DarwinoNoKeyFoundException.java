package org.darwino.jnosql.diana.driver;

/**
 * An exception when {@link DarwinoDocumentCollectionManager} tries to both
 * update and insert, but it does not found The column with the name "_id".
 */
public class DarwinoNoKeyFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	DarwinoNoKeyFoundException(String message) {
		super("The entity was not found at: " + message); //$NON-NLS-1$
	}
}