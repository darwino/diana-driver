package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.Settings;
import org.jnosql.diana.api.document.UnaryDocumentConfiguration;

import com.darwino.commons.json.JsonException;
import com.darwino.commons.util.StringUtil;
import com.darwino.platform.DarwinoApplication;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DarwinoDocumentConfiguration implements UnaryDocumentConfiguration<DarwinoDocumentCollectionManagerFactory> {
	
	public static final String DATABASE_ID = "databaseId"; //$NON-NLS-1$

	@Override
	public DarwinoDocumentCollectionManagerFactory get() throws UnsupportedOperationException {
		try {
			return new DarwinoDocumentCollectionManagerFactory(DarwinoApplication.get().getManifest().getDatabases()[0]);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory get(Settings settings) throws NullPointerException {
		requireNonNull(settings, "settings is required");

		Map<String, String> configurations = new HashMap<>();
		settings.entrySet().forEach(e -> configurations.put(e.getKey(), e.getValue().toString()));

		String databaseId = configurations.get(DATABASE_ID);
		if(StringUtil.isEmpty(databaseId)) {
			databaseId = DarwinoApplication.get().getManifest().getDatabases()[0];
		}
		try {
			return new DarwinoDocumentCollectionManagerFactory(databaseId);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory getAsync() throws UnsupportedOperationException {
		try {
			return new DarwinoDocumentCollectionManagerFactory(DarwinoApplication.get().getManifest().getDatabases()[0]);
		} catch (JsonException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public DarwinoDocumentCollectionManagerFactory getAsync(Settings settings) throws NullPointerException {
		return get(settings);
	}
}