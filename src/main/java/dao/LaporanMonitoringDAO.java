package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.LaporanMonitoring;

/**
 * Data Access Object untuk operasi database pada tabel laporan_monitoring
 */
public class LaporanMonitoringDAO extends HomeDAO {
    
    /**
     * Mengambil semua laporan monitoring dari database
     * @return List of LaporanMonitoring objects
     */
    public List<LaporanMonitoring> getAllLaporan() {
        String query = """
            SELECT lm.*, a.nama as nama_anak 
            FROM laporan_monitoring lm
            LEFT JOIN anak a ON lm.id_anak = a.id
            ORDER BY lm.created_at DESC
            """;
        List<LaporanMonitoring> laporanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LaporanMonitoring laporan = mapResultSetToLaporan(rs);
                laporanList.add(laporan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return laporanList;
    }
    
    /**
     * Mengambil laporan berdasarkan ID user (relawan)
     * @param idUser ID user/relawan
     * @return List of LaporanMonitoring objects
     */
    public List<LaporanMonitoring> getLaporanByUser(int idUser) {
        String query = """
            SELECT lm.*, a.nama as nama_anak 
            FROM laporan_monitoring lm
            LEFT JOIN anak a ON lm.id_anak = a.id
            WHERE lm.id_user = ?
            ORDER BY lm.created_at DESC
            """;
        List<LaporanMonitoring> laporanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LaporanMonitoring laporan = mapResultSetToLaporan(rs);
                    laporanList.add(laporan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return laporanList;
    }
    
    /**
     * Mengambil laporan berdasarkan ID anak
     * @param idAnak ID anak
     * @return List of LaporanMonitoring objects
     */
    public List<LaporanMonitoring> getLaporanByAnak(int idAnak) {
        String query = """
            SELECT lm.*, a.nama as nama_anak 
            FROM laporan_monitoring lm
            LEFT JOIN anak a ON lm.id_anak = a.id
            WHERE lm.id_anak = ?
            ORDER BY lm.tanggal_monitoring DESC
            """;
        List<LaporanMonitoring> laporanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idAnak);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LaporanMonitoring laporan = mapResultSetToLaporan(rs);
                    laporanList.add(laporan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return laporanList;
    }
    
    /**
     * Mengambil laporan berdasarkan ID
     * @param id ID laporan
     * @return LaporanMonitoring object jika ditemukan, null jika tidak ditemukan
     */
    public LaporanMonitoring getLaporanById(int id) {
        String query = """
            SELECT lm.*, a.nama as nama_anak 
            FROM laporan_monitoring lm
            LEFT JOIN anak a ON lm.id_anak = a.id
            WHERE lm.id = ?
            """;
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLaporan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Menyimpan laporan monitoring baru ke database
     * @param laporan LaporanMonitoring object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(LaporanMonitoring laporan) {
        String query = "INSERT INTO laporan_monitoring (id_anak, id_user, nama, status, tanggal_monitoring, progress_pendidikan, kondisi_kesehatan, catatan, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, laporan.getIdAnak());
            stmt.setInt(2, laporan.getIdUser());
            stmt.setString(3, laporan.getNama());
            stmt.setString(4, laporan.getStatus() != null ? laporan.getStatus() : "Draft");
            
            if (laporan.getTanggalMonitoring() != null) {
                stmt.setDate(5, java.sql.Date.valueOf(laporan.getTanggalMonitoring()));
            } else {
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            }
            
            stmt.setString(6, laporan.getProgressPendidikan());
            stmt.setString(7, laporan.getKondisiKesehatan());
            stmt.setString(8, laporan.getCatatan());
            stmt.setString(9, laporan.getFoto());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan laporan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update laporan monitoring
     * @param laporan LaporanMonitoring object yang akan diupdate
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(LaporanMonitoring laporan) {
        // Jika status Dikembalikan diubah menjadi Draft, reset catatan revisi
        String query = "UPDATE laporan_monitoring SET nama = ?, status = ?, catatan_revisi = ?, tanggal_monitoring = ?, progress_pendidikan = ?, kondisi_kesehatan = ?, catatan = ?, foto = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, laporan.getNama());
            
            // Set status (jika dari Dikembalikan menjadi Draft, reset catatan_revisi)
            String newStatus = laporan.getStatus() != null ? laporan.getStatus() : "Draft";
            stmt.setString(2, newStatus);
            
            // Reset catatan revisi jika status berubah menjadi Draft
            if (newStatus.equals("Draft")) {
                stmt.setString(3, null);
            } else {
                stmt.setString(3, laporan.getCatatanRevisi());
            }
            
            if (laporan.getTanggalMonitoring() != null) {
                stmt.setDate(4, java.sql.Date.valueOf(laporan.getTanggalMonitoring()));
            } else {
                stmt.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
            }
            
            stmt.setString(5, laporan.getProgressPendidikan());
            stmt.setString(6, laporan.getKondisiKesehatan());
            stmt.setString(7, laporan.getCatatan());
            stmt.setString(8, laporan.getFoto());
            stmt.setInt(9, laporan.getId());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update laporan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update status laporan (untuk admin approve/reject)
     * @param id ID laporan
     * @param status Status baru (Disetujui atau Dikembalikan)
     * @param catatanRevisi Catatan revisi (opsional, hanya untuk status Dikembalikan)
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateStatus(int id, String status, String catatanRevisi) {
        String query = "UPDATE laporan_monitoring SET status = ?, catatan_revisi = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setString(2, catatanRevisi);
            stmt.setInt(3, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update status laporan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus laporan monitoring dari database
     * @param id ID laporan yang akan dihapus
     * @return true jika berhasil, false jika gagal
     */
    public boolean delete(int id) {
        String query = "DELETE FROM laporan_monitoring WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menghapus laporan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengambil jumlah laporan yang dibuat oleh user tertentu
     * @param idUser ID user/relawan
     * @return Jumlah laporan
     */
    public int getJumlahLaporanByUser(int idUser) {
        String query = "SELECT COUNT(*) as jumlah FROM laporan_monitoring WHERE id_user = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil jumlah laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Helper method untuk mapping ResultSet ke LaporanMonitoring object
     */
    private LaporanMonitoring mapResultSetToLaporan(ResultSet rs) throws SQLException {
        LaporanMonitoring laporan = new LaporanMonitoring();
        laporan.setId(rs.getInt("id"));
        laporan.setIdAnak(rs.getInt("id_anak"));
        laporan.setIdUser(rs.getInt("id_user"));
        
        // Ambil nama dan status (bisa null jika kolom belum ada)
        try {
            laporan.setNama(rs.getString("nama"));
        } catch (SQLException e) {
            // Kolom nama mungkin tidak ada jika belum diupdate
        }
        
        try {
            laporan.setStatus(rs.getString("status"));
        } catch (SQLException e) {
            // Kolom status mungkin tidak ada jika belum diupdate, default ke Draft
            laporan.setStatus("Draft");
        }
        
        try {
            laporan.setCatatanRevisi(rs.getString("catatan_revisi"));
        } catch (SQLException e) {
            // Kolom catatan_revisi mungkin tidak ada jika belum diupdate
        }
        
        java.sql.Date date = rs.getDate("tanggal_monitoring");
        if (date != null) {
            laporan.setTanggalMonitoring(date.toLocalDate());
        }
        
        laporan.setProgressPendidikan(rs.getString("progress_pendidikan"));
        laporan.setKondisiKesehatan(rs.getString("kondisi_kesehatan"));
        laporan.setCatatan(rs.getString("catatan"));
        laporan.setFoto(rs.getString("foto"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            laporan.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        timestamp = rs.getTimestamp("updated_at");
        if (timestamp != null) {
            laporan.setUpdatedAt(timestamp.toLocalDateTime());
        }
        
        // Ambil nama anak jika ada join
        try {
            laporan.setNamaAnak(rs.getString("nama_anak"));
        } catch (SQLException e) {
            // Kolom nama_anak mungkin tidak ada jika tidak join
        }
        
        return laporan;
    }
}

