package ua.edu.ucu.apps;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CacheDocument {

    private Connection connect() {
        String url = "jdbc:sqlite:sqlite.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    public boolean recordExists(String gcsPath) {
        String sql = "SELECT count(*) FROM your-table WHERE gcs_path = ?";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = connect().prepareStatement(sql)) {
            pstmt.setString(1, gcsPath);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public void injectText(String text, String gcsPath) {
        String sql = "INSERT INTO your_table (gcs_path, yourcolumn) VALUES(?, ?)";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gcsPath);
            pstmt.setString(2, text);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        CacheDocument app = new CacheDocument();
        String gcsPath = "gs://cv-examples/wiki.png";
        if (app.recordExists(gcsPath)) {
            app.injectText("Text", gcsPath);
            System.out.println("Record updated successfully");
        }
        else {
            System.out.println("Record not found");
        }
    }
}
