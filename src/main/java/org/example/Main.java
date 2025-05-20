package org.example;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

public class Main extends JFrame {
    private static QuestionDAO dao = new QuestionDAO();
    private QuestionTableModel tableModel;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    public Main() {
        setTitle("Quản lý ngân hàng đề thi");
        setSize(900, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Bảng câu hỏi
        tableModel = new QuestionTableModel();
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            Font fontJP = JulyUtils.FONT_JP;
            Font fontVI = JulyUtils.FONT_VI;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 1 || column == 2 || column == 5) {
                    c.setFont(fontJP);
                } else {
                    c.setFont(fontVI);
                }

                return c;
            }
        });

        table.setFont(new Font("Meiryo", Font.PLAIN, 14));
        table.setRowHeight(40);
        refreshData();

        // Double click để sửa
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        Question q = tableModel.getQuestionAt(row);
                        showForm(q);
                    }
                }
            }
        });

        // Nút CRUD
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");

        btnRefresh.addActionListener(e -> refreshData());
        btnAdd.addActionListener(e -> showForm(null));
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Question q = tableModel.getQuestionAt(row);
                showForm(q);
            }
        });
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Xoá câu hỏi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        dao.delete(tableModel.getQuestionAt(row).getId());
                        refreshData();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel panelButtons = new JPanel();
        panelButtons.add(btnRefresh);
        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }

    private void refreshData() {
        try {
            List<Question> list = dao.getAll();
            tableModel.setData(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showForm(Question q) {
        QuestionForm form = new QuestionForm(this, q, this::refreshData);
        form.setVisible(true);
    }
}
