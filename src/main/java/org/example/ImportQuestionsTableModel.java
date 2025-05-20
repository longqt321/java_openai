// src/main/java/org/example/ImportQuestionsTableModel.java
package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.List;
public class ImportQuestionsTableModel extends AbstractTableModel {
    private final String[] columns = {"Nội dung", "Loại câu hỏi", "Cấp độ", "File âm thanh", "Câu trả lời đề xuất"};
    private List<Question> questions;

    public ImportQuestionsTableModel(List<Question> questions) {
        this.questions = questions;
    }

    public Question getQuestionAt(int row) {
        return questions.get(row);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void removeQuestionAt(int row) {
        questions.remove(row);
        fireTableRowsDeleted(row, row);
    }

    public void clear() {
        questions.clear();
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return questions.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int col) {
        return columns[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        Question q = questions.get(row);
        return switch (col) {
            case 0 -> q.getContent();
            case 1 -> q.getType();
            case 2 -> q.getLevel();
            case 3 -> q.getAudio_url();
            case 4 -> q.getSuggested_answer();
            default -> "";
        };
    }
}
