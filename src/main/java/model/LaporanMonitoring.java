package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk representasi data laporan monitoring anak
 */
public class LaporanMonitoring {
    private int id;
    private int idAnak;
    private int idUser; // ID relawan yang membuat laporan
    private String nama; // Nama/judul laporan
    private String status; // Status: Draft, Diajukan, Disetujui, Dikembalikan
    private String catatanRevisi; // Catatan revisi dari admin
    private LocalDate tanggalMonitoring;
    private String progressPendidikan;
    private String kondisiKesehatan;
    private String catatan;
    private String foto; // Path ke file foto
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Untuk join dengan tabel anak (optional)
    private String namaAnak;
    
    public LaporanMonitoring() {
    }
    
    public LaporanMonitoring(int id, int idAnak, int idUser, String nama, String status, 
                            LocalDate tanggalMonitoring, String progressPendidikan, 
                            String kondisiKesehatan, String catatan, String foto) {
        this.id = id;
        this.idAnak = idAnak;
        this.idUser = idUser;
        this.nama = nama;
        this.status = status;
        this.tanggalMonitoring = tanggalMonitoring;
        this.progressPendidikan = progressPendidikan;
        this.kondisiKesehatan = kondisiKesehatan;
        this.catatan = catatan;
        this.foto = foto;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdAnak() {
        return idAnak;
    }
    
    public void setIdAnak(int idAnak) {
        this.idAnak = idAnak;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCatatanRevisi() {
        return catatanRevisi;
    }
    
    public void setCatatanRevisi(String catatanRevisi) {
        this.catatanRevisi = catatanRevisi;
    }
    
    public LocalDate getTanggalMonitoring() {
        return tanggalMonitoring;
    }
    
    public void setTanggalMonitoring(LocalDate tanggalMonitoring) {
        this.tanggalMonitoring = tanggalMonitoring;
    }
    
    public String getProgressPendidikan() {
        return progressPendidikan;
    }
    
    public void setProgressPendidikan(String progressPendidikan) {
        this.progressPendidikan = progressPendidikan;
    }
    
    public String getKondisiKesehatan() {
        return kondisiKesehatan;
    }
    
    public void setKondisiKesehatan(String kondisiKesehatan) {
        this.kondisiKesehatan = kondisiKesehatan;
    }
    
    public String getCatatan() {
        return catatan;
    }
    
    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }
    
    public String getFoto() {
        return foto;
    }
    
    public void setFoto(String foto) {
        this.foto = foto;
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
    
    public String getNamaAnak() {
        return namaAnak;
    }
    
    public void setNamaAnak(String namaAnak) {
        this.namaAnak = namaAnak;
    }
}

