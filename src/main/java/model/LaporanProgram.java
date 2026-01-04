package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk representasi data laporan program kerja
 */
public class LaporanProgram {
    private int id;
    private int idProgram;
    private String laporan;
    private String dokumentasi; // Path/file dokumentasi
    private LocalDate tanggalPelaksanaan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public LaporanProgram() {
    }
    
    public LaporanProgram(int id, int idProgram, String laporan, String dokumentasi, 
                         LocalDate tanggalPelaksanaan, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.idProgram = idProgram;
        this.laporan = laporan;
        this.dokumentasi = dokumentasi;
        this.tanggalPelaksanaan = tanggalPelaksanaan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdProgram() {
        return idProgram;
    }
    
    public void setIdProgram(int idProgram) {
        this.idProgram = idProgram;
    }
    
    public String getLaporan() {
        return laporan;
    }
    
    public void setLaporan(String laporan) {
        this.laporan = laporan;
    }
    
    public String getDokumentasi() {
        return dokumentasi;
    }
    
    public void setDokumentasi(String dokumentasi) {
        this.dokumentasi = dokumentasi;
    }
    
    public LocalDate getTanggalPelaksanaan() {
        return tanggalPelaksanaan;
    }
    
    public void setTanggalPelaksanaan(LocalDate tanggalPelaksanaan) {
        this.tanggalPelaksanaan = tanggalPelaksanaan;
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
}

