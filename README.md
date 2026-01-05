# ☀️ SunEdu — Gerakan untuk Masa Depan Pendidikan

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Build-Maven-red.svg)](https://maven.apache.org/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-blue.svg)](https://www.mysql.com/)

**SunEdu** adalah aplikasi desktop berbasis **JavaFX** yang dirancang untuk mendukung **pendataan anak putus sekolah dan pengelolaan donasi pendidikan** secara terstruktur, transparan, dan berkelanjutan.

Aplikasi ini menghubungkan **Admin NGO, Relawan lapangan, dan Donatur** dalam satu sistem terintegrasi untuk memastikan setiap bantuan tepat sasaran dan dapat dipertanggungjawabkan.

---

## 🎯 Tujuan Sistem

- Menyediakan basis data terpusat anak putus sekolah
- Mendukung monitoring pendidikan dan kesehatan secara berkala
- Meningkatkan transparansi donasi dan program sosial
- Meminimalkan kehilangan data dan duplikasi laporan lapangan

---

## 🚀 Fitur Utama

### 👥 Multi-Role Ecosystem (RBAC)
- **Admin NGO**: Manajemen data, program, dan validasi laporan
- **Relawan**: Pendataan anak dan laporan monitoring
- **Donatur**: Donasi dan akses laporan transparansi

### 📊 Pendataan Anak Terstruktur
- Identitas dan riwayat pendidikan
- Kondisi ekonomi keluarga
- Riwayat kesehatan dan minat anak

### 📈 Dashboard & Monitoring
- Statistik status anak
- Progress donasi real-time
- Log aktivitas dan laporan berkala

---

## 🧱 Arsitektur Sistem

```
Presentation Layer  → JavaFX (FXML)
Controller Layer    → JavaFX Controllers
DAO Layer           → Database Access Objects
Model Layer         → Entity / POJO
Database Layer      → MySQL
```

---

## 🛠️ Tech Stack

| Layer | Teknologi |
|------|----------|
| Language | Java 17 |
| UI | JavaFX 21.0.1 |
| Build Tool | Maven |
| Database | MySQL |
| Pattern | MVC + DAO |

---

## 📂 Struktur Proyek

```
edu-sun/
├── src/main/java/
│   ├── controller/
│   ├── dao/
│   ├── model/
│   ├── util/
│   └── main/
└── src/main/resources/
    ├── view/
    └── assets/
```

---

## ⚙️ Instalasi & Menjalankan

```bash
mvn clean install
mvn javafx:run
```

---

## 👨‍👩‍👧‍👦 Tim Pengembang
- Aulia Aufa Z.
- Bayu Aji W.
- Daffa Jiyadi S.
- Kaylla Asyifa
- Rafli Maulana
- Roven Angger D.

---

### 🌱 SunEdu — Empowering Education, Brighter Future
