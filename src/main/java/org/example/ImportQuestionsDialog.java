// src/main/java/org/example/ImportQuestionsDialog.java
package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ImportQuestionsDialog extends JDialog {
    private QuestionDAO dao = new QuestionDAO();
    private JTable table;
    private ImportQuestionsTableModel tableModel;
    private JButton btnAcceptAll, btnClose;

    public ImportQuestionsDialog(JDialog parent, List<Question> questions) {
        super(parent, "Xác nhận nhập câu hỏi", true);
        setSize(700, 400);
        setLocationRelativeTo(parent);

        tableModel = new ImportQuestionsTableModel(questions);
        table = new JTable(tableModel);
        table.setRowHeight(40);

        JButton btnEdit = new JButton("Sửa");
        btnEdit.addActionListener(e -> editSelected());

        JButton btnAccept = new JButton("Chấp nhận");
        btnAccept.addActionListener(e -> acceptSelected());

        btnAcceptAll = new JButton("Chấp nhận tất cả");
        btnAcceptAll.addActionListener(e -> acceptAll());

        btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());

        JPanel panelButtons = new JPanel();
        panelButtons.add(btnEdit);
        panelButtons.add(btnAccept);
        panelButtons.add(btnAcceptAll);
        panelButtons.add(btnClose);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            Question q = tableModel.getQuestionAt(row);
            QuestionForm form = new QuestionForm((JFrame) getParent(), q, () -> tableModel.fireTableRowsUpdated(row, row));
            form.setVisible(true);
        }
    }

    private void acceptSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            try {
                dao.insert(tableModel.getQuestionAt(row));
                tableModel.removeQuestionAt(row);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu: " + ex.getMessage());
            }
        }
    }

    private void acceptAll() {
        try {
            for (Question q : tableModel.getQuestions()) {
                dao.insert(q);
            }
            tableModel.clear();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu: " + ex.getMessage());
        }
    }
}