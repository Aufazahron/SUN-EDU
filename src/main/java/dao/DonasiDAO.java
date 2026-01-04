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
    
    /**
     * Mengambil total donasi per bulan untuk user tertentu dalam tahun tertentu
     * @param idUser ID user
     * @param tahun Tahun yang akan diambil datanya
     * @return Map dengan key bulan (1-12) dan value total donasi
     */
    public java.util.Map<Integer, Long> getDonasiPerBulanByUser(int idUser, int tahun) {
        String query = """
            SELECT MONTH(tanggal_donasi) as bulan, COALESCE(SUM(nominal), 0) as total
            FROM donasi
            WHERE id_user = ? AND YEAR(tanggal_donasi) = ?
            GROUP BY MONTH(tanggal_donasi)
            """;
        
        java.util.Map<Integer, Long> donasiPerBulan = new java.util.HashMap<>();
        
        // Initialize semua bulan dengan 0
        for (int i = 1; i <= 12; i++) {
            donasiPerBulan.put(i, 0L);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            stmt.setInt(2, tahun);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int bulan = rs.getInt("bulan");
                    long total = rs.getLong("total");
                    donasiPerBulan.put(bulan, total);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil donasi per bulan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donasiPerBulan;
    }
    
    /**
     * Mengambil total donasi per kategori untuk user tertentu dalam tahun tertentu
     * @param idUser ID user
     * @param tahun Tahun yang akan diambil datanya
     * @return Map dengan key kategori dan value total donasi
     */
    public java.util.Map<String, Long> getDonasiPerKategoriByUser(int idUser, int tahun) {
        String query = """
            SELECT 
                COALESCE(p.kategori, 'Tidak Dikategorikan') AS kategori,
                COALESCE(SUM(d.nominal), 0) AS total_donasi
            FROM donasi d
            INNER JOIN program p ON d.id_program = p.id
            WHERE d.id_user = ? AND YEAR(d.tanggal_donasi) = ?
            GROUP BY p.kategori
            ORDER BY total_donasi DESC
            """;
        
        java.util.Map<String, Long> donasiPerKategori = new java.util.HashMap<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idUser);
            stmt.setInt(2, tahun);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String kategori = rs.getString("kategori");
                    long total = rs.getLong("total_donasi");
                    donasiPerKategori.put(kategori, total);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil donasi per kategori: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donasiPerKategori;
    }
    
    /**
     * Inner class untuk menyimpan data donatur dengan informasi user
     */
    public static class DonaturInfo {
        private int idDonasi;
        private int idUser;
        private String namaUser;
        private long nominal;
        private String catatanDonatur;
        private LocalDateTime tanggalDonasi;
        
        public DonaturInfo(int idDonasi, int idUser, String namaUser, long nominal, String catatanDonatur, LocalDateTime tanggalDonasi) {
            this.idDonasi = idDonasi;
            this.idUser = idUser;
            this.namaUser = namaUser;
            this.nominal = nominal;
            this.catatanDonatur = catatanDonatur;
            this.tanggalDonasi = tanggalDonasi;
        }
        
        public int getIdDonasi() { return idDonasi; }
        public int getIdUser() { return idUser; }
        public String getNamaUser() { return namaUser; }
        public long getNominal() { return nominal; }
        public String getCatatanDonatur() { return catatanDonatur; }
        public LocalDateTime getTanggalDonasi() { return tanggalDonasi; }
    }
    
    /**
     * Mengambil daftar donatur untuk program tertentu
     * @param idProgram ID program
     * @return List donatur dengan informasi nama dan nominal
     */
    public java.util.List<DonaturInfo> getDonaturByProgram(int idProgram) {
        String query = """
            SELECT 
                d.id as id_donasi,
                d.id_user,
                u.nama as nama_user,
                d.nominal,
                d.catatan_donatur,
                d.tanggal_donasi
            FROM donasi d
            INNER JOIN user u ON d.id_user = u.id
            WHERE d.id_program = ?
            ORDER BY d.tanggal_donasi DESC
            """;
        
        java.util.List<DonaturInfo> donaturList = new java.util.ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, idProgram);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idDonasi = rs.getInt("id_donasi");
                    int idUser = rs.getInt("id_user");
                    String namaUser = rs.getString("nama_user");
                    long nominal = rs.getLong("nominal");
                    String catatanDonatur = rs.getString("catatan_donatur");
                    
                    Timestamp timestamp = rs.getTimestamp("tanggal_donasi");
                    LocalDateTime tanggalDonasi = timestamp != null ? timestamp.toLocalDateTime() : null;
                    
                    DonaturInfo donatur = new DonaturInfo(idDonasi, idUser, namaUser, nominal, catatanDonatur, tanggalDonasi);
                    donaturList.add(donatur);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil donatur program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return donaturList;
    }
}

