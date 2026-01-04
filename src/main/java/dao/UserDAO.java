package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

/**
 * Data Access Object untuk operasi database pada tabel user
 */
public class UserDAO extends HomeDAO {
    
    /**
     * Mencari user berdasarkan username dan password
     * @param username username user
     * @param password password user
     * @return User object jika ditemukan, null jika tidak ditemukan
     */
    public User login(String username, String password) {
        String query = "SELECT * FROM user WHERE username = ? AND password = ? AND status = 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setNama(rs.getString("nama"));
                    user.setTelp(rs.getString("telp"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getBoolean("status"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saat login: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mengambil user berdasarkan ID
     * @param id ID user
     * @return User object jika ditemukan, null jika tidak ditemukan
     */
    public User getUserById(int id) {
        String query = "SELECT * FROM user WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setNama(rs.getString("nama"));
                    user.setTelp(rs.getString("telp"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getBoolean("status"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Mencari user berdasarkan username saja
     * @param username username user
     * @return User object jika ditemukan, null jika tidak ditemukan
     */
    public User findByUsername(String username) {
        String query = "SELECT * FROM user WHERE username = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setNama(rs.getString("nama"));
                    user.setTelp(rs.getString("telp"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getBoolean("status"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error mencari user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Menyimpan user baru ke database
     * @param user User object yang akan disimpan
     * @return true jika berhasil, false jika gagal
     */
    public boolean save(User user) {
        String query = "INSERT INTO user (username, password, email, nama, telp, role, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getNama());
            stmt.setString(5, user.getTelp());
            stmt.setString(6, user.getRole());
            stmt.setBoolean(7, user.isStatus());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mengambil semua relawan dengan status pending (status = 0)
     * @return List User dengan role relawan dan status = 0
     */
    public java.util.List<User> getPendingRelawan() {
        java.util.List<User> relawanList = new java.util.ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'relawan' AND status = 0 ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setNama(rs.getString("nama"));
                user.setTelp(rs.getString("telp"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getBoolean("status"));
                relawanList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil relawan pending: " + e.getMessage());
            e.printStackTrace();
        }
        
        return relawanList;
    }
    
    /**
     * Mengambil semua relawan yang sudah aktif (status = 1)
     * @return List User dengan role relawan dan status = 1
     */
    public java.util.List<User> getActiveRelawan() {
        java.util.List<User> relawanList = new java.util.ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'relawan' AND status = 1 ORDER BY id DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setNama(rs.getString("nama"));
                user.setTelp(rs.getString("telp"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getBoolean("status"));
                relawanList.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil relawan aktif: " + e.getMessage());
            e.printStackTrace();
        }
        
        return relawanList;
    }
    
    /**
     * Approve relawan dengan mengubah status menjadi 1 (aktif)
     * @param userId ID user yang akan di-approve
     * @return true jika berhasil, false jika gagal
     */
    public boolean approveRelawan(int userId) {
        String query = "UPDATE user SET status = 1 WHERE id = ? AND role = 'relawan'";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error approve relawan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Menolak/hapus relawan (opsional, bisa digunakan untuk reject)
     * @param userId ID user yang akan ditolak
     * @return true jika berhasil, false jika gagal
     */
    public boolean rejectRelawan(int userId) {
        String query = "DELETE FROM user WHERE id = ? AND role = 'relawan' AND status = 0";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("Error reject relawan: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

