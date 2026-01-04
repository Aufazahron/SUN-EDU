package model;

import java.time.LocalDateTime;

/**
 * Model untuk representasi aktivitas/notifikasi di dashboard admin
 */
public class Aktivitas {
    private String tipe; // "anak_baru", "laporan_baru", "donasi_baru", "pesan_baru", "relawan_baru"
    private String judul;
    private String deskripsi;
    private LocalDateTime waktu;
    private int id; // ID dari record terkait
    private String namaUser; // Nama user yang melakukan aktivitas
    
    public Aktivitas() {
    }
    
    public Aktivitas(String tipe, String judul, String deskripsi, LocalDateTime waktu, int id, String namaUser) {
        this.tipe = tipe;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.waktu = waktu;
        this.id = id;
        this.namaUser = namaUser;
    }
    
    // Getters and Setters
    public String getTipe() {
        return tipe;
    }
    
    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
    
    public String getJudul() {
        return judul;
    }
    
    public void setJudul(String judul) {
        this.judul = judul;
    }
    
    public String getDeskripsi() {
        return deskripsi;
    }
    
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
    
    public LocalDateTime getWaktu() {
        return waktu;
    }
    
    public void setWaktu(LocalDateTime waktu) {
        this.waktu = waktu;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNamaUser() {
        return namaUser;
    }
    
    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
    
    /**
     * Mendapatkan icon emoji berdasarkan tipe aktivitas
     */
    public String getIcon() {
        switch (tipe) {
            case "anak_baru":
                return "👶";
            case "laporan_baru":
                return "📋";
            case "donasi_baru":
                return "💰";
            case "pesan_baru":
                return "💬";
            case "relawan_baru":
                return "👤";
            default:
                return "📢";
        }
    }
    
    /**
     * Mendapatkan warna background berdasarkan tipe aktivitas
     */
    public String getColor() {
        switch (tipe) {
            case "anak_baru":
                return "#E3F2FD";
            case "laporan_baru":
                return "#F3E5F5";
            case "donasi_baru":
                return "#E8F5E9";
            case "pesan_baru":
                return "#FFF3E0";
            case "relawan_baru":
                return "#E1F5FE";
            default:
                return "#F5F5F5";
        }
    }
}

