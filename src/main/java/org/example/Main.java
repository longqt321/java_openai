package org.example;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;

import com.fasterxml.jackson.databind.ObjectMapper;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main extends JFrame{
    private static QuestionDAO dao = new QuestionDAO();
    private QuestionTableModel tableModel;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    public Main() {
        setTitle("Quản lý ngân hàng đề thi");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Bảng câu hỏi
        tableModel = new QuestionTableModel();
        table = new JTable(tableModel){
            @Override
            public String getToolTipText(MouseEvent event) {
                int row = rowAtPoint(event.getPoint());
                int col = columnAtPoint(event.getPoint());
                String tooltipContent = getValueAt(row, col).toString();
                if (col == 0) {
                    return "ID: " + tooltipContent;
                } else if (col == 1) {
                    return "<html><body style='width: 400px; font-family: Meiryo; font-size: 14px;'>"
                            + tooltipContent
                            + "</body></html>";
                }
                else if(col == 2){
                    return "Loại câu hỏi: " + tooltipContent;
                }
                else if (col == 3) {
                    return "Cấp độ:" + tooltipContent;
                } else if (col == 4) {
                    return "Audio URL: " + tooltipContent;
                }
                return null;
            }
        };
        table.setFont(new Font("Meiryo", Font.PLAIN, 14));
        table.setRowHeight(40);
        refreshData();

        // Double click to edit
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
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");

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