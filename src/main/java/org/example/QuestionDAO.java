package org.example;

import org.example.DBUtils;
import org.example.Question;

import java.io.File;
import java.sql.*;
import java.util.*;

public class QuestionDAO {
    public List<Question> getAll() throws SQLException {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT * FROM questions";
        try (Connection conn = DBUtils.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setContent(rs.getString("content"));
                q.setType(rs.getString("type"));
                q.setAudio_url(rs.getString("audio_url"));
                q.setLevel(rs.getString("level"));
                q.setSuggested_answer(rs.getString("suggested_answer"));
                list.add(q);
            }
        }
        return list;
    }

    public void insert(Question q) throws SQLException {
        String sql = "INSERT INTO questions(content, audio_url,type, level,suggested_answer) VALUES (?, ?,?, ?,?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, q.getContent());
            stmt.setString(2, q.getAudio_url());
            stmt.setString(3, q.getType());
            stmt.setString(4, q.getLevel());
            stmt.setString(5, q.getSuggested_answer());
            stmt.executeUpdate();
        }
    }

    public void update(Question q) throws SQLException {
        String sql = "UPDATE questions SET content=?, audio_url=?,type=?, level=?, suggested_answer=? WHERE id=?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, q.getContent());
            stmt.setString(2, q.getAudio_url());
            stmt.setString(3, q.getType());
            stmt.setString(4, q.getLevel());
            stmt.setString(5, q.getSuggested_answer());
            stmt.setInt(6, q.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE id=?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public int countQuestions(String type, String level) {
        String sql = "SELECT COUNT(*) FROM questions WHERE type=? AND level=?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            stmt.setString(2, level);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Question> generateTest(Map<String, Integer> criteria) throws SQLException {
        List<Question> result = new ArrayList<>();

        String sql = "SELECT * FROM questions WHERE type=? AND level=? ORDER BY RAND() LIMIT ?";

        try (Connection conn = DBUtils.getConnection()) {
            for (Map.Entry<String, Integer> entry : criteria.entrySet()) {
                String[] parts = entry.getKey().split(":");
                String type = parts[0];
                String level = parts[1];
                int limit = entry.getValue();

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, type);
                    stmt.setString(2, level);
                    stmt.setInt(3, limit);

                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Question q = new Question();
                            q.setId(rs.getInt("id"));
                            q.setContent(rs.getString("content"));
                            q.setType(rs.getString("type"));
                            q.setAudio_url(rs.getString("audio_url"));
                            q.setLevel(rs.getString("level"));
                            q.setSuggested_answer(rs.getString("suggested_answer"));


                            result.add(q);
                        }
                    }
                }
            }
        }

        return result;
    }

}
