package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.DetailAnak;

/**
 * Data Access Object untuk operasi database pada tabel detail_anak
 */
public class DetailAnakDAO extends HomeDAO {
    
    /**
     * Mengambil detail anak berdasarkan ID anak
     * @param idAnak ID anak
     * @return DetailAnak object jika ditemukan, null jika tidak ditemukan
     */
    public DetailAnak getDetailByAnakId(int idAnak) {
        String query = "SELECT * FROM detail_anak WHERE id_anak = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idAnak);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDetailAnak(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil detail anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mengambil detail anak berdasarkan ID
     * @param id ID detail anak
     * @return DetailAnak object jika ditemukan, null jika tidak ditemukan
     */
    public DetailAnak getDetailById(int id) {
        String query = "SELECT * FROM detail_anak WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDetailAnak(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil detail anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Menyimpan detail anak baru ke database
     * @param detailAnak DetailAnak object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(DetailAnak detailAnak) {
        // Cek apakah kolom foto ada di database
        boolean hasFotoColumn = checkColumnExists("foto");
        
        String query;
        if (hasFotoColumn) {
            query = "INSERT INTO detail_anak (" +
                "id_anak, status_orangtua, nama_ayah, nama_ibu, nama_wali, tinggal_bersama, deskripsi_keluarga, " +
                "pekerjaan_ayah, penghasilan_ayah, pekerjaan_ibu, penghasilan_ibu, pekerjaan_wali, penghasilan_wali, deskripsi_ekonomi, " +
                "sekolah_terakhir, alasan_putus_sekolah, minat_belajar, " +
                "riwayat_penyakit, layanan_kesehatan, deskripsi_kesehatan, foto" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO detail_anak (" +
                "id_anak, status_orangtua, nama_ayah, nama_ibu, nama_wali, tinggal_bersama, deskripsi_keluarga, " +
                "pekerjaan_ayah, penghasilan_ayah, pekerjaan_ibu, penghasilan_ibu, pekerjaan_wali, penghasilan_wali, deskripsi_ekonomi, " +
                "sekolah_terakhir, alasan_putus_sekolah, minat_belajar, " +
                "riwayat_penyakit, layanan_kesehatan, deskripsi_kesehatan" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, detailAnak.getIdAnak());
            stmt.setString(2, detailAnak.getStatusOrangtua());
            stmt.setString(3, detailAnak.getNamaAyah());
            stmt.setString(4, detailAnak.getNamaIbu());
            stmt.setString(5, detailAnak.getNamaWali());
            stmt.setString(6, detailAnak.getTinggalBersama());
            stmt.setString(7, detailAnak.getDeskripsiKeluarga());
            stmt.setString(8, detailAnak.getPekerjaanAyah());
            stmt.setObject(9, detailAnak.getPenghasilanAyah());
            stmt.setString(10, detailAnak.getPekerjaanIbu());
            stmt.setObject(11, detailAnak.getPenghasilanIbu());
            stmt.setString(12, detailAnak.getPekerjaanWali());
            stmt.setObject(13, detailAnak.getPenghasilanWali());
            stmt.setString(14, detailAnak.getDeskripsiEkonomi());
            stmt.setString(15, detailAnak.getSekolahTerakhir());
            stmt.setString(16, detailAnak.getAlasanPutusSekolah());
            stmt.setString(17, detailAnak.getMinatBelajar());
            stmt.setString(18, detailAnak.getRiwayatPenyakit());
            stmt.setString(19, detailAnak.getLayananKesehatan());
            stmt.setString(20, detailAnak.getDeskripsiKesehatan());
            
            if (hasFotoColumn) {
                stmt.setString(21, detailAnak.getFoto());
            }
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan detail anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cek apakah kolom tertentu ada di tabel detail_anak
     */
    private boolean checkColumnExists(String columnName) {
        String query = "SELECT COUNT(*) as count FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() " +
            "AND TABLE_NAME = 'detail_anak' " +
            "AND COLUMN_NAME = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, columnName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking column existence: " + e.getMessage());
            // Jika error, assume column doesn't exist untuk safety
            return false;
        }
        return false;
    }
    
    /**
     * Update data detail anak
     * @param detailAnak DetailAnak object yang akan diupdate
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(DetailAnak detailAnak) {
        // Cek apakah kolom foto ada di database
        boolean hasFotoColumn = checkColumnExists("foto");
        
        String query;
        if (hasFotoColumn) {
            query = "UPDATE detail_anak SET " +
                "status_orangtua = ?, nama_ayah = ?, nama_ibu = ?, nama_wali = ?, tinggal_bersama = ?, deskripsi_keluarga = ?, " +
                "pekerjaan_ayah = ?, penghasilan_ayah = ?, pekerjaan_ibu = ?, penghasilan_ibu = ?, pekerjaan_wali = ?, penghasilan_wali = ?, deskripsi_ekonomi = ?, " +
                "sekolah_terakhir = ?, alasan_putus_sekolah = ?, minat_belajar = ?, " +
                "riwayat_penyakit = ?, layanan_kesehatan = ?, deskripsi_kesehatan = ?, foto = ? " +
                "WHERE id_anak = ?";
        } else {
            query = "UPDATE detail_anak SET " +
                "status_orangtua = ?, nama_ayah = ?, nama_ibu = ?, nama_wali = ?, tinggal_bersama = ?, deskripsi_keluarga = ?, " +
                "pekerjaan_ayah = ?, penghasilan_ayah = ?, pekerjaan_ibu = ?, penghasilan_ibu = ?, pekerjaan_wali = ?, penghasilan_wali = ?, deskripsi_ekonomi = ?, " +
                "sekolah_terakhir = ?, alasan_putus_sekolah = ?, minat_belajar = ?, " +
                "riwayat_penyakit = ?, layanan_kesehatan = ?, deskripsi_kesehatan = ? " +
                "WHERE id_anak = ?";
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, detailAnak.getStatusOrangtua());
            stmt.setString(2, detailAnak.getNamaAyah());
            stmt.setString(3, detailAnak.getNamaIbu());
            stmt.setString(4, detailAnak.getNamaWali());
            stmt.setString(5, detailAnak.getTinggalBersama());
            stmt.setString(6, detailAnak.getDeskripsiKeluarga());
            stmt.setString(7, detailAnak.getPekerjaanAyah());
            stmt.setObject(8, detailAnak.getPenghasilanAyah());
            stmt.setString(9, detailAnak.getPekerjaanIbu());
            stmt.setObject(10, detailAnak.getPenghasilanIbu());
            stmt.setString(11, detailAnak.getPekerjaanWali());
            stmt.setObject(12, detailAnak.getPenghasilanWali());
            stmt.setString(13, detailAnak.getDeskripsiEkonomi());
            stmt.setString(14, detailAnak.getSekolahTerakhir());
            stmt.setString(15, detailAnak.getAlasanPutusSekolah());
            stmt.setString(16, detailAnak.getMinatBelajar());
            stmt.setString(17, detailAnak.getRiwayatPenyakit());
            stmt.setString(18, detailAnak.getLayananKesehatan());
            stmt.setString(19, detailAnak.getDeskripsiKesehatan());
            
            if (hasFotoColumn) {
                stmt.setString(20, detailAnak.getFoto());
                stmt.setInt(21, detailAnak.getIdAnak());
            } else {
                stmt.setInt(20, detailAnak.getIdAnak());
            }
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update detail anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menyimpan atau update detail anak (upsert)
     * @param detailAnak DetailAnak object yang akan disimpan/update
     * @return true jika berhasil, false jika gagal
     */
    public boolean saveOrUpdate(DetailAnak detailAnak) {
        // Cek apakah sudah ada data
        DetailAnak existing = getDetailByAnakId(detailAnak.getIdAnak());
        
        if (existing != null) {
            // Update
            detailAnak.setId(existing.getId());
            return update(detailAnak);
        } else {
            // Insert
            return save(detailAnak);
        }
    }
    
    /**
     * Menghapus detail anak dari database
     * @param idAnak ID anak
     * @return true jika berhasil, false jika gagal
     */
    public boolean delete(int idAnak) {
        String query = "DELETE FROM detail_anak WHERE id_anak = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idAnak);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menghapus detail anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method untuk mapping ResultSet ke DetailAnak object
     */
    private DetailAnak mapResultSetToDetailAnak(ResultSet rs) throws SQLException {
        DetailAnak detail = new DetailAnak();
        detail.setId(rs.getInt("id"));
        detail.setIdAnak(rs.getInt("id_anak"));
        
        detail.setStatusOrangtua(rs.getString("status_orangtua"));
        detail.setNamaAyah(rs.getString("nama_ayah"));
        detail.setNamaIbu(rs.getString("nama_ibu"));
        detail.setNamaWali(rs.getString("nama_wali"));
        detail.setTinggalBersama(rs.getString("tinggal_bersama"));
        detail.setDeskripsiKeluarga(rs.getString("deskripsi_keluarga"));
        
        detail.setPekerjaanAyah(rs.getString("pekerjaan_ayah"));
        Long penghasilanAyah = rs.getLong("penghasilan_ayah");
        if (!rs.wasNull()) {
            detail.setPenghasilanAyah(penghasilanAyah);
        }
        detail.setPekerjaanIbu(rs.getString("pekerjaan_ibu"));
        Long penghasilanIbu = rs.getLong("penghasilan_ibu");
        if (!rs.wasNull()) {
            detail.setPenghasilanIbu(penghasilanIbu);
        }
        detail.setPekerjaanWali(rs.getString("pekerjaan_wali"));
        Long penghasilanWali = rs.getLong("penghasilan_wali");
        if (!rs.wasNull()) {
            detail.setPenghasilanWali(penghasilanWali);
        }
        detail.setDeskripsiEkonomi(rs.getString("deskripsi_ekonomi"));
        
        detail.setSekolahTerakhir(rs.getString("sekolah_terakhir"));
        detail.setAlasanPutusSekolah(rs.getString("alasan_putus_sekolah"));
        detail.setMinatBelajar(rs.getString("minat_belajar"));
        
        detail.setRiwayatPenyakit(rs.getString("riwayat_penyakit"));
        detail.setLayananKesehatan(rs.getString("layanan_kesehatan"));
        detail.setDeskripsiKesehatan(rs.getString("deskripsi_kesehatan"));
        
        // Handle foto column (might not exist in older databases)
        try {
            detail.setFoto(rs.getString("foto"));
        } catch (SQLException e) {
            // Column doesn't exist, set to null
            detail.setFoto(null);
        }
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            detail.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        timestamp = rs.getTimestamp("updated_at");
        if (timestamp != null) {
            detail.setUpdatedAt(timestamp.toLocalDateTime());
        }
        
        return detail;
    }
}

