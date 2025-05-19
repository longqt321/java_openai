// src/main/java/org/example/ImportQuestionsTableModel.java
package org.example;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ImportQuestionsTableModel extends AbstractTableModel {
    private final String[] columns = {"Nội dung", "Loại", "Trình độ", "Audio"};
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
        switch (col) {
            case 0: return q.getContent();
            case 1: return q.getType();
            case 2: return q.getLevel();
            case 3: return q.getAudio_url();
            default: return "";
        }
    }
}