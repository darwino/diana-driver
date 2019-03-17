package org.darwino.jnosql.diana.driver;

import org.darwino.jnosql.diana.attachment.DarwinoDocumentAttachment;
import org.darwino.jnosql.diana.attachment.EntityAttachment;
import org.jnosql.diana.api.ValueReader;

import com.darwino.commons.json.JsonException;
import com.darwino.jsonstore.Attachment;

public class DarwinoAttachmentValueReader implements ValueReader {

	@Override
	public <T> boolean isCompatible(Class<T> clazz) {
		return EntityAttachment.class.isAssignableFrom(clazz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T read(Class<T> clazz, Object value) {
		Attachment att = (Attachment)value;
		try {
			return (T) new DarwinoDocumentAttachment(att);
		} catch (JsonException e) {
			throw new RuntimeException(e);
		}
	}

}
