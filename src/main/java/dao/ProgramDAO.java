package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
                Program program = new Program();
                program.setId(rs.getInt("id"));
                program.setIdUser(rs.getInt("id_user"));
                program.setNama(rs.getString("nama"));
                program.setDeskripsi(rs.getString("deskripsi"));
                program.setTempat(rs.getString("tempat"));
                program.setCover(rs.getString("cover"));
                program.setTargetDonasi(rs.getLong("target_donasi"));
                program.setDonasiTerkumpul(rs.getLong("donasi_terkumpul"));
                program.setJumlahDonatur(rs.getInt("jumlah_donatur"));
                
                Timestamp timestamp = rs.getTimestamp("created_at");
                if (timestamp != null) {
                    program.setCreatedAt(timestamp.toLocalDateTime());
                }
                
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
                    Program program = new Program();
                    program.setId(rs.getInt("id"));
                    program.setIdUser(rs.getInt("id_user"));
                    program.setNama(rs.getString("nama"));
                    program.setDeskripsi(rs.getString("deskripsi"));
                    program.setTempat(rs.getString("tempat"));
                    program.setCover(rs.getString("cover"));
                    program.setTargetDonasi(rs.getLong("target_donasi"));
                    program.setDonasiTerkumpul(rs.getLong("donasi_terkumpul"));
                    program.setJumlahDonatur(rs.getInt("jumlah_donatur"));
                    
                    Timestamp timestamp = rs.getTimestamp("created_at");
                    if (timestamp != null) {
                        program.setCreatedAt(timestamp.toLocalDateTime());
                    }
                    
                    return program;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil program: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Menyimpan program baru ke database
     * @param program Program object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(Program program) {
        String query = "INSERT INTO program (id_user, nama, deskripsi, tempat, cover) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, program.getIdUser());
            stmt.setString(2, program.getNama());
            stmt.setString(3, program.getDeskripsi());
            stmt.setString(4, program.getTempat());
            stmt.setString(5, program.getCover());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan program: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}


