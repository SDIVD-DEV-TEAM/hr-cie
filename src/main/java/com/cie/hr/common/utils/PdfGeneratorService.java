package com.cie.hr.common.utils;

import com.cie.hr.infrastructure.entity.ScorecardEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;

/**
 * @author Alexis TAMBIE
 * @created 20/09/2024
 * @project hr-cie
 */
@Service
public class PdfGeneratorService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final SpringTemplateEngine templateEngine;

    public PdfGeneratorService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generatePdfFromHtml(ScorecardEntity scorecard) throws IOException {
        // Créer le contexte Thymeleaf avec l'objet CampaignStatistics
        Context context = new Context();
        context.setVariable("campaign", scorecard);

        // Résoudre le template Thymeleaf
        String htmlContent = templateEngine.process("evaluation_sheet", context);

        // Obtenir le chemin relatif du répertoire templates/
        ClassPathResource resource = new ClassPathResource("templates/");
        String baseUrl = resource.getURL().toString();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();

            renderer.setDocumentFromString(htmlContent, baseUrl);

            // Ajouter les polices Montserrat avec le FontResolver
            ITextFontResolver fontResolver = renderer.getFontResolver();

            // Define temporary file directory
            File tempDir = Files.createTempDirectory("fonts").toFile();

            // Load fonts using streams from the classpath and save to temp files
            addFontToResolver(fontResolver, "templates/css/fonts/Montserrat-Regular.ttf", tempDir);
            addFontToResolver(fontResolver, "templates/css/fonts/Montserrat-Bold.ttf", tempDir);
            addFontToResolver(fontResolver, "templates/css/fonts/SourceSans3-Bold.ttf", tempDir);
            addFontToResolver(fontResolver, "templates/css/fonts/SourceSans3-Regular.ttf", tempDir);

            // Générer le PDF
            renderer.layout();
            renderer.createPDF(outputStream);

            // Clean up temporary files (optional, can be done after the PDF generation)
            deleteTempDir(tempDir);

            return outputStream.toByteArray();
        } catch (IOException e) {
            LOGGER.error("Failed to generate PDF", e);
            throw new IOException("Failed to generate PDF", e);
        }
    }

    private void addFontToResolver(ITextFontResolver fontResolver, String fontPath, File tempDir) throws IOException {
        // Load the font as a stream
        try (InputStream fontStream = new ClassPathResource(fontPath).getInputStream()) {
            // Create a temporary file for the font
            File tempFontFile = new File(tempDir, new File(fontPath).getName());
            try (FileOutputStream fos = new FileOutputStream(tempFontFile)) {
                // Write the font stream to the temporary file
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fontStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }
            // Add the temporary font file to the font resolver
            fontResolver.addFont(tempFontFile.getAbsolutePath(), true);
        }
    }

    private void deleteTempDir(File tempDir) {
        if (tempDir != null && tempDir.exists() && tempDir.isDirectory()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.delete()) {
                        LOGGER.warn("Failed to delete file: {}", file.getAbsolutePath());
                    }
                }
            }
            if (!tempDir.delete()) {
                LOGGER.warn("Failed to delete directory: {}", tempDir.getAbsolutePath());
            }
        }
    }
}
