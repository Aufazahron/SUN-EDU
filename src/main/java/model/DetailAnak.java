package model;

import java.time.LocalDateTime;

/**
 * Model untuk representasi detail lengkap data anak
 */
public class DetailAnak {
    private int id;
    private int idAnak;
    
    // Informasi Orang Tua/Wali
    private String statusOrangtua; // 'Ayah Ibu', 'Ayah', 'Ibu', 'Wali'
    private String namaAyah;
    private String namaIbu;
    private String namaWali;
    private String tinggalBersama;
    private String deskripsiKeluarga;
    
    // Kondisi Ekonomi
    private String pekerjaanAyah;
    private Long penghasilanAyah;
    private String pekerjaanIbu;
    private Long penghasilanIbu;
    private String pekerjaanWali;
    private Long penghasilanWali;
    private String deskripsiEkonomi;
    
    // Kondisi Pendidikan
    private String sekolahTerakhir;
    private String alasanPutusSekolah;
    private String minatBelajar;
    
    // Kondisi Kesehatan
    private String riwayatPenyakit;
    private String layananKesehatan;
    private String deskripsiKesehatan;
    
    // Foto
    private String foto;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DetailAnak() {
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
    
    public String getStatusOrangtua() {
        return statusOrangtua;
    }
    
    public void setStatusOrangtua(String statusOrangtua) {
        this.statusOrangtua = statusOrangtua;
    }
    
    public String getNamaAyah() {
        return namaAyah;
    }
    
    public void setNamaAyah(String namaAyah) {
        this.namaAyah = namaAyah;
    }
    
    public String getNamaIbu() {
        return namaIbu;
    }
    
    public void setNamaIbu(String namaIbu) {
        this.namaIbu = namaIbu;
    }
    
    public String getNamaWali() {
        return namaWali;
    }
    
    public void setNamaWali(String namaWali) {
        this.namaWali = namaWali;
    }
    
    public String getTinggalBersama() {
        return tinggalBersama;
    }
    
    public void setTinggalBersama(String tinggalBersama) {
        this.tinggalBersama = tinggalBersama;
    }
    
    public String getDeskripsiKeluarga() {
        return deskripsiKeluarga;
    }
    
    public void setDeskripsiKeluarga(String deskripsiKeluarga) {
        this.deskripsiKeluarga = deskripsiKeluarga;
    }
    
    public String getPekerjaanAyah() {
        return pekerjaanAyah;
    }
    
    public void setPekerjaanAyah(String pekerjaanAyah) {
        this.pekerjaanAyah = pekerjaanAyah;
    }
    
    public Long getPenghasilanAyah() {
        return penghasilanAyah;
    }
    
    public void setPenghasilanAyah(Long penghasilanAyah) {
        this.penghasilanAyah = penghasilanAyah;
    }
    
    public String getPekerjaanIbu() {
        return pekerjaanIbu;
    }
    
    public void setPekerjaanIbu(String pekerjaanIbu) {
        this.pekerjaanIbu = pekerjaanIbu;
    }
    
    public Long getPenghasilanIbu() {
        return penghasilanIbu;
    }
    
    public void setPenghasilanIbu(Long penghasilanIbu) {
        this.penghasilanIbu = penghasilanIbu;
    }
    
    public String getPekerjaanWali() {
        return pekerjaanWali;
    }
    
    public void setPekerjaanWali(String pekerjaanWali) {
        this.pekerjaanWali = pekerjaanWali;
    }
    
    public Long getPenghasilanWali() {
        return penghasilanWali;
    }
    
    public void setPenghasilanWali(Long penghasilanWali) {
        this.penghasilanWali = penghasilanWali;
    }
    
    public String getDeskripsiEkonomi() {
        return deskripsiEkonomi;
    }
    
    public void setDeskripsiEkonomi(String deskripsiEkonomi) {
        this.deskripsiEkonomi = deskripsiEkonomi;
    }
    
    public String getSekolahTerakhir() {
        return sekolahTerakhir;
    }
    
    public void setSekolahTerakhir(String sekolahTerakhir) {
        this.sekolahTerakhir = sekolahTerakhir;
    }
    
    public String getAlasanPutusSekolah() {
        return alasanPutusSekolah;
    }
    
    public void setAlasanPutusSekolah(String alasanPutusSekolah) {
        this.alasanPutusSekolah = alasanPutusSekolah;
    }
    
    public String getMinatBelajar() {
        return minatBelajar;
    }
    
    public void setMinatBelajar(String minatBelajar) {
        this.minatBelajar = minatBelajar;
    }
    
    public String getRiwayatPenyakit() {
        return riwayatPenyakit;
    }
    
    public void setRiwayatPenyakit(String riwayatPenyakit) {
        this.riwayatPenyakit = riwayatPenyakit;
    }
    
    public String getLayananKesehatan() {
        return layananKesehatan;
    }
    
    public void setLayananKesehatan(String layananKesehatan) {
        this.layananKesehatan = layananKesehatan;
    }
    
    public String getDeskripsiKesehatan() {
        return deskripsiKesehatan;
    }
    
    public void setDeskripsiKesehatan(String deskripsiKesehatan) {
        this.deskripsiKesehatan = deskripsiKesehatan;
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
}

