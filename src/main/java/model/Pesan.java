package model;

import java.time.LocalDateTime;

/**
 * Model untuk representasi data pesan dari form "Hubungi Kami"
 */
public class Pesan {
    private int id;
    private String nama;
    private String email;
    private String subjek;
    private String pesan;
    private String status; // baru, dibaca, dijawab
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Pesan() {
    }
    
    public Pesan(String nama, String email, String subjek, String pesan) {
        this.nama = nama;
        this.email = email;
        this.subjek = subjek;
        this.pesan = pesan;
        this.status = "baru";
    }
    
    public Pesan(int id, String nama, String email, String subjek, String pesan, 
                 String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.subjek = subjek;
        this.pesan = pesan;
        this.status = status;
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
    
    public String getNama() {
        return nama;
    }
    
    public void setNama(String nama) {
        this.nama = nama;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSubjek() {
        return subjek;
    }
    
    public void setSubjek(String subjek) {
        this.subjek = subjek;
    }
    
    public String getPesan() {
        return pesan;
    }
    
    public void setPesan(String pesan) {
        this.pesan = pesan;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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

