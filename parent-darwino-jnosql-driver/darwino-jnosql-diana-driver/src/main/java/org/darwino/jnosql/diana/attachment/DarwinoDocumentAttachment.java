/**
 * Copyright © 2017-2021 Jesse Gallagher
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
package org.darwino.jnosql.diana.attachment;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jnosql.communication.driver.attachment.EntityAttachment;

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
