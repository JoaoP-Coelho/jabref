package org.jabref.logic.importer.fetcher;

import org.jabref.testutils.category.FetcherTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

@FetcherTest
class BiodiversityLibraryTest {

    private BiodiversityLibrary finder;
    private String url;
    private String fakeUrl;

    @BeforeEach
    void setUp() {
        finder = new BiodiversityLibrary();
        this.url = "https://www.biodiversitylibrary.org/api3?op=GetCollections&format=json&apikey=";
        this.fakeUrl = "https://fake-url.com/fake.json";
    }

    @Test
    void getFetchDataShouldReturnJsonWithDataTest() throws Exception {
        var data = finder.fetchData(url);

        assertTrue(data.length() > 0);
        assertTrue(data.contains("\"Status\":\"ok\""));
    }

    @Test
    void getFetchDataShouldThrowExceptionTest() throws Exception {
        assertThrows(Exception.class, () -> {
            finder.fetchData(fakeUrl);
        });
    }

    @Test
    void getCollectionsShouldReturnObjWithDataTest() throws Exception {
        var collections = finder.getCollections();

        assertEquals("ok", collections.getStatus());
        assertEquals("", collections.getErrorMessage());
        assertFalse(collections.getResult().isEmpty());
    }

    @Test
    void getAuthorsShouldReturnObjWithMetadataTest() throws Exception {
        var authors = finder.getAuthorMetadata(87509, 't');

        assertEquals("ok", authors.getStatus());
        assertEquals("", authors.getErrorMessage());
        assertFalse(authors.getResult().isEmpty());
    }

    @Test
    void getAuthorsShouldThrowExceptionTest() throws Exception {
        assertThrows(Exception.class, () -> {
            finder.getAuthorMetadata(0, ' ');
        });
    }

    @Test
    void getSubjectMetadataShouldReturnObjWithMetadataTest() throws Exception {
        var subjects = finder.getSubjectMetadata("water", 't');

        assertEquals("ok", subjects.getStatus());
        assertEquals("", subjects.getErrorMessage());
        assertFalse(subjects.getResult().isEmpty());
    }

    @Test
    void getSubjectMetadataShouldThrowExceptionTest() throws Exception {
        assertThrows(Exception.class, () -> {
            finder.getSubjectMetadata("", ' ');
        });
    }

    @Test
    void publicationSearchShouldReturnObjWithMetadataTest() throws Exception {
        String searchTerm = "cocos+island+costa+rica+birds";
        char searchType = 'C';
        int page = 1, pageSize = 10;

        var publications = finder.publicationSearch(searchTerm, searchType, page, pageSize);

        assertEquals("ok", publications.getStatus());
        assertEquals("", publications.getErrorMessage());
        assertFalse(publications.getResult().isEmpty());
    }

    @Test
    void publicationSearchShouldThrowExceptionTest() throws Exception {
        assertThrows(Exception.class, () -> {
            finder.publicationSearch("", ' ', -1, -1);
        });
    }

    @Test
    void authorSearchShouldReturnObjWithMetadataTest() throws Exception {
        String authorName = "dimmock";

        var authors = finder.authorSearch(authorName);

        assertEquals("ok", authors.getStatus());
        assertEquals("", authors.getErrorMessage());
        assertFalse(authors.getResult().isEmpty());
    }

}
