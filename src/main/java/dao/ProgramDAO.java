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
import model.Program;

/**
 * Data Access Object untuk operasi database pada tabel program
 */
public class ProgramDAO extends HomeDAO {
    
    /**
     * Mengambil semua program dari database
     * @return List of Program objects
     */
    public List<Program> getAllPrograms() {
        String query = "SELECT * FROM program ORDER BY created_at DESC";
        List<Program> programs = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Program program = mapResultSetToProgram(rs);
                programs.add(program);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil data program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return programs;
    }
    
    /**
     * Mengambil program berdasarkan ID
     * @param id ID program
     * @return Program object jika ditemukan, null jika tidak ditemukan
     */
    public Program getProgramById(int id) {
        String query = "SELECT * FROM program WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProgram(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Mengambil program yang pernah didonasi oleh user tertentu
     * @param idUser ID user
     * @return List program yang pernah didukung user
     */
    public List<Program> getProgramsDonatedByUser(int idUser) {
        String query = """
                SELECT DISTINCT p.*
                FROM program p
                JOIN donasi d ON d.id_program = p.id
                WHERE d.id_user = ?
                ORDER BY p.created_at DESC
                """;

        List<Program> programs = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Program program = mapResultSetToProgram(rs);
                    programs.add(program);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil program yang didonasi user: " + e.getMessage());
            e.printStackTrace();
        }

        return programs;
    }
    
    /**
     * Menyimpan program baru ke database
     * @param program Program object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(Program program) {
        String query = "INSERT INTO program (id_user, nama, deskripsi, tempat, kategori, cover, target_donasi, tanggal_pelaksanaan) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, program.getIdUser());
            stmt.setString(2, program.getNama());
            stmt.setString(3, program.getDeskripsi());
            stmt.setString(4, program.getTempat());
            stmt.setString(5, program.getKategori());
            stmt.setString(6, program.getCover());
            stmt.setLong(7, program.getTargetDonasi());
            if (program.getTanggalPelaksanaan() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(program.getTanggalPelaksanaan()));
            } else {
                stmt.setDate(8, null);
            }
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update data program
     * @param program Program object yang akan diupdate
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(Program program) {
        String query = "UPDATE program SET nama = ?, deskripsi = ?, tempat = ?, kategori = ?, cover = ?, target_donasi = ?, tanggal_pelaksanaan = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, program.getNama());
            stmt.setString(2, program.getDeskripsi());
            stmt.setString(3, program.getTempat());
            stmt.setString(4, program.getKategori());
            stmt.setString(5, program.getCover());
            stmt.setLong(6, program.getTargetDonasi());
            if (program.getTanggalPelaksanaan() != null) {
                stmt.setDate(7, java.sql.Date.valueOf(program.getTanggalPelaksanaan()));
            } else {
                stmt.setDate(7, null);
            }
            stmt.setInt(8, program.getId());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error update program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menghapus program dari database
     * @param id ID program yang akan dihapus
     * @return true jika berhasil, false jika gagal
     */
    public boolean delete(int id) {
        String query = "DELETE FROM program WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menghapus program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mencari program berdasarkan keyword (nama, deskripsi, tempat, kategori)
     * @param keyword Keyword untuk pencarian
     * @return List of Program objects
     */
    public List<Program> searchProgram(String keyword) {
        String query = "SELECT * FROM program WHERE nama LIKE ? OR deskripsi LIKE ? OR tempat LIKE ? OR kategori LIKE ? ORDER BY created_at DESC";
        List<Program> programs = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Program program = mapResultSetToProgram(rs);
                    programs.add(program);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return programs;
    }
    
    /**
     * Mengambil program berdasarkan status
     * @param status Status program (draft, aktif, selesai, dibatalkan)
     * @return List of Program objects
     */
    public List<Program> getProgramsByStatus(String status) {
        String query = "SELECT * FROM program WHERE status_program = ? ORDER BY created_at DESC";
        List<Program> programs = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Program program = mapResultSetToProgram(rs);
                    programs.add(program);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil program by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return programs;
    }
    
    /**
     * Mengambil status program berdasarkan ID
     * @param id ID program
     * @return Status program atau null jika tidak ditemukan
     */
    public String getStatusProgram(int id) {
        String query = "SELECT status_program FROM program WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status_program");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil status program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mengambil program yang memiliki laporan
     * @return List of Program objects yang memiliki laporan
     */
    public List<Program> getProgramsWithLaporan() {
        String query = """
            SELECT DISTINCT p.*
            FROM program p
            INNER JOIN laporan_program lp ON p.id = lp.id_program
            ORDER BY lp.created_at DESC
            """;
        List<Program> programs = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Program program = mapResultSetToProgram(rs);
                programs.add(program);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil program dengan laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return programs;
    }
    
    /**
     * Helper method untuk mapping ResultSet ke Program object
     */
    private Program mapResultSetToProgram(ResultSet rs) throws SQLException {
        Program program = new Program();
        program.setId(rs.getInt("id"));
        program.setIdUser(rs.getInt("id_user"));
        program.setNama(rs.getString("nama"));
        program.setDeskripsi(rs.getString("deskripsi"));
        program.setTempat(rs.getString("tempat"));
        program.setKategori(rs.getString("kategori"));
        program.setCover(rs.getString("cover"));
        program.setTargetDonasi(rs.getLong("target_donasi"));
        program.setDonasiTerkumpul(rs.getLong("donasi_terkumpul"));
        program.setJumlahDonatur(rs.getInt("jumlah_donatur"));
        
        java.sql.Date tanggalPelaksanaan = rs.getDate("tanggal_pelaksanaan");
        if (tanggalPelaksanaan != null) {
            program.setTanggalPelaksanaan(tanggalPelaksanaan.toLocalDate());
        }
        
        Timestamp timestamp = rs.getTimestamp("created_at");
        if (timestamp != null) {
            program.setCreatedAt(timestamp.toLocalDateTime());
        }
        
        return program;
    }
}
