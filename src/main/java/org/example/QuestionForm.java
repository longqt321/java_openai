package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class QuestionForm extends JDialog {
    private JTextArea txtContent,txtSuggestedAnswer;
    private JComboBox<String> cbLevel,cbType;
    private JTextField txtAudioUrl;
    private JButton btnSave, btnCancel,btnChooseAudio,btnImportImage;

    private Question question;
    private QuestionDAO dao = new QuestionDAO();
    private Runnable onSaveCallback;



    private static final Font FONT_JP = new Font("Meiryo", Font.PLAIN, 16);

    public QuestionForm(JFrame parent, Question q, Runnable onSaveCallback) {
        super(parent, "Nhập câu hỏi", true);
        this.question = q;
        this.onSaveCallback = onSaveCallback;

        setSize(600, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // ==== Tạo form chính ====
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // ==== Nội dung câu hỏi ====
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nội dung câu hỏi:"), gbc);


        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;

        txtContent = new JTextArea(10, 50);
        txtContent.setFont(FONT_JP);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);

        JScrollPane contentScroll = new JScrollPane(txtContent);
        formPanel.add(contentScroll, gbc);
        gbc.weighty = 0;


        // ==== Trình độ ====
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Trình độ (N5–N1):"), gbc);

        cbLevel = new JComboBox<>(new String[]{"N5", "N4", "N3", "N2", "N1"});
        gbc.gridx = 1;
        formPanel.add(cbLevel, gbc);

        // ==== Loại câu hỏi ====
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Loại câu hỏi:"), gbc);

        cbType = new JComboBox<>(new String[]{"漢字", "文法", "語彙"});
        gbc.gridx = 1;
        formPanel.add(cbType, gbc);

        // ==== Câu trả lời đề xuất ====
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Câu trả lời đề xuất:"), gbc);
        txtSuggestedAnswer = new JTextArea(2, 40);
        txtSuggestedAnswer.setFont(FONT_JP);
        gbc.gridx = 1;
        formPanel.add(txtSuggestedAnswer, gbc);



        // ==== Audio URL ====
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Chọn file audio:"), gbc);

        JPanel audioPanel = new JPanel(new BorderLayout(5, 0));
        txtAudioUrl = new JTextField();
        txtAudioUrl.setEditable(false); // Không cho nhập thủ công

        btnChooseAudio = new JButton("Chọn...");
        btnChooseAudio.addActionListener(e -> chooseAudioFile());

        audioPanel.add(txtAudioUrl, BorderLayout.CENTER);
        audioPanel.add(btnChooseAudio, BorderLayout.EAST);

        gbc.gridx = 1;
        formPanel.add(audioPanel, gbc);


        // ==== Nút lưu & huỷ ====
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Huỷ");
        btnImportImage = new JButton("Nhập từ ảnh");
        btnImportImage.addActionListener(e -> importFromImage());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnImportImage);


        // ==== Gắn vào layout ====
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // ==== Đổ dữ liệu nếu sửa ====
        if (question != null) {
            txtContent.setText(question.getContent());
            cbLevel.setSelectedItem(question.getLevel());
            txtAudioUrl.setText(question.getAudio_url());
            cbType.setSelectedItem(JulyUtils.TYPE_EN_TO_JP.get(question.getType()));
            txtSuggestedAnswer.setText(question.getSuggested_answer());

            btnImportImage.setEnabled(false); // Không cho nhập từ ảnh khi sửa

        }

        // ==== Đặt con trỏ vào vùng nội dung khi mở form ====
        SwingUtilities.invokeLater(() -> txtContent.requestFocusInWindow());

        // ==== Sự kiện ====
        btnSave.addActionListener(e -> saveQuestion());
        btnCancel.addActionListener(e -> dispose());
    }
    // Dummy parser: replace with actual parsing logic
    private List<Question> parseQuestions(String raw) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(raw, new TypeReference<List<Question>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void importFromImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh chứa câu hỏi");
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            btnImportImage.setEnabled(false);

            final JDialog loadingDialog = new JDialog(this, "Đang xử lý...");
            JLabel lbl = new JLabel("Đang xử lý ảnh và sinh câu hỏi, vui lòng chờ...");
            lbl.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            JLabel timerLabel = new JLabel("Đã chờ: 0 giây");

            loadingDialog.add(lbl);
            loadingDialog.add(timerLabel, BorderLayout.SOUTH);
            loadingDialog.pack();
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);



            loadingDialog.setVisible(true);
            SwingWorker<List<Question>, Void> worker = new SwingWorker<>() {
                final int[] seconds = {0};
                Timer timer = new Timer(1000, e -> {
                    seconds[0]++;
                    timerLabel.setText("Đã chờ: " + seconds[0] + " giây");
                });
                @Override
                protected List<Question> doInBackground() throws Exception {
                    timer.start();
                    String raw = AIUtils.extractQuestions(selected.getAbsolutePath());
                    return parseQuestions(raw);
                }

                @Override
                protected void done() {
                    timer.stop();
                    loadingDialog.dispose();
                    btnImportImage.setEnabled(true);
                    try {
                        List<Question> questions = get();
                        if (!questions.isEmpty()) {
                            JOptionPane.showMessageDialog(QuestionForm.this, "Đã sinh ra danh sách câu hỏi từ ảnh.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            ImportQuestionsDialog dialog = new ImportQuestionsDialog(QuestionForm.this, questions);
                            dialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(QuestionForm.this, "Không tìm thấy câu hỏi nào.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(QuestionForm.this, "Lỗi khi nhập từ ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    private void chooseAudioFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file âm thanh");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Chỉ cho phép chọn file có đuôi .mp3, .wav, .ogg, v.v.
        FileNameExtensionFilter audioFilter = new FileNameExtensionFilter(
                "File âm thanh (.mp3, .wav, .ogg)", "mp3", "wav", "ogg", "flac", "aac", "m4a"
        );
        chooser.setFileFilter(audioFilter);
        chooser.setAcceptAllFileFilterUsed(false); // Ẩn tuỳ chọn "All files"

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selected = chooser.getSelectedFile();
            txtAudioUrl.setText(selected.getAbsolutePath());
        }
    }

    private void saveQuestion() {
        try {
            if (question == null) question = new Question();
            if (txtContent.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nội dung câu hỏi không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }


            question.setContent(txtContent.getText().trim());
            question.setLevel((String) cbLevel.getSelectedItem());
            question.setType(JulyUtils.TYPE_JP_TO_EN.get(cbType.getSelectedItem()));
            question.setAudio_url(txtAudioUrl.getText().trim());
            question.setSuggested_answer(txtSuggestedAnswer.getText().trim());

            if (question.getId() == 0) {
                dao.insert(question);
            } else {
                dao.update(question);
            }

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
