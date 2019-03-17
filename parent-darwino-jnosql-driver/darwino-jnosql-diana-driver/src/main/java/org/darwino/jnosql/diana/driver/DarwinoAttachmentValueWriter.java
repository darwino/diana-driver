package org.darwino.jnosql.diana.driver;

import java.io.IOException;
import java.util.Date;

import org.darwino.jnosql.diana.attachment.DarwinoDocumentAttachment;
import org.darwino.jnosql.diana.attachment.EntityAttachment;
import org.jnosql.diana.api.ValueWriter;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.util.io.content.InputStreamContent;
import com.darwino.jsonstore.Attachment;
import com.darwino.jsonstore.Document;

public class DarwinoAttachmentValueWriter implements ValueWriter<EntityAttachment, Attachment> {

	@Override
	public <T> boolean isCompatible(Class<T> clazz) {
		return DarwinoDocumentAttachment.class.isAssignableFrom(clazz);
	}

	@Override
	public Attachment write(EntityAttachment object) {
		DarwinoDocumentAttachment docAtt = (DarwinoDocumentAttachment)object;
		String name = docAtt.getName();
		try {
			Document doc = docAtt.getDocument();
			if(doc.attachmentExists(name)) {
				Attachment att = doc.getAttachment(name);
				if(att.getLastModificationDate().before(new Date(docAtt.getLastModified()))) {
					doc.getAttachment(name).deleteAttachment();
				} else {
					return att;
				}
			}
			
			return doc.createAttachment(name, new InputStreamContent(docAtt.getData(), -1, docAtt.getContentType()));
		} catch(JsonException | IOException e) {
			throw new RuntimeException(e);
		}
	}

}
