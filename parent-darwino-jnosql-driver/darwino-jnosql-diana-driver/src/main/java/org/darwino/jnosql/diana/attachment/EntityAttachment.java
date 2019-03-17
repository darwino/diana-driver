package org.darwino.jnosql.diana.attachment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a binary attachment attached to a JNoSQL entity.
 * @author Jesse Gallagher
 * @since 0.0.9
 */
public interface EntityAttachment {
	String getName();
	long getLastModified();
	String getContentType();
	InputStream getData() throws IOException;
	long getLength();
	String getETag();
	
	/**
	 * Creates a new in-memory {@link EntityAttachment} for the provided information
	 * 
	 * @param name the name of the attachment
	 * @param lastModified the last modification date, in ms since the epoch
	 * @param contentType the MIME type of the content
	 * @param data the data if the attachment
	 * @return a new {@link EntityAttachment}
	 */
	static EntityAttachment of(String name, long lastModified, String contentType, byte[] data) {
		return new DefaultEntityAttachment(name, contentType, lastModified, data);
	}
}
