-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 03 Jan 2026 pada 13.39
-- Versi server: 10.4.32-MariaDB
-- Versi PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `edu-sun`
--

-- --------------------------------------------------------

--
-- Struktur dari tabel `anak`
--

CREATE TABLE `anak` (
  `id` int(11) NOT NULL,
  `id_relawan` int(11) NOT NULL COMMENT 'ID relawan yang mendampingi anak (FK ke user.id)',
  `nama` varchar(256) NOT NULL COMMENT 'Nama lengkap anak',
  `tanggal_lahir` date DEFAULT NULL COMMENT 'Tanggal lahir anak',
  `jenis_kelamin` enum('L','P') DEFAULT NULL COMMENT 'Jenis kelamin: L=Laki-laki, P=Perempuan',
  `alamat` text NOT NULL COMMENT 'Alamat lengkap anak',
  `status` enum('Rentan','Dalam Pemantauan','Stabil','Diajukan','Selesai') DEFAULT NULL COMMENT 'Status pendidikan: SD, SMP, atau SMA',
  `nama_orangtua` varchar(256) NOT NULL COMMENT 'Nama orangtua/wali anak',
  `no_telp_orangtua` varchar(32) NOT NULL COMMENT 'Nomor telepon orangtua/wali',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'Waktu data dibuat',
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'Waktu data terakhir diupdate'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Tabel data anak yang didampingi relawan';

--
-- Dumping data untuk tabel `anak`
--

INSERT INTO `anak` (`id`, `id_relawan`, `nama`, `tanggal_lahir`, `jenis_kelamin`, `alamat`, `status`, `nama_orangtua`, `no_telp_orangtua`, `created_at`, `updated_at`) VALUES
(1, 8, 'awdawdawdawdaw', '2026-01-01', 'L', 'dwadawdaw', '', 'dwadawdawd', '080808080808', '2026-01-02 22:29:36', '2026-01-03 12:17:13'),
(2, 8, 'Siti Nurhalizaa', '2015-05-15', 'P', 'Jl. Merdeka No. 123, Jakarta Pusat', 'Rentan', 'Ahmad Nurhaliza', '081111111111', '2026-01-02 22:30:31', '2026-01-03 12:23:46'),
(3, 8, 'Andi Pratama', '2010-08-20', 'L', 'Jl. Sudirman No. 456, Bandung', '', 'Budi Pratama', '082222222222', '2026-01-02 22:30:31', '2026-01-03 12:17:13'),
(4, 8, 'Rina Sari', '2007-03-10', 'P', 'Jl. Gatot Subroto No. 789, Surabaya', '', 'Sari Wati', '083333333333', '2026-01-02 22:30:31', '2026-01-03 12:17:13'),
(5, 8, 'Doni Kurniawan', '2016-02-14', 'L', 'Jl. Diponegoro No. 321, Yogyakarta', '', 'Kurniawan', '084444444444', '2026-01-02 22:30:31', '2026-01-03 12:17:13'),
(6, 8, 'Maya Indira', '2009-11-25', 'P', 'Jl. Ahmad Yani No. 654, Semarang', '', 'Indira Sari', '085555555555', '2026-01-02 22:30:31', '2026-01-03 12:17:13'),
(7, 8, 'Bapia', '2026-01-03', 'P', 'Uhuw', 'Stabil', 'dawdaw', '0808080', '2026-01-03 15:46:55', '2026-01-03 16:56:51');

-- --------------------------------------------------------

--
-- Struktur dari tabel `detail_anak`
--

CREATE TABLE `detail_anak` (
  `id` int(11) NOT NULL,
  `id_anak` int(11) NOT NULL COMMENT 'Foreign Key ke tabel anak.id',
  `status_orangtua` enum('Ayah Ibu','Ayah','Ibu','Wali') DEFAULT NULL COMMENT 'Status orangtua',
  `nama_ayah` varchar(256) DEFAULT NULL COMMENT 'Nama ayah',
  `nama_ibu` varchar(256) DEFAULT NULL COMMENT 'Nama ibu',
  `nama_wali` varchar(256) DEFAULT NULL COMMENT 'Nama wali (jika ada)',
  `tinggal_bersama` varchar(100) DEFAULT NULL COMMENT 'Tinggal bersama siapa',
  `deskripsi_keluarga` text DEFAULT NULL COMMENT 'Deskripsi kondisi keluarga',
  `pekerjaan_ayah` varchar(256) DEFAULT NULL COMMENT 'Pekerjaan ayah',
  `penghasilan_ayah` bigint(20) DEFAULT NULL COMMENT 'Penghasilan ayah per bulan',
  `pekerjaan_ibu` varchar(256) DEFAULT NULL COMMENT 'Pekerjaan ibu',
  `penghasilan_ibu` bigint(20) DEFAULT NULL COMMENT 'Penghasilan ibu per bulan',
  `pekerjaan_wali` varchar(256) DEFAULT NULL COMMENT 'Pekerjaan wali',
  `penghasilan_wali` bigint(20) DEFAULT NULL COMMENT 'Penghasilan wali per bulan',
  `deskripsi_ekonomi` text DEFAULT NULL COMMENT 'Deskripsi kondisi ekonomi',
  `sekolah_terakhir` varchar(256) DEFAULT NULL COMMENT 'Sekolah terakhir yang pernah diikuti',
  `alasan_putus_sekolah` text DEFAULT NULL COMMENT 'Alasan putus sekolah (jika ada)',
  `minat_belajar` text DEFAULT NULL COMMENT 'Minat belajar anak',
  `riwayat_penyakit` text DEFAULT NULL COMMENT 'Riwayat penyakit yang pernah diderita',
  `layanan_kesehatan` varchar(256) DEFAULT NULL COMMENT 'Layanan kesehatan yang digunakan',
  `deskripsi_kesehatan` text DEFAULT NULL COMMENT 'Deskripsi kondisi kesehatan',
  `foto` varchar(512) DEFAULT NULL COMMENT 'Path/nama file foto anak',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'Waktu data dibuat',
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'Waktu data terakhir diupdate'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Tabel detail lengkap informasi anak';

--
-- Dumping data untuk tabel `detail_anak`
--

INSERT INTO `detail_anak` (`id`, `id_anak`, `status_orangtua`, `nama_ayah`, `nama_ibu`, `nama_wali`, `tinggal_bersama`, `deskripsi_keluarga`, `pekerjaan_ayah`, `penghasilan_ayah`, `pekerjaan_ibu`, `penghasilan_ibu`, `pekerjaan_wali`, `penghasilan_wali`, `deskripsi_ekonomi`, `sekolah_terakhir`, `alasan_putus_sekolah`, `minat_belajar`, `riwayat_penyakit`, `layanan_kesehatan`, `deskripsi_kesehatan`, `foto`, `created_at`, `updated_at`) VALUES
(1, 2, 'Ayah', 'ada', '', '', '', '', '', NULL, '', NULL, '', NULL, '', '', '', '', '', '', '', 'foto_anak_2_1767414582285_Screenshot 2025-12-04 113638.png', '2026-01-03 10:54:05', '2026-01-03 11:48:54');

-- --------------------------------------------------------

--
-- Struktur dari tabel `donasi`
--

CREATE TABLE `donasi` (
  `id` int(11) NOT NULL,
  `id_program` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `nominal` bigint(20) NOT NULL,
  `bukti_transfer` varchar(128) DEFAULT NULL COMMENT 'File bukti transfer',
  `catatan_donatur` text DEFAULT NULL COMMENT 'Pesan/doa dari donatur',
  `catatan_admin` text DEFAULT NULL COMMENT 'Catatan dari admin',
  `tanggal_donasi` datetime NOT NULL DEFAULT current_timestamp(),
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `donasi`
--

INSERT INTO `donasi` (`id`, `id_program`, `id_user`, `nominal`, `bukti_transfer`, `catatan_donatur`, `catatan_admin`, `tanggal_donasi`, `created_at`, `updated_at`) VALUES
(1, 2, 2, 50000, NULL, 'Aamiin', NULL, '2025-12-04 11:18:54', '2025-12-04 11:18:54', NULL),
(2, 3, 2, 5000000, 'bukti_1764825679829_Frame 6 - Dahboard Donatur.png', 'Lancar Raya Karyanya', NULL, '2025-12-04 12:21:19', '2025-12-04 12:21:19', NULL),
(3, 3, 3, 50000, NULL, '', NULL, '2025-12-15 12:58:50', '2025-12-15 12:58:50', NULL),
(4, 2, 3, 50000, NULL, '', NULL, '2025-12-15 13:05:44', '2025-12-15 13:05:44', NULL),
(5, 2, 3, 200000, NULL, '', NULL, '2025-12-15 13:26:03', '2025-12-15 13:26:03', NULL),
(6, 2, 3, 500000, NULL, '', NULL, '2025-12-16 11:07:41', '2025-12-16 11:07:41', NULL),
(7, 1, 3, 500000, NULL, 'Aamiin', NULL, '2026-01-03 13:25:51', '2026-01-03 13:25:51', NULL),
(8, 3, 3, 50000, NULL, '', NULL, '2026-01-03 18:25:33', '2026-01-03 18:25:33', NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `laporan_monitoring`
--

CREATE TABLE `laporan_monitoring` (
  `id` int(11) NOT NULL,
  `id_anak` int(11) NOT NULL COMMENT 'ID anak yang dimonitoring (FK ke anak.id)',
  `id_user` int(11) NOT NULL COMMENT 'ID relawan yang membuat laporan (FK ke user.id)',
  `nama` varchar(256) DEFAULT NULL COMMENT 'Nama/judul laporan monitoring',
  `status` enum('Draft','Diajukan','Disetujui','Dikembalikan') DEFAULT 'Draft' COMMENT 'Status laporan: Draft, Diajukan, Disetujui, atau Dikembalikan',
  `catatan_revisi` text DEFAULT NULL COMMENT 'Catatan revisi dari admin ketika laporan dikembalikan',
  `tanggal_monitoring` date NOT NULL COMMENT 'Tanggal monitoring dilakukan',
  `progress_pendidikan` text DEFAULT NULL COMMENT 'Progress pendidikan anak',
  `kondisi_kesehatan` text DEFAULT NULL COMMENT 'Kondisi kesehatan anak',
  `catatan` text DEFAULT NULL COMMENT 'Catatan tambahan dari relawan',
  `foto` varchar(256) DEFAULT NULL COMMENT 'Path/file foto monitoring (opsional)',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'Waktu laporan dibuat',
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'Waktu laporan terakhir diupdate'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Tabel laporan monitoring progress anak';

--
-- Dumping data untuk tabel `laporan_monitoring`
--

INSERT INTO `laporan_monitoring` (`id`, `id_anak`, `id_user`, `nama`, `status`, `catatan_revisi`, `tanggal_monitoring`, `progress_pendidikan`, `kondisi_kesehatan`, `catatan`, `foto`, `created_at`, `updated_at`) VALUES
(1, 2, 8, NULL, 'Diajukan', NULL, '2026-01-02', 'Anak sudah bisa membaca dan menulis dengan baik. Nilai matematika meningkat dari 60 menjadi 75. Aktif mengikuti kegiatan ekstrakurikuler.', 'Kondisi kesehatan baik. Tidak ada keluhan sakit. Imunisasi sudah lengkap.', 'Anak sangat antusias belajar. Orangtua sangat kooperatif dan mendukung program.', NULL, '2026-01-02 22:30:31', '2026-01-03 15:22:23'),
(2, 3, 8, NULL, 'Draft', NULL, '2025-12-26', 'Progress baik, nilai rata-rata meningkat. Masih perlu bimbingan untuk pelajaran matematika.', 'Kondisi sehat, rutin check-up. Tidak ada masalah kesehatan.', 'Anak mulai menunjukkan minat pada olahraga. Perlu dukungan untuk mengembangkan bakat.', NULL, '2026-01-02 22:30:31', '2026-01-02 22:31:33'),
(3, 4, 8, NULL, 'Draft', NULL, '2025-12-19', 'Persiapan ujian berjalan lancar. Nilai ujian tryout menunjukkan peningkatan signifikan.', 'Kondisi kesehatan prima. Aktif dalam kegiatan fisik.', 'Anak sangat termotivasi untuk melanjutkan ke perguruan tinggi. Perlu bimbingan konseling karir.', NULL, '2026-01-02 22:30:31', '2026-01-02 22:31:35'),
(4, 1, 8, NULL, 'Diajukan', NULL, '2026-01-02', 'Jadi gini', 'Aman', 'Lanjut', NULL, '2026-01-02 22:31:09', '2026-01-03 14:08:35'),
(5, 2, 8, 'Siti Nurhalizaa - 2026-01-03', 'Disetujui', NULL, '2026-01-03', 'oywkkwkw', 'uhy', 'dwad', 'foto_1767419400667_IMG_0970.JPG', '2026-01-03 12:50:00', '2026-01-03 14:19:37');

-- --------------------------------------------------------

--
-- Struktur dari tabel `laporan_program`
--

CREATE TABLE `laporan_program` (
  `id` int(11) NOT NULL,
  `id_program` int(11) NOT NULL COMMENT 'Foreign Key ke tabel program.id',
  `laporan` text NOT NULL COMMENT 'Isi laporan pelaksanaan program',
  `dokumentasi` varchar(256) DEFAULT NULL COMMENT 'Path/file dokumentasi (foto, dokumen, dll)',
  `tanggal_pelaksanaan` date DEFAULT NULL COMMENT 'Tanggal pelaksanaan program',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'Waktu laporan dibuat',
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'Waktu laporan terakhir diupdate'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Tabel laporan program kerja yang sudah selesai';

--
-- Dumping data untuk tabel `laporan_program`
--

INSERT INTO `laporan_program` (`id`, `id_program`, `laporan`, `dokumentasi`, `tanggal_pelaksanaan`, `created_at`, `updated_at`) VALUES
(1, 3, 'Bagus', 'dokumentasi_3_1767437900028_IMG_0970.JPG', '2026-01-03', '2026-01-03 17:58:20', NULL);

-- --------------------------------------------------------

--
-- Struktur dari tabel `pesan`
--

CREATE TABLE `pesan` (
  `id` int(11) NOT NULL,
  `nama` varchar(256) NOT NULL COMMENT 'Nama pengirim pesan',
  `email` varchar(256) NOT NULL COMMENT 'Email pengirim pesan',
  `subjek` varchar(256) NOT NULL COMMENT 'Subjek pesan',
  `pesan` text NOT NULL COMMENT 'Isi pesan',
  `status` enum('baru','dibaca','dijawab') NOT NULL DEFAULT 'baru' COMMENT 'Status pesan: baru, dibaca, atau dijawab',
  `created_at` datetime NOT NULL DEFAULT current_timestamp() COMMENT 'Waktu pesan dibuat',
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT 'Waktu pesan diupdate'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `pesan`
--

INSERT INTO `pesan` (`id`, `nama`, `email`, `subjek`, `pesan`, `status`, `created_at`, `updated_at`) VALUES
(1, 'Asep', 'as@upi.edu', 'Halo', 'halooooooooo', 'dijawab', '2026-01-03 19:30:58', '2026-01-03 19:31:34');

-- --------------------------------------------------------

--
-- Struktur dari tabel `program`
--

CREATE TABLE `program` (
  `id` int(11) NOT NULL,
  `id_user` int(11) NOT NULL,
  `nama` varchar(128) NOT NULL,
  `deskripsi` varchar(256) NOT NULL,
  `tempat` varchar(256) NOT NULL,
  `kategori` enum('Pendidikan','Pengembangan Skill','Pendampingan') DEFAULT NULL COMMENT 'Kategori program: Pendidikan, Pengembangan Skill, atau Pendampingan',
  `cover` varchar(64) NOT NULL,
  `biaya_program` bigint(20) DEFAULT NULL,
  `target_donasi` bigint(20) DEFAULT 0,
  `donasi_terkumpul` bigint(20) DEFAULT 0,
  `jumlah_donatur` int(11) DEFAULT 0,
  `status_program` enum('draft','aktif','selesai','dibatalkan') DEFAULT 'draft',
  `tanggal_pelaksanaan` date DEFAULT NULL,
  `tanggal_mulai_donasi` date DEFAULT NULL,
  `tanggal_selesai_donasi` date DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `program`
--

INSERT INTO `program` (`id`, `id_user`, `nama`, `deskripsi`, `tempat`, `kategori`, `cover`, `biaya_program`, `target_donasi`, `donasi_terkumpul`, `jumlah_donatur`, `status_program`, `tanggal_pelaksanaan`, `tanggal_mulai_donasi`, `tanggal_selesai_donasi`, `created_at`, `updated_at`) VALUES
(1, 3, 'MemBAYUkan Semua', 'mendinginkan suasana', 'Aula Awan Kinton', 'Pendampingan', 'bayu.jpg', NULL, 500000, 500000, 1, 'draft', NULL, NULL, NULL, '2025-11-20 11:46:26', '2026-01-03 13:25:51'),
(2, 1, 'Monev', 'Triwulan 3', 'Balai Desa', 'Pengembangan Skill', 'proker.jpeg', NULL, 500000, 800000, 4, 'draft', NULL, NULL, NULL, '2025-11-20 12:04:20', '2026-01-03 13:21:07'),
(3, 4, 'RAYAKARYA', 'LOMBA', 'KAMPUS', 'Pendidikan', 'wkwk.png', NULL, 5000000, 5100000, 3, 'draft', NULL, NULL, NULL, '2025-11-20 12:09:33', '2026-01-03 18:25:33');

-- --------------------------------------------------------

--
-- Struktur dari tabel `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(256) NOT NULL,
  `password` varchar(256) NOT NULL,
  `email` varchar(128) NOT NULL,
  `nama` varchar(256) NOT NULL,
  `telp` varchar(32) NOT NULL,
  `role` enum('admin','donatur','relawan') DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data untuk tabel `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `email`, `nama`, `telp`, `role`, `status`) VALUES
(1, 'kaylla', 'kaylla123', 'kaylla.asyifa31@upi.edu', 'Kaylla Asyifa', '0891234567890', NULL, 1),
(2, 'aufazahron', 'aufa1234', 'aufazahron@upi.edu', 'Aufa Zahron', '08981234567', 'admin', 1),
(3, 'bayu', 'bayu123', 'bayuaji@gmail.com', 'Bayu Aji', '08123456789', 'donatur', 1),
(4, 'roven', 'roven123', 'roven@upi.edu', 'Roven', '091234567890', 'relawan', 1),
(5, 'key', 'key123', 'key@upi.edu', 'Kaylla Asyifa', '0123467890', 'relawan', 1),
(6, 'dancow', 'dancow123', 'dancow@upi.edu', 'Dancow', '081234567890', 'relawan', 1),
(8, 'relawan', 'relawan123', 'relawan@upi.edu', 'relawan', '08124567890', 'relawan', 1),
(9, 'asep', 'asep1234', 'asep@gmail.com', 'asep', '0808080808', 'relawan', 0),
(10, 'dawdwa', 'dawdawdaw', 'dawdawd@123.cp,', 'dwadaw', 'dawd', 'relawan', 0);

-- --------------------------------------------------------

--
-- Struktur dari tabel `user_donatur`
--

CREATE TABLE `user_donatur` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `jenis_donatur` enum('perorangan','perusahaan') NOT NULL DEFAULT 'perorangan',
  `nama_perusahaan` varchar(256) DEFAULT NULL COMMENT 'Diisi jika jenis perusahaan',
  `jabatan` varchar(128) DEFAULT NULL COMMENT 'Jabatan di perusahaan',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indeks untuk tabel `anak`
--
ALTER TABLE `anak`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_anak_relawan` (`id_relawan`);

--
-- Indeks untuk tabel `detail_anak`
--
ALTER TABLE `detail_anak`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_detail_anak` (`id_anak`);

--
-- Indeks untuk tabel `donasi`
--
ALTER TABLE `donasi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_donasi_program` (`id_program`),
  ADD KEY `fk_donasi_user` (`id_user`);

--
-- Indeks untuk tabel `laporan_monitoring`
--
ALTER TABLE `laporan_monitoring`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_laporan_anak` (`id_anak`),
  ADD KEY `fk_laporan_user` (`id_user`);

--
-- Indeks untuk tabel `laporan_program`
--
ALTER TABLE `laporan_program`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_laporan_program` (`id_program`);

--
-- Indeks untuk tabel `pesan`
--
ALTER TABLE `pesan`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_created_at` (`created_at`);

--
-- Indeks untuk tabel `program`
--
ALTER TABLE `program`
  ADD PRIMARY KEY (`id`),
  ADD KEY `program_ibfk_1` (`id_user`);

--
-- Indeks untuk tabel `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- Indeks untuk tabel `user_donatur`
--
ALTER TABLE `user_donatur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_user` (`user_id`),
  ADD KEY `idx_jenis` (`jenis_donatur`);

--
-- AUTO_INCREMENT untuk tabel yang dibuang
--

--
-- AUTO_INCREMENT untuk tabel `anak`
--
ALTER TABLE `anak`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT untuk tabel `detail_anak`
--
ALTER TABLE `detail_anak`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `donasi`
--
ALTER TABLE `donasi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT untuk tabel `laporan_monitoring`
--
ALTER TABLE `laporan_monitoring`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT untuk tabel `laporan_program`
--
ALTER TABLE `laporan_program`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `pesan`
--
ALTER TABLE `pesan`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT untuk tabel `program`
--
ALTER TABLE `program`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT untuk tabel `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT untuk tabel `user_donatur`
--
ALTER TABLE `user_donatur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `anak`
--
ALTER TABLE `anak`
  ADD CONSTRAINT `fk_anak_relawan` FOREIGN KEY (`id_relawan`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `detail_anak`
--
ALTER TABLE `detail_anak`
  ADD CONSTRAINT `fk_detail_anak` FOREIGN KEY (`id_anak`) REFERENCES `anak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `donasi`
--
ALTER TABLE `donasi`
  ADD CONSTRAINT `fk_donasi_program` FOREIGN KEY (`id_program`) REFERENCES `program` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_donasi_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `laporan_monitoring`
--
ALTER TABLE `laporan_monitoring`
  ADD CONSTRAINT `fk_laporan_anak` FOREIGN KEY (`id_anak`) REFERENCES `anak` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_laporan_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `laporan_program`
--
ALTER TABLE `laporan_program`
  ADD CONSTRAINT `fk_laporan_program` FOREIGN KEY (`id_program`) REFERENCES `program` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Ketidakleluasaan untuk tabel `program`
--
ALTER TABLE `program`
  ADD CONSTRAINT `program_ibfk_1` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`);

--
-- Ketidakleluasaan untuk tabel `user_donatur`
--
ALTER TABLE `user_donatur`
  ADD CONSTRAINT `fk_user_donatur` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
