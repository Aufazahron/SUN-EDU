package model;

import java.time.LocalDateTime;

/**
 * Model untuk representasi data donasi
 */
public class Donasi {
    private int id;
    private int idProgram;
    private int idUser;
    private long nominal;
    private String buktiTransfer;
    private String catatanDonatur;
    private String catatanAdmin;
    private LocalDateTime tanggalDonasi;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Donasi() {
    }
    
    public Donasi(int id, int idProgram, int idUser, long nominal, 
                  String buktiTransfer, String catatanDonatur, 
                  LocalDateTime tanggalDonasi) {
        this.id = id;
        this.idProgram = idProgram;
        this.idUser = idUser;
        this.nominal = nominal;
        this.buktiTransfer = buktiTransfer;
        this.catatanDonatur = catatanDonatur;
        this.tanggalDonasi = tanggalDonasi;
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
    
    public int getIdUser() {
        return idUser;
    }
    
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }
    
    public long getNominal() {
        return nominal;
    }
    
    public void setNominal(long nominal) {
        this.nominal = nominal;
    }
    
    public String getBuktiTransfer() {
        return buktiTransfer;
    }
    
    public void setBuktiTransfer(String buktiTransfer) {
        this.buktiTransfer = buktiTransfer;
    }
    
    public String getCatatanDonatur() {
        return catatanDonatur;
    }
    
    public void setCatatanDonatur(String catatanDonatur) {
        this.catatanDonatur = catatanDonatur;
    }
    
    public String getCatatanAdmin() {
        return catatanAdmin;
    }
    
    public void setCatatanAdmin(String catatanAdmin) {
        this.catatanAdmin = catatanAdmin;
    }
    
    public LocalDateTime getTanggalDonasi() {
        return tanggalDonasi;
    }
    
    public void setTanggalDonasi(LocalDateTime tanggalDonasi) {
        this.tanggalDonasi = tanggalDonasi;
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

