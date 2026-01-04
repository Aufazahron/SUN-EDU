# Diagram Sistem Edu-Sun

Dokumen ini berisi Class Diagram, Entity Relationship Diagram (ERD), dan Diagram Relasional dari sistem Edu-Sun.

---

## 1. Class Diagram

### 1.1 Model Classes

```
┌─────────────────────────────────────────────────────────────┐
│                         MODEL LAYER                           │
└─────────────────────────────────────────────────────────────┘

┌──────────────────┐
│      User        │
├──────────────────┤
│ - id: int        │
│ - username: String│
│ - password: String│
│ - email: String  │
│ - nama: String   │
│ - telp: String   │
│ - role: String   │
│ - status: boolean│
└──────────────────┘
         ▲
         │
         │
    ┌────┴────┐
    │         │
    │         │
    ▼         ▼
┌─────────┐ ┌──────────────┐
│  Anak   │ │   Program    │
├─────────┤ ├──────────────┤
│ - id    │ │ - id         │
│ - idRelawan│ │ - idUser    │
│ - nama  │ │ - nama       │
│ - tanggalLahir│ │ - deskripsi │
│ - jenisKelamin│ │ - tempat   │
│ - alamat│ │ - kategori   │
│ - statusPendidikan│ │ - cover    │
│ - namaOrangtua│ │ - targetDonasi│
│ - noTelpOrangtua│ │ - donasiTerkumpul│
│ - createdAt│ │ - jumlahDonatur│
│ - updatedAt│ │ - tanggalPelaksanaan│
└─────────┘ │ - createdAt  │
    │       └──────────────┘
    │              │
    │              │
    ▼              ▼
┌─────────────────┐ ┌──────────────┐
│ DetailAnak      │ │   Donasi     │
├─────────────────┤ ├──────────────┤
│ - id            │ │ - id         │
│ - idAnak        │ │ - idProgram  │
│ - statusOrangtua│ │ - idUser     │
│ - namaAyah      │ │ - nominal    │
│ - namaIbu       │ │ - buktiTransfer│
│ - namaWali      │ │ - catatanDonatur│
│ - tinggalBersama│ │ - catatanAdmin│
│ - deskripsiKeluarga│ │ - tanggalDonasi│
│ - pekerjaanAyah │ │ - createdAt  │
│ - penghasilanAyah│ │ - updatedAt │
│ - pekerjaanIbu  │ └──────────────┘
│ - penghasilanIbu│
│ - pekerjaanWali │
│ - penghasilanWali│
│ - deskripsiEkonomi│
│ - sekolahTerakhir│
│ - alasanPutusSekolah│
│ - minatBelajar  │
│ - riwayatPenyakit│
│ - layananKesehatan│
│ - deskripsiKesehatan│
│ - foto          │
│ - createdAt     │
│ - updatedAt     │
└─────────────────┘

┌──────────────────────┐
│ LaporanMonitoring    │
├──────────────────────┤
│ - id                 │
│ - idAnak             │
│ - idUser             │
│ - nama               │
│ - status             │
│ - catatanRevisi      │
│ - tanggalMonitoring  │
│ - progressPendidikan│
│ - kondisiKesehatan   │
│ - catatan            │
│ - foto               │
│ - createdAt          │
│ - updatedAt          │
│ - namaAnak           │
└──────────────────────┘

┌──────────────────┐
│ LaporanProgram   │
├──────────────────┤
│ - id              │
│ - idProgram       │
│ - laporan         │
│ - dokumentasi     │
│ - tanggalPelaksanaan│
│ - createdAt       │
│ - updatedAt       │
└──────────────────┘

┌──────────┐
│  Pesan   │
├──────────┤
│ - id     │
│ - nama   │
│ - email  │
│ - subjek │
│ - pesan  │
│ - status │
│ - createdAt│
│ - updatedAt│
└──────────┘

┌──────────────┐
│  Aktivitas  │
├──────────────┤
│ - tipe      │
│ - judul     │
│ - deskripsi │
│ - waktu     │
│ - id        │
│ - namaUser  │
└──────────────┘
```

### 1.2 DAO Classes

```
┌─────────────────────────────────────────────────────────────┐
│                        DAO LAYER                             │
└─────────────────────────────────────────────────────────────┘

                    ┌──────────────┐
                    │   HomeDAO    │
                    ├──────────────┤
                    │ + getConnection()│
                    │ + closeConnection()│
                    │ + testConnection()│
                    └──────────────┘
                           ▲
                           │
            ┌──────────────┼──────────────┐
            │              │              │
            ▼              ▼              ▼
    ┌───────────┐  ┌───────────┐  ┌───────────┐
    │  UserDAO  │  │  AnakDAO  │  │ ProgramDAO│
    └───────────┘  └───────────┘  └───────────┘
            │              │              │
            │              ▼              │
            │      ┌──────────────┐       │
            │      │DetailAnakDAO │       │
            │      └──────────────┘       │
            │                              │
            ▼                              ▼
    ┌──────────────┐              ┌──────────────┐
    │  DonasiDAO   │              │LaporanProgramDAO│
    └──────────────┘              └──────────────┘
            │
            │
    ┌──────────────┐
    │LaporanMonitoringDAO│
    └──────────────┘
            │
    ┌──────────────┐
    │  PesanDAO    │
    └──────────────┘
            │
    ┌──────────────┐
    │ AktivitasDAO │
    └──────────────┘
```

### 1.3 Controller Classes

```
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                        │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────┐
│   Authentication        │
├─────────────────────────┤
│ - LoginController       │
│ - RegisterController    │
└─────────────────────────┘

┌─────────────────────────┐
│   Dashboard Controllers │
├─────────────────────────┤
│ - DashboardAdminController│
│ - DashboardDonaturController│
│ - DashboardRelawanController│
└─────────────────────────┘

┌─────────────────────────┐
│   Management Controllers │
├─────────────────────────┤
│ - AnakAdminController   │
│ - AnakController        │
│ - DetailAnakController  │
│ - DetailAnakFormModalController│
│ - ListRelawanController │
│ - ProgramAdminController│
│ - ProgramController     │
│ - ProgramPageController │
│ - ProgramDetailModalController│
│ - ProgramDetailAdminModalController│
│ - ProgramDetailPublicModalController│
│ - LaporanProgramAdminController│
│ - LaporanProgramFormModalController│
│ - ListMonitoringAdminController│
│ - ListMonitoringRelawanController│
│ - LaporanController     │
│ - FaqAdminController    │
└─────────────────────────┘

┌─────────────────────────┐
│   Public Controllers    │
├─────────────────────────┤
│ - LandingPageController │
│ - HubungiKamiController │
│ - TentangKamiController │
└─────────────────────────┘
```

---

## 2. Entity Relationship Diagram (ERD)

```
┌─────────────────────────────────────────────────────────────────────┐
│                    ENTITY RELATIONSHIP DIAGRAM                      │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐
│    USER      │
├──────────────┤
│ PK id        │
│    username  │
│    password  │
│    email     │
│    nama      │
│    telp      │
│    role      │
│    status    │
└──────────────┘
    │
    │ 1
    │
    │ N
    ├──────────────────┐
    │                  │
    │                  │
    ▼                  ▼
┌──────────────┐  ┌──────────────┐
│    ANAK      │  │   PROGRAM    │
├──────────────┤  ├──────────────┤
│ PK id        │  │ PK id        │
│ FK id_relawan│  │ FK id_user   │
│    nama      │  │    nama      │
│    tanggal_lahir│ │    deskripsi │
│    jenis_kelamin│ │    tempat   │
│    alamat    │  │    kategori  │
│    status_pendidikan│ │    cover    │
│    nama_sekolah│ │    target_donasi│
│    nama_orangtua│ │    donasi_terkumpul│
│    no_telp_orangtua│ │    jumlah_donatur│
│    created_at│  │    tanggal_pelaksanaan│
│    updated_at│  │    created_at│
└──────────────┘  │    updated_at│
    │             └──────────────┘
    │ 1                  │ 1
    │                    │
    │ N                  │ N
    ▼                    ▼
┌──────────────┐  ┌──────────────┐
│ DETAIL_ANAK  │  │   DONASI     │
├──────────────┤  ├──────────────┤
│ PK id        │  │ PK id        │
│ FK id_anak   │  │ FK id_program│
│    status_orangtua│ │ FK id_user   │
│    nama_ayah │  │    nominal   │
│    nama_ibu  │  │    bukti_transfer│
│    nama_wali │  │    catatan_donatur│
│    tinggal_bersama│ │    catatan_admin│
│    deskripsi_keluarga│ │    tanggal_donasi│
│    pekerjaan_ayah│ │    created_at│
│    penghasilan_ayah│ │    updated_at│
│    pekerjaan_ibu│ └──────────────┘
│    penghasilan_ibu│
│    pekerjaan_wali│
│    penghasilan_wali│
│    deskripsi_ekonomi│
│    sekolah_terakhir│
│    alasan_putus_sekolah│
│    minat_belajar│
│    riwayat_penyakit│
│    layanan_kesehatan│
│    deskripsi_kesehatan│
│    foto      │
│    created_at│
│    updated_at│
└──────────────┘

┌──────────────┐
│    ANAK     │
└──────────────┘
    │ 1
    │
    │ N
    ▼
┌──────────────────────┐
│ LAPORAN_MONITORING   │
├──────────────────────┤
│ PK id                 │
│ FK id_anak            │
│ FK id_user            │
│    nama               │
│    status             │
│    catatan_revisi     │
│    tanggal_monitoring │
│    progress_pendidikan│
│    kondisi_kesehatan  │
│    catatan            │
│    foto               │
│    created_at         │
│    updated_at         │
└──────────────────────┘

┌──────────────┐
│   PROGRAM    │
└──────────────┘
    │ 1
    │
    │ 0..1
    ▼
┌──────────────────┐
│ LAPORAN_PROGRAM  │
├──────────────────┤
│ PK id             │
│ FK id_program     │
│    laporan        │
│    dokumentasi    │
│    tanggal_pelaksanaan│
│    created_at     │
│    updated_at     │
└──────────────────┘

┌──────────────┐
│    PESAN     │
├──────────────┤
│ PK id        │
│    nama      │
│    email     │
│    subjek    │
│    pesan     │
│    status    │
│    created_at│
│    updated_at│
└──────────────┘
```

### Legenda ERD:
- **PK** = Primary Key
- **FK** = Foreign Key
- **1** = One
- **N** = Many
- **0..1** = Zero or One (Optional)

---

## 3. Diagram Relasional Database

```
┌─────────────────────────────────────────────────────────────────────┐
│                    RELATIONAL DATABASE DIAGRAM                        │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                         USER                                 │
├─────┬──────────────┬──────────────┬──────────────┬──────────┤
│ PK  │ username     │ password    │ email        │ nama     │
│ id  │ VARCHAR(256)│ VARCHAR(256)│ VARCHAR(128) │ VARCHAR(256)│
├─────┼──────────────┼──────────────┼──────────────┼──────────┤
│     │ telp        │ role        │ status       │          │
│     │ VARCHAR(32) │ ENUM        │ TINYINT(1)   │          │
│     │             │ (admin,     │              │          │
│     │             │  donatur,   │              │          │
│     │             │  relawan)  │              │          │
└─────┴──────────────┴──────────────┴──────────────┴──────────┘
         │
         │ 1:N
         │
         ├─────────────────────────────────────┐
         │                                     │
         │                                     │
         ▼                                     ▼
┌──────────────────────────────────┐  ┌──────────────────────────────┐
│              ANAK                │  │         PROGRAM              │
├─────┬──────────────┬────────────┤  ├─────┬──────────────┬─────────┤
│ PK  │ FK           │ nama       │  │ PK  │ FK           │ nama    │
│ id  │ id_relawan   │ VARCHAR(256)│  │ id  │ id_user      │ VARCHAR(128)│
├─────┼──────────────┼────────────┤  ├─────┼──────────────┼─────────┤
│     │ tanggal_lahir│ jenis_kelamin│ │     │ deskripsi    │ tempat  │
│     │ DATE         │ ENUM(L,P)  │  │     │ VARCHAR(256) │ VARCHAR(256)│
│     │              │            │  │     │              │         │
│     │ alamat       │ status_pendidikan│ │     │ kategori    │ cover   │
│     │ TEXT         │ ENUM       │  │     │ VARCHAR(256) │ VARCHAR(64)│
│     │              │            │  │     │              │         │
│     │ nama_sekolah │ nama_orangtua│ │     │ target_donasi│ donasi_terkumpul│
│     │ VARCHAR(256) │ VARCHAR(256)│  │     │ BIGINT       │ BIGINT  │
│     │              │            │  │     │              │         │
│     │ no_telp_orangtua│ created_at│ │     │ jumlah_donatur│ tanggal_pelaksanaan│
│     │ VARCHAR(32)  │ DATETIME   │  │     │ INT          │ DATE    │
│     │              │            │  │     │              │         │
│     │ updated_at   │            │  │     │ created_at   │ updated_at│
│     │ DATETIME     │            │  │     │ DATETIME     │ DATETIME│
└─────┴──────────────┴────────────┘  └─────┴──────────────┴─────────┘
         │                                      │
         │ 1:1                                  │ 1:N
         │                                      │
         ▼                                      ▼
┌──────────────────────────────────┐  ┌──────────────────────────────┐
│         DETAIL_ANAK              │  │         DONASI              │
├─────┬──────────────┬────────────┤  ├─────┬──────────────┬─────────┤
│ PK  │ FK           │ status_orangtua│ │ PK  │ FK           │ FK      │
│ id  │ id_anak      │ ENUM       │  │ id  │ id_program   │ id_user │
├─────┼──────────────┼────────────┤  ├─────┼──────────────┼─────────┤
│     │ nama_ayah    │ nama_ibu   │  │     │ nominal      │ bukti_transfer│
│     │ VARCHAR(256)│ VARCHAR(256)│  │     │ BIGINT       │ VARCHAR(128)│
│     │              │            │  │     │              │         │
│     │ nama_wali    │ tinggal_bersama│ │     │ catatan_donatur│ catatan_admin│
│     │ VARCHAR(256) │ VARCHAR(100)│  │     │ TEXT         │ TEXT    │
│     │              │            │  │     │              │         │
│     │ deskripsi_keluarga│ pekerjaan_ayah│ │     │ tanggal_donasi│ created_at│
│     │ TEXT         │ VARCHAR(256)│  │     │ DATETIME     │ DATETIME│
│     │              │            │  │     │              │         │
│     │ penghasilan_ayah│ pekerjaan_ibu│ │     │ updated_at │         │
│     │ BIGINT       │ VARCHAR(256)│  │     │ DATETIME     │         │
│     │              │            │  │     │              │         │
│     │ penghasilan_ibu│ pekerjaan_wali│ │     │              │         │
│     │ BIGINT       │ VARCHAR(256)│  │     │              │         │
│     │              │            │  │     │              │         │
│     │ penghasilan_wali│ sekolah_terakhir│ │     │              │         │
│     │ BIGINT       │ VARCHAR(256)│  │     │              │         │
│     │              │            │  │     │              │         │
│     │ deskripsi_ekonomi│ alasan_putus_sekolah│ │     │              │         │
│     │ TEXT         │ TEXT       │  │     │              │         │
│     │              │            │  │     │              │         │
│     │ minat_belajar│ riwayat_penyakit│ │     │              │         │
│     │ TEXT         │ TEXT       │  │     │              │         │
│     │              │            │  │     │              │         │
│     │ layanan_kesehatan│ deskripsi_kesehatan│ │     │              │         │
│     │ VARCHAR(256) │ TEXT       │  │     │              │         │
│     │              │            │  │     │              │         │
│     │ foto         │ created_at │  │     │              │         │
│     │ VARCHAR(256) │ DATETIME   │  │     │              │         │
│     │              │            │  │     │              │         │
│     │ updated_at   │            │  │     │              │         │
│     │ DATETIME     │            │  │     │              │         │
└─────┴──────────────┴────────────┘  └─────┴──────────────┴─────────┘

┌──────────────────────────────────┐
│     LAPORAN_MONITORING           │
├─────┬──────────────┬────────────┤
│ PK  │ FK           │ FK        │
│ id  │ id_anak      │ id_user   │
├─────┼──────────────┼────────────┤
│     │ nama         │ status    │
│     │ VARCHAR(256) │ VARCHAR(50)│
│     │              │           │
│     │ catatan_revisi│ tanggal_monitoring│
│     │ TEXT         │ DATE      │
│     │              │           │
│     │ progress_pendidikan│ kondisi_kesehatan│
│     │ TEXT         │ TEXT      │
│     │              │           │
│     │ catatan      │ foto      │
│     │ TEXT         │ VARCHAR(256)│
│     │              │           │
│     │ created_at   │ updated_at│
│     │ DATETIME     │ DATETIME  │
└─────┴──────────────┴────────────┘

┌──────────────────────────────────┐
│      LAPORAN_PROGRAM             │
├─────┬──────────────┬────────────┤
│ PK  │ FK           │ laporan   │
│ id  │ id_program   │ TEXT      │
├─────┼──────────────┼────────────┤
│     │ dokumentasi  │ tanggal_pelaksanaan│
│     │ VARCHAR(256) │ DATE      │
│     │              │           │
│     │ created_at   │ updated_at│
│     │ DATETIME     │ DATETIME  │
└─────┴──────────────┴────────────┘

┌──────────────────────────────────┐
│           PESAN                  │
├─────┬──────────────┬────────────┤
│ PK  │ nama         │ email     │
│ id  │ VARCHAR(256) │ VARCHAR(256)│
├─────┼──────────────┼────────────┤
│     │ subjek       │ pesan     │
│     │ VARCHAR(256) │ TEXT      │
│     │              │           │
│     │ status       │ created_at│
│     │ ENUM         │ DATETIME  │
│     │ (baru,       │           │
│     │  dibaca,     │           │
│     │  dijawab)    │           │
│     │              │           │
│     │ updated_at   │           │
│     │ DATETIME     │           │
└─────┴──────────────┴────────────┘
```

### Relasi Database:

1. **USER → ANAK** (1:N)
   - Satu user (relawan) dapat memiliki banyak anak yang didampingi
   - Foreign Key: `anak.id_relawan` → `user.id`

2. **USER → PROGRAM** (1:N)
   - Satu user (admin) dapat membuat banyak program
   - Foreign Key: `program.id_user` → `user.id`

3. **USER → DONASI** (1:N)
   - Satu user (donatur) dapat melakukan banyak donasi
   - Foreign Key: `donasi.id_user` → `user.id`

4. **USER → LAPORAN_MONITORING** (1:N)
   - Satu user (relawan) dapat membuat banyak laporan monitoring
   - Foreign Key: `laporan_monitoring.id_user` → `user.id`

5. **ANAK → DETAIL_ANAK** (1:1)
   - Satu anak memiliki satu detail lengkap
   - Foreign Key: `detail_anak.id_anak` → `anak.id`

6. **ANAK → LAPORAN_MONITORING** (1:N)
   - Satu anak dapat memiliki banyak laporan monitoring
   - Foreign Key: `laporan_monitoring.id_anak` → `anak.id`

7. **PROGRAM → DONASI** (1:N)
   - Satu program dapat menerima banyak donasi
   - Foreign Key: `donasi.id_program` → `program.id`

8. **PROGRAM → LAPORAN_PROGRAM** (1:0..1)
   - Satu program dapat memiliki maksimal satu laporan (setelah selesai)
   - Foreign Key: `laporan_program.id_program` → `program.id`

---

## 4. Ringkasan Tabel Database

| No | Nama Tabel | Primary Key | Foreign Keys | Deskripsi |
|----|------------|-------------|--------------|-----------|
| 1 | `user` | `id` | - | Tabel untuk menyimpan data semua user (admin, donatur, relawan) |
| 2 | `anak` | `id` | `id_relawan` → `user.id` | Tabel data anak yang didampingi relawan |
| 3 | `detail_anak` | `id` | `id_anak` → `anak.id` | Tabel detail lengkap informasi anak |
| 4 | `program` | `id` | `id_user` → `user.id` | Tabel program kerja yang dapat didonasi |
| 5 | `donasi` | `id` | `id_program` → `program.id`<br>`id_user` → `user.id` | Tabel transaksi donasi |
| 6 | `laporan_monitoring` | `id` | `id_anak` → `anak.id`<br>`id_user` → `user.id` | Tabel laporan monitoring progress anak |
| 7 | `laporan_program` | `id` | `id_program` → `program.id` | Tabel laporan program kerja yang sudah selesai |
| 8 | `pesan` | `id` | - | Tabel pesan dari form "Hubungi Kami" |

---

## 5. Arsitektur Sistem

```
┌─────────────────────────────────────────────────────────────┐
│                      PRESENTATION LAYER                      │
│                    (JavaFX FXML Views)                       │
├─────────────────────────────────────────────────────────────┤
│  - LandingPage.fxml                                         │
│  - Login.fxml, Register.fxml                                │
│  - DashboardAdmin.fxml, DashboardDonatur.fxml,              │
│    DashboardRelawan.fxml                                    │
│  - AnakView.fxml, AnakAdminView.fxml                         │
│  - ProgramPage.fxml, ProgramAdminView.fxml                   │
│  - ListMonitoringRelawan.fxml, ListMonitoringAdmin.fxml      │
│  - Dan lainnya...                                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER LAYER                       │
│                  (JavaFX Controllers)                        │
├─────────────────────────────────────────────────────────────┤
│  - LoginController, RegisterController                       │
│  - DashboardAdminController, DashboardDonaturController,    │
│    DashboardRelawanController                               │
│  - AnakController, AnakAdminController                      │
│  - ProgramController, ProgramAdminController                 │
│  - Dan lainnya...                                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                        DAO LAYER                             │
│                  (Data Access Objects)                       │
├─────────────────────────────────────────────────────────────┤
│  - HomeDAO (Base class)                                     │
│  - UserDAO, AnakDAO, DetailAnakDAO                          │
│  - ProgramDAO, DonasiDAO                                    │
│  - LaporanMonitoringDAO, LaporanProgramDAO                   │
│  - PesanDAO, AktivitasDAO                                   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                        MODEL LAYER                           │
│                    (Entity Classes)                          │
├─────────────────────────────────────────────────────────────┤
│  - User, Anak, DetailAnak                                   │
│  - Program, Donasi                                           │
│  - LaporanMonitoring, LaporanProgram                        │
│  - Pesan, Aktivitas                                         │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE LAYER                          │
│                    (MySQL Database)                          │
├─────────────────────────────────────────────────────────────┤
│  Database: edu-sun                                           │
│  - user, anak, detail_anak                                  │
│  - program, donasi                                          │
│  - laporan_monitoring, laporan_program                       │
│  - pesan                                                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 6. Role dan Akses

### 6.1 Admin
- Mengelola data anak
- Mengelola data relawan
- Mengelola program kerja
- Melihat dan mengelola laporan monitoring
- Melihat dan mengelola laporan program
- Mengelola pesan/FAQ
- Melihat dashboard dengan statistik

### 6.2 Donatur
- Melihat program kerja tersedia
- Melakukan donasi
- Melihat program yang sudah didukung
- Melihat hasil program kerja (laporan)
- Melihat dashboard dengan statistik donasi

### 6.3 Relawan
- Mengelola data anak yang didampingi
- Membuat laporan monitoring
- Melihat dashboard dengan statistik anak dampingan

---

## 7. Catatan Penting

1. **Foreign Key Constraints**: Semua foreign key menggunakan `ON DELETE CASCADE` dan `ON UPDATE CASCADE` untuk menjaga integritas data.

2. **Status Fields**:
   - `user.status`: Boolean untuk status aktif/nonaktif user
   - `anak.status_pendidikan`: Enum untuk status pendidikan anak
   - `laporan_monitoring.status`: Status laporan (Draft, Diajukan, Disetujui, Dikembalikan)
   - `pesan.status`: Status pesan (baru, dibaca, dijawab)
   - `program.status_program`: Status program (draft, aktif, selesai, dibatalkan)

3. **Timestamps**: Semua tabel memiliki `created_at` dan `updated_at` untuk tracking waktu.

4. **File Storage**: File seperti foto, bukti transfer, dan dokumentasi disimpan sebagai path string di database.

---

**Dokumen ini dibuat untuk dokumentasi sistem Edu-Sun**
**Terakhir diperbarui: Desember 2025**

