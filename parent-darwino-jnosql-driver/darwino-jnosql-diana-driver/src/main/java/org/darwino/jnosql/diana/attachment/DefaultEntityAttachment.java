package org.darwino.jnosql.diana.attachment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Default representation of {@link EntityAttachment} for in-memory data.
 * 
 * @author Jesse Gallagher
 * @since 0.0.9
 */
class DefaultEntityAttachment implements EntityAttachment {
	private final String name;
	private final String contentType;
	private final long lastModified;
	private final byte[] data;
	
	public DefaultEntityAttachment(String name, String contentType, long lastModified, byte[] data) {
		this.name = name;
		this.contentType = contentType;
		this.lastModified = lastModified;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public long getLastModified() {
		return lastModified;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public InputStream getData() throws IOException {
		return new ByteArrayInputStream(data);
	}
	
	@Override
	public long getLength() {
		return data.length;
	}
	
	@Override
	public String getETag() {
		return name + "-" + Long.toString(lastModified, 16); //$NON-NLS-1$
	}

}
