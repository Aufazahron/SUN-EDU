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
import model.LaporanProgram;

/**
 * Data Access Object untuk operasi database pada tabel laporan_program
 */
public class LaporanProgramDAO extends HomeDAO {
    
    /**
     * Mengambil laporan program berdasarkan ID program
     * @param idProgram ID program
     * @return LaporanProgram object jika ditemukan, null jika tidak ditemukan
     */
    public LaporanProgram getLaporanByProgramId(int idProgram) {
        String query = "SELECT * FROM laporan_program WHERE id_program = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idProgram);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLaporanProgram(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil laporan program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mengambil semua laporan program
     * @return List of LaporanProgram objects
     */
    public List<LaporanProgram> getAllLaporan() {
        String query = "SELECT * FROM laporan_program ORDER BY created_at DESC";
        List<LaporanProgram> laporanList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LaporanProgram laporan = mapResultSetToLaporanProgram(rs);
                laporanList.add(laporan);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data laporan program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return laporanList;
    }
    
    /**
     * Menyimpan laporan program baru ke database
     * @param laporan LaporanProgram object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(LaporanProgram laporan) {
        String query = "INSERT INTO laporan_program (id_program, laporan, dokumentasi, tanggal_pelaksanaan) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, laporan.getIdProgram());
            stmt.setString(2, laporan.getLaporan());
            stmt.setString(3, laporan.getDokumentasi());
            
            if (laporan.getTanggalPelaksanaan() != null) {
                stmt.setDate(4, java.sql.Date.valueOf(laporan.getTanggalPelaksanaan()));
            } else {
                stmt.setDate(4, null);
            }
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan laporan program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update data laporan program
     * @param laporan LaporanProgram object yang akan diupdate
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(LaporanProgram laporan) {
        String query = "UPDATE laporan_program SET laporan = ?, dokumentasi = ?, tanggal_pelaksanaan = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, laporan.getLaporan());
            stmt.setString(2, laporan.getDokumentasi());
            
            if (laporan.getTanggalPelaksanaan() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(laporan.getTanggalPelaksanaan()));
            } else {
                stmt.setDate(3, null);
            }
            
            stmt.setInt(4, laporan.getId());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update laporan program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus laporan program dari database
     * @param id ID laporan yang akan dihapus
     * @return true jika berhasil, false jika gagal
     */
    public boolean delete(int id) {
        String query = "DELETE FROM laporan_program WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menghapus laporan program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method untuk mapping ResultSet ke LaporanProgram object
     */
    private LaporanProgram mapResultSetToLaporanProgram(ResultSet rs) throws SQLException {
        LaporanProgram laporan = new LaporanProgram();
        laporan.setId(rs.getInt("id"));
        laporan.setIdProgram(rs.getInt("id_program"));
        laporan.setLaporan(rs.getString("laporan"));
        laporan.setDokumentasi(rs.getString("dokumentasi"));
        
        java.sql.Date date = rs.getDate("tanggal_pelaksanaan");
        if (date != null) {
            laporan.setTanggalPelaksanaan(date.toLocalDate());
        }
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            laporan.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        timestamp = rs.getTimestamp("updated_at");
        if (timestamp != null) {
            laporan.setUpdatedAt(timestamp.toLocalDateTime());
        }
        
        return laporan;
    }
}

