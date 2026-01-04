package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk representasi data anak yang didampingi relawan
 */
public class Anak {
    private int id;
    private int idRelawan;
    private String nama;
    private LocalDate tanggalLahir;
    private String jenisKelamin; // 'L' atau 'P'
    private String alamat;
    private String statusPendidikan; // 'Diajukan', 'Rentan', 'Dalam Pemantauan', 'Stabil', 'Selesai'
    private String namaOrangtua;
    private String noTelpOrangtua;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Anak() {
    }
    
    public Anak(int id, int idRelawan, String nama, LocalDate tanggalLahir, 
                String jenisKelamin, String alamat, String statusPendidikan,
                String namaOrangtua, String noTelpOrangtua) {
        this.id = id;
        this.idRelawan = idRelawan;
        this.nama = nama;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.alamat = alamat;
        this.statusPendidikan = statusPendidikan;
        this.namaOrangtua = namaOrangtua;
        this.noTelpOrangtua = noTelpOrangtua;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdRelawan() {
        return idRelawan;
    }
    
    public void setIdRelawan(int idRelawan) {
        this.idRelawan = idRelawan;
    }
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }
    
    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }
    
    public String getJenisKelamin() {
        return jenisKelamin;
    }
    
    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }
    
    public String getAlamat() {
        return alamat;
    }
    
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    
    public String getStatusPendidikan() {
        return statusPendidikan;
    }
    
    public void setStatusPendidikan(String statusPendidikan) {
        this.statusPendidikan = statusPendidikan;
    }
    
    public String getNamaOrangtua() {
        return namaOrangtua;
    }
    
    public void setNamaOrangtua(String namaOrangtua) {
        this.namaOrangtua = namaOrangtua;
    }
    
    public String getNoTelpOrangtua() {
        return noTelpOrangtua;
    }
    
    public void setNoTelpOrangtua(String noTelpOrangtua) {
        this.noTelpOrangtua = noTelpOrangtua;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper method untuk mendapatkan usia
    public int getUsia() {
        if (tanggalLahir == null) {
            return 0;
        }
        return LocalDate.now().getYear() - tanggalLahir.getYear();
    }
}

