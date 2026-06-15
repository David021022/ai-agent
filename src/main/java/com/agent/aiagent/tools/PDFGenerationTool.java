/*
package com.agent.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.agent.aiagent.constant.FileConstant;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            
            FileUtil.mkdir(fileDir);
            
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                




                
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);

                Paragraph paragraph = new Paragraph(content);
                
                document.add(paragraph);
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}*/
package com.agent.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.agent.aiagent.constant.FileConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PDFGenerationTool {

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {

        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        FileUtil.mkdir(fileDir);

        String safeFileName = sanitizeFileName(fileName);
        if (!safeFileName.toLowerCase().endsWith(".pdf")) {
            safeFileName = safeFileName + ".pdf";
        }

        Path filePath = Paths.get(fileDir, safeFileName);

        try (PdfWriter writer = new PdfWriter(filePath.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            PdfFont font = createFont();
            document.setFont(font);
            document.add(new Paragraph(content == null ? "" : content));

            return "PDF generated successfully to: " + filePath;
        } catch (Exception e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    private String sanitizeFileName(String fileName) {
        String name = fileName == null ? "generated" : fileName.trim();

        if (name.isEmpty()) {
            name = "generated";
        }

        // 去掉 Windows 不允许的字符
        name = name.replaceAll("[\\\\/:*?\"<>|]", "_");

        // 防止文件名过长
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }

        return name;
    }

    private PdfFont createFont() {
        try {
            // 优先尝试中文字体
            String simsunPath = "C:/Windows/Fonts/simsun.ttc";
            return PdfFontFactory.createTtcFont(
                    simsunPath,
                    0,
                    PdfEncodings.IDENTITY_H,
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED,
                    true
            );
        } catch (Exception e) {
            try {
                // 再尝试微软雅黑
                String msyhPath = "C:/Windows/Fonts/msyh.ttc";
                return PdfFontFactory.createTtcFont(
                        msyhPath,
                        0,
                        PdfEncodings.IDENTITY_H,
                        PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED,
                        true
                );
            } catch (Exception ex) {
                try {
                    // 最后回退到默认字体
                    return PdfFontFactory.createFont();
                } catch (Exception ignore) {
                    throw new RuntimeException("Unable to create PDF font");
                }
            }
        }
    }
}