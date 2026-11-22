# Cinetrail

**Cinetrail** adalah aplikasi Android berbasis **Jetpack Compose** untuk menjelajahi film dari seluruh dunia. Aplikasi ini menampilkan rekomendasi film trending, daftar film populer dan rating tertinggi, filter berdasarkan negara dan genre, detail sinopsis, serta informasi tempat menonton (streaming, sewa, beli).

Aplikasi ini dikembangkan oleh **Abryan Yoga Pratama** sebagai tugas proyek Pemrograman Mobile.

---

## 🚀 Fitur Utama

- **Dashboard Interaktif**:
  - Carousel film *Trending Minggu Ini* dengan indikator interaktif.
  - Filter cepat berdasarkan **Negara** (Semua, Indonesia, Amerika Serikat, Korea Selatan, Jepang, India, Inggris) dan **Genre**.
  - Daftar film populer dan rating tertinggi disajikan dalam bentuk kartu interaktif (`LazyColumn`).
- **Pencarian Film**: Cari film berdasarkan kata kunci dengan pencarian cepat.
- **Detail Film Lengkap**:
  - Gambar sampul (*backdrop*) dan poster film berkualitas tinggi.
  - Rating, durasi, tanggal rilis, dan genre film.
  - Sinopsis lengkap dengan dukungan fallback bahasa Inggris jika sinopsis versi lokal kosong.
- **Tempat Nonton (Watch Providers)**:
  - Menampilkan platform streaming, tempat sewa, dan tempat beli film berdasarkan wilayah pilihan (ID, US, SG, JP, KR, GB).
  - Dilengkapi logo resmi provider dan atribusi JustWatch.
- **Multi Bahasa (ID & EN)**:
  - Aplikasi mendukung Bahasa Indonesia dan Bahasa Inggris secara penuh (UI dan data TMDB).
  - Dapat diganti langsung dari menu header tanpa perlu mengubah bahasa perangkat.
- **Dukungan Dark Mode & Rotasi Layar**:
  - Menggunakan komponen **Material 3** yang menyesuaikan mode gelap (Night mode) secara otomatis.
  - Tampilan yang adaptif saat layar diputar (*portrait* & *landscape*).

---

## 🛠️ Stack Teknologi & Library

- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Navigasi**: Navigation Compose
- **Arsitektur**: MVVM (Model-View-ViewModel) + StateFlow
- **Networking**: Retrofit 2, OkHttp 3, Gson
- **Image Loading**: Coil Compose
- **Sumber Data**: TMDB API (The Movie Database) & JustWatch API

---

## 📋 Syarat & Cara Menjalankan Proyek

### Persyaratan
- Android Studio Ladybug / Meerkat / 2026.x atau yang lebih baru.
- JDK 11 atau 17.
- Kunci API gratis dari [TMDB (The Movie Database)](https://www.themoviedb.org/).

### Langkah Instalasi
1. Clone repositori ini:
   ```bash
   git clone https://github.com/AbryanYoga/Cinetrail.git
   cd Cinetrail
   ```
2. Buat file `local.properties` di folder utama (root) proyek jika belum ada.
3. Tambahkan kunci API TMDB kamu ke `local.properties`:
   ```properties
   TMDB_API_KEY=masukkan_api_key_tmdb_kamu_di_sini
   ```
4. Buka proyek di **Android Studio**, tunggu proses Gradle Sync selesai.
5. Jalankan aplikasi pada Emulator Android (API 28+) atau Perangkat Fisik.

---

## 📜 Atribusi & Lisensi Data

- Produk ini menggunakan **API TMDB** tetapi tidak didukung atau disertifikasi oleh TMDB.
  *(This product uses the TMDB API but is not endorsed or certified by TMDB.)*
- Data tempat nonton (*Watch Providers*) disediakan oleh **JustWatch**.
  *(Watch provider data is provided by JustWatch.)*
