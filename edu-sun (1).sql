-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Waktu pembuatan: 04 Des 2025 pada 06.02
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
(1, 2, 2, 50000, NULL, 'Aamiin', NULL, '2025-12-04 11:18:54', '2025-12-04 11:18:54', NULL);

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

INSERT INTO `program` (`id`, `id_user`, `nama`, `deskripsi`, `tempat`, `cover`, `biaya_program`, `target_donasi`, `donasi_terkumpul`, `jumlah_donatur`, `status_program`, `tanggal_pelaksanaan`, `tanggal_mulai_donasi`, `tanggal_selesai_donasi`, `created_at`, `updated_at`) VALUES
(1, 3, 'MemBAYUkan Semua', 'mendinginkan suasana', 'Aula Awan Kinton', 'bayu.jpg\r\n', NULL, 0, 0, 0, 'draft', NULL, NULL, NULL, '2025-11-20 11:46:26', NULL),
(2, 1, 'Monev', 'Triwulan 3', 'Balai Desa', 'proker.jpeg', NULL, 0, 50000, 1, 'draft', NULL, NULL, NULL, '2025-11-20 12:04:20', '2025-12-04 11:39:52'),
(3, 4, 'RAYAKARYA', 'LOMBA', 'KAMPUS', 'wkwk.png', NULL, 0, 0, 0, 'draft', NULL, NULL, NULL, '2025-11-20 12:09:33', NULL);

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
(2, 'aufazahron', 'aufa1234', 'aufazahron@upi.edu', 'Aufa Zahron', '08981234567', 'donatur', 1),
(3, 'bayu', 'bayu123', 'bayuaji@gmail.com', 'Bayu Aji', '08123456789', 'donatur', 1),
(4, 'roven', 'roven123', 'roven@upi.edu', 'Roven', '091234567890', 'relawan', 1);

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
-- Indeks untuk tabel `donasi`
--
ALTER TABLE `donasi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_donasi_program` (`id_program`),
  ADD KEY `fk_donasi_user` (`id_user`);

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
-- AUTO_INCREMENT untuk tabel `donasi`
--
ALTER TABLE `donasi`
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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT untuk tabel `user_donatur`
--
ALTER TABLE `user_donatur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Ketidakleluasaan untuk tabel pelimpahan (Dumped Tables)
--

--
-- Ketidakleluasaan untuk tabel `donasi`
--
ALTER TABLE `donasi`
  ADD CONSTRAINT `fk_donasi_program` FOREIGN KEY (`id_program`) REFERENCES `program` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_donasi_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

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
