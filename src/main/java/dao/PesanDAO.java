package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Pesan;

/**
 * Data Access Object untuk operasi database pada tabel pesan
 */
public class PesanDAO extends HomeDAO {
    
    /**
     * Menyimpan pesan baru ke database
     * @param pesan Pesan object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(Pesan pesan) {
        String query = "INSERT INTO pesan (nama, email, subjek, pesan, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, pesan.getNama());
            stmt.setString(2, pesan.getEmail());
            stmt.setString(3, pesan.getSubjek());
            stmt.setString(4, pesan.getPesan());
            stmt.setString(5, pesan.getStatus() != null ? pesan.getStatus() : "baru");
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan pesan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengambil semua pesan dari database
     * @return List of Pesan objects
     */
    public List<Pesan> getAllPesan() {
        String query = "SELECT * FROM pesan ORDER BY created_at DESC";
        List<Pesan> pesanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pesan pesan = mapResultSetToPesan(rs);
                pesanList.add(pesan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data pesan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pesanList;
    }
    
    /**
     * Mengambil pesan berdasarkan ID
     * @param id ID pesan
     * @return Pesan object atau null jika tidak ditemukan
     */
    public Pesan getPesanById(int id) {
        String query = "SELECT * FROM pesan WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPesan(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil pesan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mengambil pesan berdasarkan status
     * @param status Status pesan (baru, dibaca, dijawab)
     * @return List of Pesan objects
     */
    public List<Pesan> getPesanByStatus(String status) {
        String query = "SELECT * FROM pesan WHERE status = ? ORDER BY created_at DESC";
        List<Pesan> pesanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Pesan pesan = mapResultSetToPesan(rs);
                pesanList.add(pesan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil pesan berdasarkan status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return pesanList;
    }
    
    /**
     * Update status pesan
     * @param id ID pesan
     * @param status Status baru (baru, dibaca, dijawab)
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateStatus(int id, String status) {
        String query = "UPDATE pesan SET status = ?, updated_at = NOW() WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update status pesan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghitung jumlah pesan dengan status tertentu
     * @param status Status pesan
     * @return Jumlah pesan
     */
    public int countByStatus(String status) {
        String query = "SELECT COUNT(*) FROM pesan WHERE status = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error menghitung pesan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Menghitung jumlah pesan baru (belum dibaca)
     * @return Jumlah pesan baru
     */
    public int countPesanBaru() {
        return countByStatus("baru");
    }
    
    /**
     * Map ResultSet ke Pesan object
     * @param rs ResultSet
     * @return Pesan object
     * @throws SQLException jika terjadi error
     */
    private Pesan mapResultSetToPesan(ResultSet rs) throws SQLException {
        Pesan pesan = new Pesan();
        pesan.setId(rs.getInt("id"));
        pesan.setNama(rs.getString("nama"));
        pesan.setEmail(rs.getString("email"));
        pesan.setSubjek(rs.getString("subjek"));
        pesan.setPesan(rs.getString("pesan"));
        pesan.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            pesan.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            pesan.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return pesan;
    }
}

