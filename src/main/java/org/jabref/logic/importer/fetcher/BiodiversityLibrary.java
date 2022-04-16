package org.jabref.logic.importer.fetcher;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiodiversityLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(BiodiversityLibrary.class);

    private static final String API_COLLECTION_URL = "https://www.biodiversitylibrary.org/api3?op=GetCollections&format=json&apikey=";
    private static final String API_AUTHOR_URL = "https://www.biodiversitylibrary.org/api3?op=GetAuthorMetadata&";
    private static final String API_SUBJECT_URL = "https://www.biodiversitylibrary.org/api3?op=GetSubjectMetadata&";
    private static final String API_PUBLICATION_SEARCH_URL = "https://www.biodiversitylibrary.org/api3?op=PublicationSearch&";
    private static final String API_AUTHOR_SEARCH_URL = "https://www.biodiversitylibrary.org/api3?op=AuthorSearch&";
    private static final String API_KEY = "35c3b825-b2a5-4fd3-8c58-e16a6862a9fe";

    public ResponseAuthorMetadata getAuthorMetadata(int id, char pubs) throws Exception {
        var response = this.fetchData(API_AUTHOR_URL + "id=" + id + "&pubs=" + pubs + "&format=json&apikey=");
        var authors = new Gson().fromJson(response.toString(), ResponseAuthorMetadata.class);

        if ((authors != null) && authors.getStatus().equals("ok")) {
            LOGGER.info("getAuthorMetadata: " + authors.toString());
            return authors;
        }

        return null;
    }

    public ResponseSubjectMetadata getSubjectMetadata(String subject, char pubs) throws Exception {
        var response = this.fetchData(API_SUBJECT_URL + "subject=" + subject + "&pubs=" + pubs + "&format=json&apikey=");
        var subjects = new Gson().fromJson(response.toString(), ResponseSubjectMetadata.class);

        if ((subjects != null) && subjects.getStatus().equals("ok")) {
            LOGGER.info("getSubjectMetadata: " + subjects.toString());
            return subjects;
        }

        return null;
    }

    public ResponseCollection getCollections() throws Exception {
        var response = this.fetchData(API_COLLECTION_URL);
        var collections = new Gson().fromJson(response.toString(), ResponseCollection.class);

        if ((collections != null) && collections.getStatus().equals("ok")) {
            LOGGER.info("getCollections: " + collections.toString());
            return collections;
        }

        return null;
    }

    public ResponsePublicationSearch publicationSearch(String searchTerm, char searchType,
                                                       int page, int pageSize)
        throws Exception {
        var response = this.fetchData(API_PUBLICATION_SEARCH_URL
                                      + "searchterm=" + searchTerm + "&searchtype=" + searchType + "&page="
                                      + page + "&pageSize=" + pageSize + "&format=json&apikey=");
        var publications = new Gson().fromJson(response.toString(), ResponsePublicationSearch.class);

        if ((publications != null) && publications.getStatus().equals("ok")) {
            LOGGER.info("getCollections: " + publications.toString());
            return publications;
        }

        return null;
    }

    public ResponseAuthorSearch authorSearch(String authorName) throws Exception {
        var response = this.fetchData(API_AUTHOR_SEARCH_URL + "authorname="
                                      + authorName + "&format=json&apikey=");
        var authors = new Gson().fromJson(response.toString(), ResponseAuthorSearch.class);

        if ((authors != null) && authors.getStatus().equals("ok")) {
            LOGGER.info("getCollections: " + authors.toString());
            return authors;
        }

        return null;
    }

    public String fetchData(String apiUrl) throws Exception {
        try {
            URL url = new URL(apiUrl + API_KEY);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();

            while (sc.hasNextLine()) {
                response.append(sc.nextLine());
            }

            sc.close();

            LOGGER.info("fetchData: " + response.toString());

            return response.toString();
        } catch (Exception e) {
            throw new Exception();
        }
    }

}

class ResponseCollection {

    private String Status;
    private String ErrorMessage;
    private final List<Collection> Result = new ArrayList<>();

    public ResponseCollection() {}

    public String getStatus() {
        return Status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public List<Collection> getResult() {
        return Result;
    }

    @Override
    public String toString() {
        return "ResponseCollection [Status=" + Status + ", Result=" + Result + "]";
    }

}

class Collection {

    private Integer CollectionID;
    private String CollectionName;
    private String CollectionDescription;

    public Collection() {
    }

    public Integer getCollectionID() {
        return CollectionID;
    }

    public String getCollectionName() {
        return CollectionName;
    }

    public String getCollectionDescription() {
        return CollectionDescription;
    }

    @Override
    public String toString() {
        return "Collection [CollectionID=" + CollectionID + ", CollectionName=" + CollectionName
               + ", CollectionDescription=" + CollectionDescription + "]";
    }

}

class ResponseAuthorMetadata {

    private String Status;
    private String ErrorMessage;
    private final List<Author> Result = new ArrayList<>();

    public ResponseAuthorMetadata() {
    }

    public String getStatus() {
        return Status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public List<Author> getResult() {
        return Result;
    }

    @Override
    public String toString() {
        return "ResponseAuthorMetadata [Status=" + Status + ", ErrorMessage=" + ErrorMessage + ", Result=" + Result + "]";
    }

}

class Author {

    private String AuthorID;
    private String Name;
    private String Unit;
    private String CreatorUrl;
    private String CreationDate;

    private final List<Identifier> Identifiers = new ArrayList<>();
    private final List<Author> Publications = new ArrayList<>();

    public Author() {
    }

    public String getAuthorID() {
        return AuthorID;
    }

    public String getName() {
        return Name;
    }

    public String getUnit() {
        return Unit;
    }

    public String getCreatorUrl() {
        return CreatorUrl;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public List<Identifier> getIdentifiers() {
        return Identifiers;
    }

    public List<Author> getPublications() {
        return Publications;
    }

    @Override
    public String toString() {
        return "Author [AuthorID=" + AuthorID + ", Name=" + Name + ", Unit=" + Unit + ", CreatorUrl=" + CreatorUrl + ", CreationDate=" + CreationDate + ", Identifiers=" + Identifiers + ", Publications=" + Publications + "]";
    }

}

class Identifier {

    private String IdentifierName;
    private String IdentifierValue;

    public Identifier() {}

    public String getIdentifierName() {
        return IdentifierName;
    }

    public String getIdentifierValue() {
        return IdentifierValue;
    }

    @Override
    public String toString() {
        return "Identifier [IdentifierName=" + IdentifierName + ", IdentifierValue=" + IdentifierValue + "]";
    }

}

class Publication {

    private String BHLType;
    private String FoundIn;
    private Integer TitleID;
    private String TitleUrl;
    private String MaterialType;
    private String PublisherPlace;
    private String PublisherName;
    private String PublicationDate;
    private final List<AuthorName> Authors = new ArrayList<>();
    private String Genre;
    private String Title;

    public Publication() {
    }

    public String getBHLType() {
        return BHLType;
    }

    public String getFoundIn() {
        return FoundIn;
    }

    public Integer getTitleID() {
        return TitleID;
    }

    public String getTitleUrl() {
        return TitleUrl;
    }

    public String getMaterialType() {
        return MaterialType;
    }

    public String getPublisherPlace() {
        return PublisherPlace;
    }

    public String getPublisherName() {
        return PublisherName;
    }

    public String getPublicationDate() {
        return PublicationDate;
    }

    public List<AuthorName> getAuthors() {
        return Authors;
    }

    public String getGenre() {
        return Genre;
    }

    public String getTitle() {
        return Title;
    }

    @Override
    public String toString() {
        return "Publication [BHLType=" + BHLType + ", FoundIn=" + FoundIn + ", TitleID=" + TitleID + ", TitleUrl=" + TitleUrl + ", MaterialType=" + MaterialType + ", PublisherPlace=" + PublisherPlace + ", PublisherName=" + PublisherName + ", PublicationDate=" + PublicationDate + ", Authors=" + Authors + ", Genre=" + Genre + ", Title=" + Title + "]";
    }

}

class AuthorName {

    private String Name;

    public AuthorName() {
    }

    public String getName() {
        return Name;
    }

}

class ResponseSubjectMetadata {

    private String Status;
    private String ErrorMessage;
    private final List<SubjectMetadata> Result = new ArrayList<>();

    public ResponseSubjectMetadata() {
    }

    public String getStatus() {
        return Status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public List<SubjectMetadata> getResult() {
        return Result;
    }

    @Override
    public String toString() {
        return "ResponseSubjectMetadata [Status=" + Status + ", ErrorMessage=" + ErrorMessage + ", Result=" + Result + "]";
    }

}

class SubjectMetadata {

    private String SubjectText;
    private String CreationDate;

    private final List<Publication> Publications = new ArrayList<>();

    public SubjectMetadata() {
    }

    public String getSubjectText() {
        return SubjectText;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public List<Publication> getPublications() {
        return Publications;
    }

    @Override
    public String toString() {
        return "SubjectMetadata [SubjectText=" + SubjectText + ", CreationDate=" + CreationDate + ", Publications=" + Publications + "]";
    }

}

class ResponsePublicationSearch {

    private String Status;
    private String ErrorMessage;
    private final List<PublicationSearch> Result = new ArrayList<>();

    public ResponsePublicationSearch() {
    }

    public String getStatus() {
        return Status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public List<PublicationSearch> getResult() {
        return Result;
    }

    @Override
    public String toString() {
        return "ResponsePublicationSearch [Status=" + Status + ", ErrorMessage=" + ErrorMessage + ", Result=" + Result + "]";
    }

}

class PublicationSearch {

    private String BHLType;
    private String FoundIn;
    private String Volume;
    private final List<AuthorName> Authors = new ArrayList<>();
    private String PartUrl;
    private String PartID;
    private String Genre;
    private String Title;
    private String ContainerTitle;
    private String Series;
    private String Date;
    private String PageRange;

    public PublicationSearch() {
    }

    public String getBHLType() {
        return BHLType;
    }

    public String getFoundIn() {
        return FoundIn;
    }

    public String getVolume() {
        return Volume;
    }

    public List<AuthorName> getAuthors() {
        return Authors;
    }

    public String getPartUrl() {
        return PartUrl;
    }

    public String getPartID() {
        return PartID;
    }

    public String getGenre() {
        return Genre;
    }

    public String getTitle() {
        return Title;
    }

    public String getContainerTitle() {
        return ContainerTitle;
    }

    public String getSeries() {
        return Series;
    }

    public String getDate() {
        return Date;
    }

    public String getPageRange() {
        return PageRange;
    }

    @Override
    public String toString() {
        return "PublicationSearch [BHLType=" + BHLType + ", FoundIn=" + FoundIn + ", Volume=" + Volume + ", Authors=" + Authors + ", PartUrl=" + PartUrl + ", PartID=" + PartID + ", Genre=" + Genre + ", Title=" + Title + ", ContainerTitle=" + ContainerTitle + ", Series=" + Series + ", Date=" + Date + ", PageRange=" + PageRange + "]";
    }

}

class ResponseAuthorSearch {

    private String Status;
    private String ErrorMessage;
    private final List<AuthorSearch> Result = new ArrayList<>();

    public ResponseAuthorSearch() {
    }

    public String getStatus() {
        return Status;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public List<AuthorSearch> getResult() {
        return Result;
    }

    @Override
    public String toString() {
        return "ResponseAuthorSearch [Status=" + Status + ", ErrorMessage=" + ErrorMessage + ", Result=" + Result + "]";
    }

}

class AuthorSearch {

    private String AuthorID;
    private String Name;
    private String CreatorUrl;

    public AuthorSearch() {
    }

    public String getAuthorID() {
        return AuthorID;
    }

    public String getName() {
        return Name;
    }

    public String getCreatorUrl() {
        return CreatorUrl;
    }

    @Override
    public String toString() {
        return "AuthorSearch [AuthorID=" + AuthorID + ", Name=" + Name + ", CreatorUrl=" + CreatorUrl + "]";
    }

}
