/**
 * Copyright © 2017-2019 Jesse Gallagher
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
package org.darwino.jnosql.diana.driver;

import jakarta.nosql.TypeReference;
import jakarta.nosql.document.Document;
import jakarta.nosql.document.DocumentDeleteQuery;
import jakarta.nosql.document.DocumentEntity;
import jakarta.nosql.document.DocumentQuery;
import jakarta.nosql.document.Documents;
import org.junit.AfterClass;
import org.junit.Test;

import com.darwino.jsonstore.Database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static jakarta.nosql.document.DocumentQuery.select;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({ "nls", "resource" })
public class DarwinoDocumentCollectionManagerTest extends AbstractDarwinoAppTest {

	public static final String COLLECTION_NAME = "person";
	private DarwinoDocumentCollectionManager entityManager;

	{
		DarwinoDocumentConfiguration configuration = new DarwinoDocumentConfiguration();
		DarwinoDocumentCollectionManagerFactory managerFactory = configuration.get();
		entityManager = managerFactory.get(Database.STORE_DEFAULT);
	}

	@AfterClass
	public static void afterClass() {
	}

	@Test
	public void shouldSave() {
		DocumentEntity entity = getEntity();
		DocumentEntity documentEntity = entityManager.insert(entity);
		assertEquals(entity, documentEntity);
	}
	
	@Test
	public void shouldSaveAutoUnid() {
		DocumentEntity entity = getEntity();
		entity.remove("_id");
		DocumentEntity documentEntity = entityManager.insert(entity);
		assertTrue(documentEntity.find("_id").isPresent());
	}

	@Test
	public void shouldSaveWithKey() {
		DocumentEntity entity = getEntity();
		entity.add("_key", "anyvalue");
		DocumentEntity documentEntity = entityManager.insert(entity);
		assertEquals(entity, documentEntity);
	}

	@Test
    public void shouldUpdateSave() {
    		try {
	        DocumentEntity entity = getEntity();
	        @SuppressWarnings("unused")
			DocumentEntity documentEntity = entityManager.insert(entity);
	        Document newField = Documents.of("newField", "10");
	        entity.add(newField);
	        DocumentEntity updated = entityManager.update(entity);
	        assertEquals(newField, updated.find("newField").get());
    		} catch(Exception e) {
    			e.printStackTrace();
    			throw e;
    		}
    }

	@Test
	public void shouldRemoveEntityByName() {
		DocumentEntity documentEntity = entityManager.insert(getEntity());

		Optional<Document> name = documentEntity.find("name");
		DocumentQuery query = select().from(COLLECTION_NAME)
				.where("name").eq(name.get().get())
				.build();
		DocumentDeleteQuery deleteQuery = DocumentDeleteQuery.delete().from(COLLECTION_NAME)
				.where("name").eq(name.get().get())
				.build();
		entityManager.delete(deleteQuery);
		assertTrue(entityManager.select(query).count() == 0);
	}

	@Test
	public void shouldSaveSubDocument() throws InterruptedException {
		DocumentEntity entity = getEntity();
		entity.add(Document.of("phones", Document.of("mobile", "1231231")));
		DocumentEntity entitySaved = entityManager.insert(entity);
		Thread.sleep(5_00L);
		Document id = entitySaved.find("_id").get();

		DocumentQuery query = select().from(COLLECTION_NAME)
				.where("_id").eq(id.get())
				.build();
		DocumentEntity entityFound = entityManager.select(query).findFirst().orElse(null);
		Document subDocument = entityFound.find("phones").get();
		List<Document> documents = subDocument.get(new TypeReference<List<Document>>() {
		});
		assertThat(documents, contains(Document.of("mobile", "1231231")));
	}

	@Test
	public void shouldSaveSubDocument2() throws InterruptedException {
		DocumentEntity entity = getEntity();
		entity.add(Document.of("phones", asList(Document.of("mobile", "1231231"), Document.of("mobile2", "1231231"))));
		DocumentEntity entitySaved = entityManager.insert(entity);
		Thread.sleep(1_00L);
		Document id = entitySaved.find("_id").get();
		DocumentQuery query = select().from(COLLECTION_NAME)
				.where("_id").eq(id.get())
				.build();
		DocumentEntity entityFound = entityManager.select(query).findFirst().orElse(null);
		System.out.println("shouldSaveSubDocument2 entity found is " + entityFound);
		Document subDocument = entityFound.find("phones").get();
		List<Document> documents = subDocument.get(new TypeReference<List<Document>>() {
		});
		assertThat(documents, contains(Document.of("mobile", "1231231"), Document.of("mobile2", "1231231")));
	}

	@Test
	public void shouldSaveSetDocument() throws InterruptedException {
		try {
			Set<String> set = new HashSet<>();
			set.add("Acarajé");
			set.add("Munguzá");
			DocumentEntity entity = DocumentEntity.of(COLLECTION_NAME);
			entity.add(Document.of("_id", "id" + System.currentTimeMillis()));
			entity.add(Document.of("foods", set));
			entityManager.insert(entity);
			Document id = entity.find("_id").get();
			Thread.sleep(1_000L);
			DocumentQuery query = select().from(COLLECTION_NAME)
					.where("_id").eq(id.get())
					.build();
			DocumentEntity entityFound = entityManager.singleResult(query).get();
			Optional<Document> foods = entityFound.find("foods");
			Set<String> setFoods = foods.get().get(new TypeReference<Set<String>>() {
			});
			assertEquals(set, setFoods);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private DocumentEntity getEntity() {
		DocumentEntity entity = DocumentEntity.of(COLLECTION_NAME);
		Map<String, Object> map = new HashMap<>();
		map.put("name", "Poliana");
		map.put("city", "Salvador");
		map.put("_id", "id" + System.currentTimeMillis());

		List<Document> documents = Documents.of(map);
		documents.forEach(entity::add);
		return entity;
	}

}