package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk representasi data program
 */
public class Program {
    private int id;
    private int idUser;
    private String nama;
    private String deskripsi;
    private String tempat;
    private String kategori;
    private String cover;
    private long targetDonasi;
    private long donasiTerkumpul;
    private int jumlahDonatur;
    private LocalDate tanggalPelaksanaan;
    private LocalDateTime createdAt;
    
    public Program() {
    }
    
    public Program(int id, int idUser, String nama, String deskripsi, 
                   String tempat, String kategori, String cover, long targetDonasi, 
                   long donasiTerkumpul, int jumlahDonatur, LocalDateTime createdAt) {
        this.id = id;
        this.idUser = idUser;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.tempat = tempat;
        this.kategori = kategori;
        this.cover = cover;
        this.targetDonasi = targetDonasi;
        this.donasiTerkumpul = donasiTerkumpul;
        this.jumlahDonatur = jumlahDonatur;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getDeskripsi() {
        return deskripsi;
    }
    
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }
    
    public String getTempat() {
        return tempat;
    }
    
    public void setTempat(String tempat) {
        this.tempat = tempat;
    }
    
    public String getKategori() {
        return kategori;
    }
    
    public void setKategori(String kategori) {
        this.kategori = kategori;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getTargetDonasi() {
        return targetDonasi;
    }
    
    public void setTargetDonasi(long targetDonasi) {
        this.targetDonasi = targetDonasi;
    }
    
    public long getDonasiTerkumpul() {
        return donasiTerkumpul;
    }
    
    public void setDonasiTerkumpul(long donasiTerkumpul) {
        this.donasiTerkumpul = donasiTerkumpul;
    }
    
    public int getJumlahDonatur() {
        return jumlahDonatur;
    }
    
    public void setJumlahDonatur(int jumlahDonatur) {
        this.jumlahDonatur = jumlahDonatur;
    }
    
    public LocalDate getTanggalPelaksanaan() {
        return tanggalPelaksanaan;
    }
    
    public void setTanggalPelaksanaan(LocalDate tanggalPelaksanaan) {
        this.tanggalPelaksanaan = tanggalPelaksanaan;
    }
}


