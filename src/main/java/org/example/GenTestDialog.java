package org.example;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenTestDialog extends JDialog {
    private final JPanel contentPanel = new JPanel();
    private final JButton btnAdd = new JButton("➕ Thêm dòng");
    private final JButton btnChooseFolder = new JButton("📁 Chọn thư mục lưu");
    private final JButton btnGenerate = new JButton("🚀 Tạo đề thi");
    private final JLabel lblFolder = new JLabel("Chưa chọn");
    private File selectedFolder;
    private final QuestionDAO dao;

    public GenTestDialog(JFrame parent, QuestionDAO dao) {
        super(parent, "Tạo đề thi", true);
        this.dao = dao;
        initUI();
        setupLayout();
        addCriteriaRow();
    }

    private void initUI() {
        setSize(650, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // Top panel with add button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(btnAdd);

        // Configure main panels
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Giảm padding 2 bên
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Bottom controls
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.add(createFolderPanel());
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(createGeneratePanel());

        // Add components to main dialog
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Event handlers
        btnAdd.addActionListener(e -> addCriteriaRow());
        btnChooseFolder.addActionListener(e -> selectFolder());
        btnGenerate.addActionListener(e -> generate());
    }

    private void setupLayout() {
        lblFolder.setPreferredSize(new Dimension(400, 25));
        btnGenerate.setPreferredSize(new Dimension(120, 30));
    }

    private JPanel createFolderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Thư mục lưu đề thi"),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panel.add(btnChooseFolder, BorderLayout.WEST);
        panel.add(lblFolder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGeneratePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
        panel.add(btnGenerate);
        return panel;
    }

    private void addCriteriaRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        row.setMaximumSize(new Dimension(600, 40));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)));


        JPanel controlsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 2, 0, 2);

        // Loại
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        controlsPanel.add(new JLabel("Loại:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> cbType = createComboBox(new String[]{"漢字", "文法", "語彙"});
        controlsPanel.add(cbType, gbc);

        // Cấp độ
        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.EAST;
        controlsPanel.add(new JLabel("Cấp độ:"), gbc);

        gbc.gridx = 3;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> cbLevel = createComboBox(new String[]{"N5", "N4", "N3", "N2", "N1"});
        controlsPanel.add(cbLevel, gbc);

        // Số lượng
        gbc.gridx = 4;
        gbc.anchor = GridBagConstraints.EAST;
        controlsPanel.add(new JLabel("Số lượng:"), gbc);

        gbc.gridx = 5;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField tfNum = createNumberField();
        controlsPanel.add(tfNum, gbc);

        gbc.gridx = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 0);
        JButton btnDelete = new JButton("❌");
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Dialog", Font.PLAIN, 10));
        btnDelete.setPreferredSize(new Dimension(35, 25));
        btnDelete.setMargin(new Insets(0, 0, 0, 0));
        btnDelete.addActionListener(e -> {
            // Chỉ xóa nếu có nhiều hơn 1 dòng
            if (contentPanel.getComponentCount() > 1) {
                contentPanel.remove(row);
                contentPanel.revalidate();
                contentPanel.repaint();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Cần có ít nhất một dòng tiêu chí",
                        "Không thể xóa",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        controlsPanel.add(btnDelete, gbc);


        row.add(controlsPanel);
        contentPanel.add(row);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JComboBox<String> createComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(80, 25));
        return comboBox;
    }

    private JTextField createNumberField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(60, 25));
        field.setHorizontalAlignment(SwingConstants.RIGHT);
        return field;
    }

    private void selectFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFolder = chooser.getSelectedFile();
            lblFolder.setText("📂 " + selectedFolder.getAbsolutePath());
        }
    }

    private void generate() {
        if (selectedFolder == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thư mục để lưu đề thi.");
            return;
        }

        Map<String, Integer> criteria = new LinkedHashMap<>();

        for (Component c : contentPanel.getComponents()) {
            if (c instanceof JPanel rowPanel) {
                // The first (and only) child is controlsPanel
                JPanel controlsPanel = (JPanel) rowPanel.getComponent(0);

                JComboBox<String> cbType = (JComboBox<String>) controlsPanel.getComponent(1);
                JComboBox<String> cbLevel = (JComboBox<String>) controlsPanel.getComponent(3);
                JTextField tfNum = (JTextField) controlsPanel.getComponent(5);

                String type = cbType.getSelectedItem().toString();
                String level = cbLevel.getSelectedItem().toString();
                int num;

                try {
                    num = Integer.parseInt(tfNum.getText().trim());
                    if (num <= 0) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Số lượng phải là số nguyên dương.");
                    return;
                }

                criteria.put(JulyUtils.TYPE_JP_TO_EN.get(type) + ":" + level, num);
            }
        }

        try {
            List<Question> allQuestions = dao.generateTest(criteria);
            if (allQuestions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có câu hỏi nào phù hợp với tiêu chí đã chọn.");
                return;
            }

            String timestamp = new java.text.SimpleDateFormat("HH_mm_dd_MM_yyyy").format(new java.util.Date());
            File outputDir = new File(selectedFolder, timestamp + "_test");

            if (!outputDir.exists()) {
                outputDir.mkdirs(); // tạo folder con
            }

            // 1. Ghi PDF (không có suggested_answer)
            File pdfFile = new File(outputDir, "Test.pdf");
            try (PDDocument doc = new PDDocument()) {
                PDPage page = new PDPage(PDRectangle.A4);
                doc.addPage(page);
                PDPageContentStream content = new PDPageContentStream(doc, page);
                InputStream fontStream = getClass().getClassLoader().getResourceAsStream("fonts/NotoSansJP-Regular.ttf");
                PDType0Font unicodeFont = PDType0Font.load(doc, fontStream);

                content.setFont(unicodeFont, 12);
                content.beginText();
                content.setLeading(14.5f);
                content.newLineAtOffset(50, 750);

                int index = 1;
                for (Question q : allQuestions) {
                    content.showText(index + ". (" + q.getType() + " - " + q.getLevel() + ")");
                    content.newLine();
                    content.showText("    " + q.getContent());
                    content.newLine();
                    content.newLine();
                    index++;
                }

                content.endText();
                content.close();
                doc.save(pdfFile);
            }

            // 2. Ghi file đáp án
            File answerFile = new File(outputDir, "AnswerKey.txt");
            try (PrintWriter writer = new PrintWriter(answerFile)) {
                int index = 1;
                for (Question q : allQuestions) {
                    writer.println(index + ". " + q.getSuggested_answer());
                    index++;
                }
            }

            // 3. Copy audio
            for (int i = 0; i < allQuestions.size(); i++) {
                Question q = allQuestions.get(i);
                if (q.getAudio_url() != null && !q.getAudio_url().isBlank()) {
                    File src = new File(q.getAudio_url());
                    if (!src.exists()) {
                        JOptionPane.showMessageDialog(this, "❌ Không tìm thấy file âm thanh: " + src.getAbsolutePath());
                        return;
                    }
                    String extension = src.getName().substring(src.getName().lastIndexOf("."));
                    File dest = new File(outputDir, "audio_" + (i + 1) + extension);
                    Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            }


            JOptionPane.showMessageDialog(this, "✅ Đề thi đã được tạo thành công!");
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra khi tạo đề thi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
