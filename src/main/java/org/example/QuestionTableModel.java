package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class QuestionTableModel extends AbstractTableModel {
    private String[] columnNames = {"ID", "Nội dung","Loại câu hỏi", "Cấp độ", "File âm thanh"};
    private List<Question> data = new ArrayList<>();

    public void setData(List<Question> questions) {
        this.data = questions;
        fireTableDataChanged(); // Cập nhật lại bảng
    }

    public Question getQuestionAt(int row) {
        return data.get(row);
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Question q = data.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> q.getId();
            case 1 -> q.getContent();
            case 2 -> q.getType();
            case 3 -> q.getLevel();
            case 4 -> q.getAudio_url();
            default -> "";
        };
    }
}
