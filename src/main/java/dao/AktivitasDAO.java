package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import model.Aktivitas;

/**
 * Data Access Object untuk mengambil aktivitas terbaru dari berbagai sumber
 */
public class AktivitasDAO extends HomeDAO {
    
    private AnakDAO anakDAO;
    private LaporanMonitoringDAO laporanDAO;
    private DonasiDAO donasiDAO;
    private PesanDAO pesanDAO;
    private UserDAO userDAO;
    
    public AktivitasDAO() {
        anakDAO = new AnakDAO();
        laporanDAO = new LaporanMonitoringDAO();
        donasiDAO = new DonasiDAO();
        pesanDAO = new PesanDAO();
        userDAO = new UserDAO();
    }
    
    /**
     * Mengambil semua aktivitas terbaru dari berbagai sumber
     * @param limit Jumlah maksimal aktivitas yang diambil
     * @return List of Aktivitas objects, diurutkan dari yang terbaru
     */
    public List<Aktivitas> getAktivitasTerbaru(int limit) {
        List<Aktivitas> semuaAktivitas = new ArrayList<>();
        
        // 1. Aktivitas anak baru
        semuaAktivitas.addAll(getAktivitasAnakBaru());
        
        // 2. Aktivitas laporan monitoring baru
        semuaAktivitas.addAll(getAktivitasLaporanBaru());
        
        // 3. Aktivitas donasi baru
        semuaAktivitas.addAll(getAktivitasDonasiBaru());
        
        // 4. Aktivitas pesan FAQ baru
        semuaAktivitas.addAll(getAktivitasPesanBaru());
        
        // 5. Aktivitas relawan baru (pending)
        semuaAktivitas.addAll(getAktivitasRelawanBaru());
        
        // Urutkan berdasarkan waktu (terbaru dulu)
        Collections.sort(semuaAktivitas, new Comparator<Aktivitas>() {
            @Override
            public int compare(Aktivitas a1, Aktivitas a2) {
                if (a1.getWaktu() == null && a2.getWaktu() == null) return 0;
                if (a1.getWaktu() == null) return 1;
                if (a2.getWaktu() == null) return -1;
                return a2.getWaktu().compareTo(a1.getWaktu());
            }
        });
        
        // Ambil hanya limit teratas
        if (semuaAktivitas.size() > limit) {
            return semuaAktivitas.subList(0, limit);
        }
        
        return semuaAktivitas;
    }
    
    /**
     * Mengambil aktivitas anak baru
     */
    private List<Aktivitas> getAktivitasAnakBaru() {
        List<Aktivitas> aktivitasList = new ArrayList<>();
        String query = "SELECT a.id, a.nama, a.created_at, u.nama as nama_relawan " +
                      "FROM anak a " +
                      "LEFT JOIN user u ON a.id_relawan = u.id " +
                      "ORDER BY a.created_at DESC " +
                      "LIMIT 10";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Aktivitas aktivitas = new Aktivitas();
                aktivitas.setTipe("anak_baru");
                aktivitas.setId(rs.getInt("id"));
                aktivitas.setJudul("Anak Baru Ditambahkan");
                aktivitas.setDeskripsi("Relawan " + (rs.getString("nama_relawan") != null ? rs.getString("nama_relawan") : "Unknown") + 
                                      " menambahkan anak baru: " + rs.getString("nama"));
                aktivitas.setNamaUser(rs.getString("nama_relawan"));
                
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    aktivitas.setWaktu(createdAt.toLocalDateTime());
                }
                
                aktivitasList.add(aktivitas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil aktivitas anak: " + e.getMessage());
            e.printStackTrace();
        }
        
        return aktivitasList;
    }
    
    /**
     * Mengambil aktivitas laporan monitoring baru
     */
    private List<Aktivitas> getAktivitasLaporanBaru() {
        List<Aktivitas> aktivitasList = new ArrayList<>();
        String query = "SELECT l.id, l.nama, l.status, l.created_at, u.nama as nama_relawan " +
                      "FROM laporan_monitoring l " +
                      "LEFT JOIN user u ON l.id_user = u.id " +
                      "WHERE l.status IN ('Draft', 'Diajukan') " +
                      "ORDER BY l.created_at DESC " +
                      "LIMIT 10";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Aktivitas aktivitas = new Aktivitas();
                aktivitas.setTipe("laporan_baru");
                aktivitas.setId(rs.getInt("id"));
                aktivitas.setJudul("Laporan Monitoring Baru");
                aktivitas.setDeskripsi("Relawan " + (rs.getString("nama_relawan") != null ? rs.getString("nama_relawan") : "Unknown") + 
                                      " mengajukan laporan: " + rs.getString("nama") + 
                                      " (Status: " + rs.getString("status") + ")");
                aktivitas.setNamaUser(rs.getString("nama_relawan"));
                
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    aktivitas.setWaktu(createdAt.toLocalDateTime());
                }
                
                aktivitasList.add(aktivitas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil aktivitas laporan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return aktivitasList;
    }
    
    /**
     * Mengambil aktivitas donasi baru
     */
    private List<Aktivitas> getAktivitasDonasiBaru() {
        List<Aktivitas> aktivitasList = new ArrayList<>();
        String query = "SELECT d.id, d.nominal, d.tanggal_donasi, u.nama as nama_donatur, p.nama as nama_program " +
                      "FROM donasi d " +
                      "LEFT JOIN user u ON d.id_user = u.id " +
                      "LEFT JOIN program p ON d.id_program = p.id " +
                      "ORDER BY d.tanggal_donasi DESC " +
                      "LIMIT 10";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Aktivitas aktivitas = new Aktivitas();
                aktivitas.setTipe("donasi_baru");
                aktivitas.setId(rs.getInt("id"));
                aktivitas.setJudul("Donasi Baru");
                long nominal = rs.getLong("nominal");
                String formatNominal = String.format("Rp %,d", nominal).replace(",", ".");
                aktivitas.setDeskripsi((rs.getString("nama_donatur") != null ? rs.getString("nama_donatur") : "Donatur") + 
                                      " memberikan donasi " + formatNominal + 
                                      " untuk program: " + (rs.getString("nama_program") != null ? rs.getString("nama_program") : "Unknown"));
                aktivitas.setNamaUser(rs.getString("nama_donatur"));
                
                Timestamp tanggalDonasi = rs.getTimestamp("tanggal_donasi");
                if (tanggalDonasi != null) {
                    aktivitas.setWaktu(tanggalDonasi.toLocalDateTime());
                }
                
                aktivitasList.add(aktivitas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil aktivitas donasi: " + e.getMessage());
            e.printStackTrace();
        }
        
        return aktivitasList;
    }
    
    /**
     * Mengambil aktivitas pesan FAQ baru
     */
    private List<Aktivitas> getAktivitasPesanBaru() {
        List<Aktivitas> aktivitasList = new ArrayList<>();
        String query = "SELECT id, nama, email, subjek, created_at " +
                      "FROM pesan " +
                      "WHERE status = 'baru' " +
                      "ORDER BY created_at DESC " +
                      "LIMIT 10";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Aktivitas aktivitas = new Aktivitas();
                aktivitas.setTipe("pesan_baru");
                aktivitas.setId(rs.getInt("id"));
                aktivitas.setJudul("Pesan FAQ Baru");
                aktivitas.setDeskripsi(rs.getString("nama") + " mengirim pesan: " + rs.getString("subjek"));
                aktivitas.setNamaUser(rs.getString("nama"));
                
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    aktivitas.setWaktu(createdAt.toLocalDateTime());
                }
                
                aktivitasList.add(aktivitas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil aktivitas pesan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return aktivitasList;
    }
    
    /**
     * Mengambil aktivitas relawan baru (pending)
     */
    private List<Aktivitas> getAktivitasRelawanBaru() {
        List<Aktivitas> aktivitasList = new ArrayList<>();
        String query = "SELECT id, nama, email, created_at " +
                      "FROM user " +
                      "WHERE role = 'relawan' AND status = 0 " +
                      "ORDER BY created_at DESC " +
                      "LIMIT 10";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Aktivitas aktivitas = new Aktivitas();
                aktivitas.setTipe("relawan_baru");
                aktivitas.setId(rs.getInt("id"));
                aktivitas.setJudul("Relawan Baru Mendaftar");
                aktivitas.setDeskripsi(rs.getString("nama") + " mendaftar sebagai relawan (Pending Approval)");
                aktivitas.setNamaUser(rs.getString("nama"));
                
                Timestamp createdAt = rs.getTimestamp("created_at");
                if (createdAt != null) {
                    aktivitas.setWaktu(createdAt.toLocalDateTime());
                }
                
                aktivitasList.add(aktivitas);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil aktivitas relawan: " + e.getMessage());
            e.printStackTrace();
        }
        
        return aktivitasList;
    }
}

