package org.jabref.logic.exporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jabref.logic.bibtex.BibEntryWriter;
import org.jabref.logic.bibtex.FieldWriter;
import org.jabref.logic.bibtex.FieldWriterPreferences;
import org.jabref.logic.l10n.Localization;
import org.jabref.logic.util.OS;
import org.jabref.logic.util.StandardFileType;
import org.jabref.logic.util.io.FileUtil;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDEmbeddedFilesNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;
import org.apache.pdfbox.pdmodel.common.filespecification.PDEmbeddedFile;

/**
 * A custom exporter to write bib entries to an embedded bib file.
 */
public class EmbeddedBibFilePdfExporter extends Exporter {

    public static String EMBEDDED_FILE_NAME = "main.bib";

    private final BibDatabaseMode bibDatabaseMode;
    private final BibEntryTypesManager bibEntryTypesManager;
    private final FieldWriterPreferences fieldWriterPreferences;

    public EmbeddedBibFilePdfExporter(BibDatabaseMode bibDatabaseMode, BibEntryTypesManager bibEntryTypesManager, FieldWriterPreferences fieldWriterPreferences) {
        super("bib", "Embedded BibTeX", StandardFileType.PDF);
        this.bibDatabaseMode = bibDatabaseMode;
        this.bibEntryTypesManager = bibEntryTypesManager;
        this.fieldWriterPreferences = fieldWriterPreferences;
    }

    /**
     * @param databaseContext the database to export from
     * @param file            the file to write to. If it contains "split", then the output is split into different files
     * @param entries         a list containing all entries that should be exported
     */
    @Override
    public void export(BibDatabaseContext databaseContext, Path file, List<BibEntry> entries) throws Exception {
        Objects.requireNonNull(databaseContext);
        Objects.requireNonNull(file);
        Objects.requireNonNull(entries);

        String bibString = getBibString(entries);
        embedBibTex(bibString, file);
    }

    private void embedBibTex(String bibTeX, Path file) throws IOException {
        if (!Files.exists(file) || !FileUtil.isPDFFile(file)) {
            return;
        }
        try (PDDocument document = Loader.loadPDF(file.toFile())) {
            PDDocumentNameDictionary nameDictionary = document.getDocumentCatalog().getNames();
            PDEmbeddedFilesNameTreeNode efTree;
            Map<String, PDComplexFileSpecification> names;

            if (nameDictionary == null) {
                efTree = new PDEmbeddedFilesNameTreeNode();
                names = new HashMap<>();
                nameDictionary = new PDDocumentNameDictionary(document.getDocumentCatalog());
                nameDictionary.setEmbeddedFiles(efTree);
                document.getDocumentCatalog().setNames(nameDictionary);
            } else {
                efTree = nameDictionary.getEmbeddedFiles();
                if (efTree == null) {
                    efTree = new PDEmbeddedFilesNameTreeNode();
                    nameDictionary.setEmbeddedFiles(efTree);
                }
                names = efTree.getNames();
                if (names == null) {
                    names = new HashMap<>();
                    efTree.setNames(names);
                }
            }

            PDComplexFileSpecification fileSpecification;
            if (names.containsKey(EMBEDDED_FILE_NAME)) {
                fileSpecification = names.get(EMBEDDED_FILE_NAME);
            } else {
                fileSpecification = new PDComplexFileSpecification();
            }
            if (efTree != null) {
                InputStream inputStream = new ByteArrayInputStream(bibTeX.getBytes(StandardCharsets.UTF_8));
                fileSpecification.setFile(EMBEDDED_FILE_NAME);
                PDEmbeddedFile embeddedFile = new PDEmbeddedFile(document, inputStream);
                embeddedFile.setSubtype("text/x-bibtex");
                embeddedFile.setSize(bibTeX.length());
                fileSpecification.setEmbeddedFile(embeddedFile);

                if (!names.containsKey(EMBEDDED_FILE_NAME)) {
                    try {
                        names.put(EMBEDDED_FILE_NAME, fileSpecification);
                    } catch (UnsupportedOperationException e) {
                        throw new IOException(Localization.lang("File '%0' is write protected.", file.toString()));
                    }
                }

                efTree.setNames(names);
                nameDictionary.setEmbeddedFiles(efTree);
                document.getDocumentCatalog().setNames(nameDictionary);
            }
            document.save(file.toFile());
        }
    }

    private String getBibString(List<BibEntry> entries) throws IOException {
        StringWriter stringWriter = new StringWriter();
        BibWriter bibWriter = new BibWriter(stringWriter, OS.NEWLINE);
        FieldWriter fieldWriter = FieldWriter.buildIgnoreHashes(fieldWriterPreferences);
        BibEntryWriter bibEntryWriter = new BibEntryWriter(fieldWriter, bibEntryTypesManager);
        for (BibEntry entry : entries) {
            bibEntryWriter.write(entry, bibWriter, bibDatabaseMode);
        }
        return stringWriter.toString();
    }
}
