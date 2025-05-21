package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PDFUtils {

    public static void exportToPdf(String rootFolder, List<Question> questions, int index) throws IOException {
        if (rootFolder == null || rootFolder.isBlank()) {
            JOptionPane.showMessageDialog(null, "❌ Vui lòng chọn thư mục xuất file PDF");
            return;
        }

        // 1. Tạo folder riêng cho mỗi mã đề
        File outputDir = new File(rootFolder, "De_" + index);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 2. Tạo file PDF đề
        File pdfFile = new File(outputDir, "Test.pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            InputStream fontStream = PDFUtils.class.getClassLoader().getResourceAsStream("fonts/NotoSansJP-Regular.ttf");
            if (fontStream == null) {
                throw new IOException("Không tìm thấy font NotoSansJP-Regular.ttf");
            }
            PDType0Font unicodeFont = PDType0Font.load(doc, fontStream);

            content.setFont(unicodeFont, 12);
            content.beginText();
            content.setLeading(14.5f);
            content.newLineAtOffset(50, 750);

            content.setFont(unicodeFont, 14);
            content.showText("ĐỀ KIỂM TRA - MÃ ĐỀ " + index + "Người thực hiện: Trần Đức Long - 102230027 - 23T_Nhat1");
            content.newLine();
            content.setFont(unicodeFont, 12);
            content.newLine();

            int qIndex = 1;
            for (Question q : questions) {
                content.showText(qIndex + ". (" + JulyUtils.TYPE_EN_TO_JP.get(q.getType()) + " - " + q.getLevel() + ")");
                content.newLine();
                content.showText("    " + q.getContent());
                content.newLine();
                content.newLine();
                qIndex++;
            }

            content.endText();
            content.close();
            doc.save(pdfFile);
        }

        // 3. Ghi file đáp án
        File answerFile = new File(outputDir, "AnswerKey.txt");
        try (PrintWriter writer = new PrintWriter(answerFile)) {
            int qIndex = 1;
            for (Question q : questions) {
                writer.println(qIndex + ". " + q.getSuggested_answer());
                qIndex++;
            }
        }

        // 4. Copy file âm thanh (nếu có)
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            if (q.getAudio_url() != null && !q.getAudio_url().isBlank()) {
                File src = new File(q.getAudio_url());
                if (!src.exists()) {
                    JOptionPane.showMessageDialog(null, "❌ Không tìm thấy file âm thanh: " + src.getAbsolutePath());
                    return;
                }
                String extension = src.getName().substring(src.getName().lastIndexOf("."));
                File dest = new File(outputDir, "audio_" + (i + 1) + extension);
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}