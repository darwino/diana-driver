package org.darwino.jnosql.diana.attachment;

import java.io.IOException;
import java.io.InputStream;

import org.jnosql.diana.driver.attachment.EntityAttachment;

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.Attachment;
import com.darwino.jsonstore.Database;
import com.darwino.jsonstore.Document;
import com.darwino.jsonstore.Session;
import com.darwino.jsonstore.Store;
import com.darwino.platform.DarwinoContext;

/**
 * Represents a document attachment within a Darwino database
 * 
 * @author Jesse Gallagher
 * @since 0.0.9
 */
public class DarwinoDocumentAttachment implements EntityAttachment {
	private final String databaseName;
	private final String storeId;
	private final String documentUnid;
	private final String name;
	private final long lastModified;
	private final long length;
	private final String contentType;
	private final String etag;
	private transient Attachment att;
	
	public DarwinoDocumentAttachment(Attachment att) throws JsonException {
		Document doc = att.getDocument();
		this.databaseName = doc.getDatabase().getId();
		this.storeId = doc.getStore().getId();
		this.documentUnid = doc.getUnid();
		this.name = att.getName();
		this.length = att.getLength();
		this.lastModified = att.getLastModificationDate().getTime();
		this.contentType = att.getMimeType();
		this.etag = att.getETag();
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	public String getStoreId() {
		return storeId;
	}
	public String getDocumentUnid() {
		return documentUnid;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public long getLastModified() {
		return this.lastModified;
	}
	
	@Override
	public String getContentType() {
		return this.contentType;
	}
	
	@Override
	public long getLength() {
		return length;
	}
	
	@Override
	public String getETag() {
		return etag;
	}
	
	@Override
	public InputStream getData() throws IOException {
		try {
			return getAttachment().getInputStream();
		} catch(JsonException e) {
			throw new IOException("Encountered exception retrieving attachment from Darwino database", e);
		}
	}
	
	public Document getDocument() throws JsonException {
		Session session = DarwinoContext.get().getSession();
		Database database = session.getDatabase(databaseName);
		Store store = database.getStore(storeId);
		return store.loadDocument(documentUnid);
	}
	
	private synchronized Attachment getAttachment() throws JsonException {
		if(this.att == null) {
			Document doc = getDocument();
			this.att = doc.getAttachment(name);
		}
		return this.att;
	}
}
