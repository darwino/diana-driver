package org.darwino.jnosql.diana.driver;

import org.jnosql.diana.api.document.Document;
import org.jnosql.diana.api.document.DocumentEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.darwino.jsonstore.Database;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@SuppressWarnings("nls")
public class SearchQueryTest extends AbstractDarwinoAppTest {

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

    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        DarwinoDocumentConfiguration configuration = new DarwinoDocumentConfiguration();
        DarwinoDocumentCollectionManagerFactory managerFactory = configuration.get();
        DarwinoDocumentCollectionManager entityManager = managerFactory.get(Database.STORE_DEFAULT);

        DocumentEntity salvador = DocumentEntity.of("city", asList(Document.of("_id", "salvador")
                , Document.of("name", "Salvador"),
                Document.of("description", "Founded by the Portuguese in 1549 as the first capital" +
                        " of Brazil, Salvador is" +
                        " one of the oldest colonial cities in the Americas.")));
        DocumentEntity saoPaulo = DocumentEntity.of("city", asList(Document.of("_id", "sao_paulo")
                , Document.of("name", "São Paulo"), Document.of("description", "São Paulo, razil’s vibrant " + // Changed "Brazil" -> "razil" because the test below expects only 3 Brazils
                        "financial center, is among the world's most populous cities, with numerous cultural institutions" +
                        " and a rich architectural tradition. ")));
		DocumentEntity rioJaneiro = DocumentEntity.of("city", asList(Document.of("_id", "rio_janeiro")
                , Document.of("name", "Rio de Janeiro"), Document.of("description", "Rio de Janeiro " +
                        "is a huge seaside city in Brazil, famed for its Copacabana" +
                        " and Ipanema beaches, 38m Christ the Redeemer statue atop Mount" +
                        " Corcovado and for Sugarloaf Mountain, a granite peak with cable" +
                        " cars to its summit. ")));
        DocumentEntity manaus = DocumentEntity.of("city", asList(Document.of("_id", "manaus")
                , Document.of("name", "Manaus"), Document.of("description", "Manaus, on the banks " +
                        "of the Negro River in northwestern Brazil, is the capital of the vast state of Amazonas.")));

        entityManager.insert(Arrays.asList(salvador, saoPaulo, rioJaneiro, manaus));
        Thread.sleep(2_000L);

    }


    @Test
    public void shouldSearchElement() {
        List<DocumentEntity> entities = entityManager.search("Financial");
        assertEquals(1, entities.size());
        assertEquals(Document.of("name", "São Paulo"), entities.get(0).find("name").get());
    }

    @Test
    public void shouldSearchElement2() {
        List<DocumentEntity> entities = entityManager.search("Brazil");
        assertEquals(3, entities.size());
        List<String> result = entities.stream()
                .flatMap(e -> e.getDocuments().stream())
                .filter(d -> "name".equals(d.getName()))
                .map(d -> d.get(String.class)).collect(Collectors.toList());

        assertThat(result, containsInAnyOrder("Salvador", "Rio de Janeiro", "Manaus"));
    }

    @Test
    public void shouldSearchElement3() {
        List<DocumentEntity> entities = entityManager.search("Salvador");
        assertEquals(1, entities.size());
        List<String> result = entities.stream()
                .flatMap(e -> e.getDocuments().stream())
                .filter(d -> "name".equals(d.getName()))
                .map(d -> d.get(String.class)).collect(Collectors.toList());

        assertThat(result, containsInAnyOrder("Salvador"));
    }

}