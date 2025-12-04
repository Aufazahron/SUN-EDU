package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.Donasi;

/**
 * Data Access Object untuk operasi database pada tabel donasi
 */
public class DonasiDAO extends HomeDAO {
    
    /**
     * Menyimpan donasi baru ke database
     * @param donasi Donasi object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(Donasi donasi) {
        String query = "INSERT INTO donasi (id_program, id_user, nominal, bukti_transfer, catatan_donatur) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, donasi.getIdProgram());
            stmt.setInt(2, donasi.getIdUser());
            stmt.setLong(3, donasi.getNominal());
            stmt.setString(4, donasi.getBuktiTransfer());
            stmt.setString(5, donasi.getCatatanDonatur());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Update donasi terkumpul dan jumlah donatur di tabel program
                updateProgramDonasi(donasi.getIdProgram(), donasi.getNominal());
                return true;
            }
            
            return false;
        } catch (SQLException e) {
            System.err.println("Error menyimpan donasi: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update donasi terkumpul dan jumlah donatur di tabel program
     * @param idProgram ID program
     * @param nominal Nominal donasi yang ditambahkan
     */
    private void updateProgramDonasi(int idProgram, long nominal) {
        String query = "UPDATE program SET donasi_terkumpul = donasi_terkumpul + ?, jumlah_donatur = jumlah_donatur + 1 WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setLong(1, nominal);
            stmt.setInt(2, idProgram);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error update program donasi: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Mengambil total donasi yang diberikan oleh user tertentu
     * @param idUser ID user
     * @return Total nominal donasi
     */
    public long getTotalDonasiByUser(int idUser) {
        String query = "SELECT COALESCE(SUM(nominal), 0) as total FROM donasi WHERE id_user = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil total donasi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mengambil jumlah program yang didonasi oleh user tertentu
     * @param idUser ID user
     * @return Jumlah program yang didonasi
     */
    public int getJumlahProgramDonasi(int idUser) {
        String query = "SELECT COUNT(DISTINCT id_program) as jumlah FROM donasi WHERE id_user = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil jumlah program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
}

