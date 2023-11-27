package ua.edu.ucu.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CacheDocument {
    private Document doc;

    public CacheDocument(Document document) {
        this.doc = document;
    }
    private Connection connect() {
        String url = "jdbc:sqlite:sqlite2.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return conn;
    }
    private void handleSQLException(SQLException e) {
        System.err.println("SQL Exception: " + e.getMessage());
    }
    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS documents ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "path TEXT NOT NULL,"
                + "document TEXT NOT NULL);";

        try (Connection conn = this.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
            System.out.println("Database created");
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    public boolean recordExists(String gcsPath) {
        String sql = "SELECT count(*) FROM documents WHERE path = ?";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setString(1, gcsPath);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            handleSQLException(e);
        }
        return false;
    }
    public void injectText(String text, String gcsPath) {
        String sql = "INSERT INTO documents (path, document) VALUES(?, ?)";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gcsPath);
            pstmt.setString(2, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            handleSQLException(e);
        }
    }
    public static void main(String[] args) {
        String gcsPath = "gs://cv-examples/wiki.png";
        CacheDocument app = new CacheDocument(new SmartDocument(gcsPath));
        app.connect();
        app.createTable();
        if (app.recordExists(gcsPath)) {
            app.injectText("text1", gcsPath);
            System.out.println("Record updated successfully");
        }
        else {
            System.out.println("Record not found");
        }
    }
}