package org.example;

import org.example.DBUtils;
import org.example.Question;

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
                list.add(q);
            }
        }
        return list;
    }

    public void insert(Question q) throws SQLException {
        String sql = "INSERT INTO questions(content, audio_url,type, level) VALUES (?, ?, ?)";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, q.getContent());
            stmt.setString(2, q.getAudio_url());
            stmt.setString(3, q.getType());
            stmt.setString(4, q.getLevel());
            stmt.executeUpdate();
        }
    }

    public void update(Question q) throws SQLException {
        String sql = "UPDATE questions SET content=?, audio_url=?,type=?, level=? WHERE id=?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, q.getContent());
            stmt.setString(2, q.getAudio_url());
            stmt.setString(3, q.getType());
            stmt.setString(4, q.getLevel());
            stmt.setInt(5, q.getId());
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
}
