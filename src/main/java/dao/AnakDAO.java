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
import model.Anak;

/**
 * Data Access Object untuk operasi database pada tabel anak
 */
public class AnakDAO extends HomeDAO {
    
    /**
     * Mengambil semua anak dari database
     * @return List of Anak objects
     */
    public List<Anak> getAllAnak() {
        String query = "SELECT * FROM anak ORDER BY created_at DESC";
        List<Anak> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Anak anak = mapResultSetToAnak(rs);
                anakList.add(anak);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil anak berdasarkan ID relawan
     * @param idRelawan ID relawan
     * @return List of Anak objects
     */
    public List<Anak> getAnakByRelawan(int idRelawan) {
        String query = "SELECT * FROM anak WHERE id_relawan = ? ORDER BY created_at DESC";
        List<Anak> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idRelawan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Anak anak = mapResultSetToAnak(rs);
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil anak berdasarkan status pendidikan
     * @param statusPendidikan Status pendidikan (Rentan, Dalam Pemantauan, Stabil)
     * @return List of Anak objects
     */
    public List<Anak> getAnakByStatusPendidikan(String statusPendidikan) {
        String query = "SELECT * FROM anak WHERE status = ? ORDER BY created_at DESC";
        List<Anak> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, statusPendidikan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Anak anak = mapResultSetToAnak(rs);
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil anak berdasarkan ID relawan dan status pendidikan
     * @param idRelawan ID relawan
     * @param statusPendidikan Status pendidikan
     * @return List of Anak objects
     */
    public List<Anak> getAnakByRelawanAndStatus(int idRelawan, String statusPendidikan) {
        String query = "SELECT * FROM anak WHERE id_relawan = ? AND status = ? ORDER BY created_at DESC";
        List<Anak> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idRelawan);
            stmt.setString(2, statusPendidikan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Anak anak = mapResultSetToAnak(rs);
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil anak berdasarkan ID
     * @param id ID anak
     * @return Anak object jika ditemukan, null jika tidak ditemukan
     */
    public Anak getAnakById(int id) {
        String query = "SELECT * FROM anak WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnak(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Menyimpan anak baru ke database
     * @param anak Anak object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(Anak anak) {
        String query = "INSERT INTO anak (id_relawan, nama, tanggal_lahir, jenis_kelamin, alamat, status, nama_orangtua, no_telp_orangtua) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, anak.getIdRelawan());
            stmt.setString(2, anak.getNama());
            
            if (anak.getTanggalLahir() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(anak.getTanggalLahir()));
            } else {
                stmt.setDate(3, null);
            }
            
            stmt.setString(4, anak.getJenisKelamin());
            stmt.setString(5, anak.getAlamat());
            stmt.setString(6, anak.getStatusPendidikan());
            stmt.setString(7, anak.getNamaOrangtua());
            stmt.setString(8, anak.getNoTelpOrangtua());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update data anak
     * @param anak Anak object yang akan diupdate
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(Anak anak) {
        String query = "UPDATE anak SET id_relawan = ?, nama = ?, tanggal_lahir = ?, jenis_kelamin = ?, alamat = ?, status = ?, nama_orangtua = ?, no_telp_orangtua = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, anak.getIdRelawan());
            stmt.setString(2, anak.getNama());
            
            if (anak.getTanggalLahir() != null) {
                stmt.setDate(3, java.sql.Date.valueOf(anak.getTanggalLahir()));
            } else {
                stmt.setDate(3, null);
            }
            
            stmt.setString(4, anak.getJenisKelamin());
            stmt.setString(5, anak.getAlamat());
            stmt.setString(6, anak.getStatusPendidikan());
            stmt.setString(7, anak.getNamaOrangtua());
            stmt.setString(8, anak.getNoTelpOrangtua());
            stmt.setInt(9, anak.getId());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus anak dari database
     * @param id ID anak yang akan dihapus
     * @return true jika berhasil, false jika gagal
     */
    public boolean delete(int id) {
        String query = "DELETE FROM anak WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menghapus anak: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mencari anak berdasarkan keyword (nama, alamat)
     * @param keyword Keyword untuk pencarian
     * @param idRelawan ID relawan (optional, bisa 0 untuk semua)
     * @return List of Anak objects
     */
    public List<Anak> searchAnak(String keyword, int idRelawan) {
        String query;
        if (idRelawan > 0) {
            query = "SELECT * FROM anak WHERE id_relawan = ? AND (nama LIKE ? OR alamat LIKE ?) ORDER BY created_at DESC";
        } else {
            query = "SELECT * FROM anak WHERE nama LIKE ? OR alamat LIKE ? ORDER BY created_at DESC";
        }
        
        List<Anak> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            
            if (idRelawan > 0) {
                stmt.setInt(1, idRelawan);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
            } else {
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Anak anak = mapResultSetToAnak(rs);
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil semua anak dengan informasi relawan (untuk admin)
     * @return List of Anak objects dengan nama relawan
     */
    public List<AnakWithRelawan> getAllAnakWithRelawan() {
        String query = "SELECT a.*, u.nama as nama_relawan FROM anak a " +
                      "LEFT JOIN user u ON a.id_relawan = u.id " +
                      "ORDER BY a.created_at DESC";
        List<AnakWithRelawan> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                AnakWithRelawan anak = new AnakWithRelawan();
                anak.setId(rs.getInt("id"));
                anak.setIdRelawan(rs.getInt("id_relawan"));
                anak.setNama(rs.getString("nama"));
                
                java.sql.Date date = rs.getDate("tanggal_lahir");
                if (date != null) {
                    anak.setTanggalLahir(date.toLocalDate());
                }
                
                anak.setJenisKelamin(rs.getString("jenis_kelamin"));
                anak.setAlamat(rs.getString("alamat"));
                anak.setStatusPendidikan(rs.getString("status"));
                anak.setNamaOrangtua(rs.getString("nama_orangtua"));
                anak.setNoTelpOrangtua(rs.getString("no_telp_orangtua"));
                anak.setNamaRelawan(rs.getString("nama_relawan"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    anak.setCreatedAt(timestamp.toLocalDateTime());
                }
                
                timestamp = rs.getTimestamp("updated_at");
                if (timestamp != null) {
                    anak.setUpdatedAt(timestamp.toLocalDateTime());
                }
                
                anakList.add(anak);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak dengan relawan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mencari anak dengan informasi relawan berdasarkan keyword (untuk admin)
     * @param keyword Keyword untuk pencarian
     * @return List of AnakWithRelawan objects
     */
    public List<AnakWithRelawan> searchAnakWithRelawan(String keyword) {
        String query = "SELECT a.*, u.nama as nama_relawan FROM anak a " +
                      "LEFT JOIN user u ON a.id_relawan = u.id " +
                      "WHERE a.nama LIKE ? OR a.alamat LIKE ? OR u.nama LIKE ? " +
                      "ORDER BY a.created_at DESC";
        
        List<AnakWithRelawan> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AnakWithRelawan anak = new AnakWithRelawan();
                    anak.setId(rs.getInt("id"));
                    anak.setIdRelawan(rs.getInt("id_relawan"));
                    anak.setNama(rs.getString("nama"));
                    
                    java.sql.Date date = rs.getDate("tanggal_lahir");
                    if (date != null) {
                        anak.setTanggalLahir(date.toLocalDate());
                    }
                    
                    anak.setJenisKelamin(rs.getString("jenis_kelamin"));
                    anak.setAlamat(rs.getString("alamat"));
                    anak.setStatusPendidikan(rs.getString("status"));
                    anak.setNamaOrangtua(rs.getString("nama_orangtua"));
                    anak.setNoTelpOrangtua(rs.getString("no_telp_orangtua"));
                    anak.setNamaRelawan(rs.getString("nama_relawan"));
                    
                    Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        anak.setCreatedAt(timestamp.toLocalDateTime());
                    }
                    
                    timestamp = rs.getTimestamp("updated_at");
                    if (timestamp != null) {
                        anak.setUpdatedAt(timestamp.toLocalDateTime());
                    }
                    
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari anak dengan relawan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil anak berdasarkan ID relawan dengan informasi relawan (untuk admin)
     * @param idRelawan ID relawan
     * @return List of AnakWithRelawan objects
     */
    public List<AnakWithRelawan> getAnakByRelawanWithInfo(int idRelawan) {
        String query = "SELECT a.*, u.nama as nama_relawan FROM anak a " +
                      "LEFT JOIN user u ON a.id_relawan = u.id " +
                      "WHERE a.id_relawan = ? " +
                      "ORDER BY a.created_at DESC";
        List<AnakWithRelawan> anakList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idRelawan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AnakWithRelawan anak = new AnakWithRelawan();
                    anak.setId(rs.getInt("id"));
                    anak.setIdRelawan(rs.getInt("id_relawan"));
                    anak.setNama(rs.getString("nama"));
                    
                    java.sql.Date date = rs.getDate("tanggal_lahir");
                    if (date != null) {
                        anak.setTanggalLahir(date.toLocalDate());
                    }
                    
                    anak.setJenisKelamin(rs.getString("jenis_kelamin"));
                    anak.setAlamat(rs.getString("alamat"));
                    anak.setStatusPendidikan(rs.getString("status"));
                    anak.setNamaOrangtua(rs.getString("nama_orangtua"));
                    anak.setNoTelpOrangtua(rs.getString("no_telp_orangtua"));
                    anak.setNamaRelawan(rs.getString("nama_relawan"));
                    
                    Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        anak.setCreatedAt(timestamp.toLocalDateTime());
                    }
                    
                    timestamp = rs.getTimestamp("updated_at");
                    if (timestamp != null) {
                        anak.setUpdatedAt(timestamp.toLocalDateTime());
                    }
                    
                    anakList.add(anak);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data anak dengan relawan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return anakList;
    }
    
    /**
     * Mengambil statistik jumlah anak per status pendidikan
     * @return Map dengan key status dan value jumlah
     */
    public java.util.Map<String, Integer> getStatistikByStatus() {
        String query = "SELECT status, COUNT(*) as jumlah FROM anak GROUP BY status";
        java.util.Map<String, Integer> statistik = new java.util.HashMap<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String status = rs.getString("status");
                int jumlah = rs.getInt("jumlah");
                statistik.put(status, jumlah);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil statistik: " + e.getMessage());
            e.printStackTrace();
        }
        
        return statistik;
    }
    
    /**
     * Helper method untuk mapping ResultSet ke Anak object
     */
    private Anak mapResultSetToAnak(ResultSet rs) throws SQLException {
        Anak anak = new Anak();
        anak.setId(rs.getInt("id"));
        anak.setIdRelawan(rs.getInt("id_relawan"));
        anak.setNama(rs.getString("nama"));
        
        java.sql.Date date = rs.getDate("tanggal_lahir");
        if (date != null) {
            anak.setTanggalLahir(date.toLocalDate());
        }
        
        anak.setJenisKelamin(rs.getString("jenis_kelamin"));
        anak.setAlamat(rs.getString("alamat"));
        anak.setStatusPendidikan(rs.getString("status"));
        anak.setNamaOrangtua(rs.getString("nama_orangtua"));
        anak.setNoTelpOrangtua(rs.getString("no_telp_orangtua"));
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            anak.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        timestamp = rs.getTimestamp("updated_at");
        if (timestamp != null) {
            anak.setUpdatedAt(timestamp.toLocalDateTime());
        }
        
        return anak;
    }
    
    /**
     * Mengambil jumlah anak yang dipegang oleh relawan tertentu
     * @param idRelawan ID relawan
     * @return Jumlah anak
     */
    public int getJumlahAnakByRelawan(int idRelawan) {
        String query = "SELECT COUNT(*) as jumlah FROM anak WHERE id_relawan = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idRelawan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("jumlah");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil jumlah anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mengambil list nama anak yang dipegang oleh relawan tertentu
     * @param idRelawan ID relawan
     * @return List nama anak
     */
    public List<String> getNamaAnakByRelawan(int idRelawan) {
        String query = "SELECT nama FROM anak WHERE id_relawan = ? ORDER BY nama ASC";
        List<String> namaList = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idRelawan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    namaList.add(rs.getString("nama"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil nama anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return namaList;
    }
    
    /**
     * Inner class untuk Anak dengan informasi relawan
     */
    public static class AnakWithRelawan extends Anak {
        private String namaRelawan;
        
        public String getNamaRelawan() {
            return namaRelawan;
        }
        
        public void setNamaRelawan(String namaRelawan) {
            this.namaRelawan = namaRelawan;
        }
    }
}

