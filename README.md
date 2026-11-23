# Cinetrail

**Cinetrail** adalah aplikasi Android berbasis **Jetpack Compose** untuk menjelajahi film dari seluruh dunia. Aplikasi ini menampilkan rekomendasi film trending, daftar film populer dan rating tertinggi, filter berdasarkan negara dan genre, detail sinopsis, serta informasi tempat menonton (streaming, sewa, beli).

Aplikasi ini dikembangkan oleh **Abryan Yoga Pratama** sebagai tugas proyek Pemrograman Mobile.

---

## 🚀 Fitur Utama

- **Dashboard Interaktif**:
  - Carousel film *Trending Minggu Ini* dengan indikator interaktif.
  - Filter cepat berdasarkan **Negara** (Semua, Indonesia, Amerika Serikat, Korea Selatan, Jepang, India, Inggris) dan **Genre**.
  - Daftar film populer dan rating tertinggi disajikan dalam bentuk kartu interaktif (`LazyColumn`).
- **Pencarian Film Cerdas & Real-time**:
  - Kolom pencarian responsif dengan *debouncing* 350ms dan pembatalan coroutine untuk efisiensi jaringan.
  - Tombol hapus pencarian (*clear button*) satu sentuhan.
  - Panduan pencarian interaktif saat kolom kosong dan hasil pencarian lengkap dengan *badge* genre film.
- **Detail Film Lengkap**:
  - Tampilan hero *backdrop* sinematik dengan pemutar cuplikan/trailer YouTube dan penanda durasi.
  - Badge informasi cepat: Usia/Rating Sensor (+18/13+), Genre Utama, Rating Bintang Emas, serta tombol Favorit.
  - Sinopsis interaktif dengan pembacaan dinamis (*Show More / Show Less*).
  - Daftar pemeran utama (*Actors*) lengkap dengan foto aktor dan nama karakter dalam film.
  - Dukungan fallback bahasa Inggris jika sinopsis versi lokal kosong.
- **Tempat Nonton (Watch Providers)**:
  - Menampilkan platform streaming, tempat sewa, dan tempat beli film berdasarkan wilayah pilihan (ID, US, SG, JP, KR, GB).
  - Tombol aksi utama untuk langsung membuka tautan resmi penyedia tayangan.
  - Dilengkapi logo resmi provider dan atribusi JustWatch.
- **Multi Bahasa (ID & EN)**:
  - Aplikasi mendukung Bahasa Indonesia dan Bahasa Inggris secara penuh (UI dan data TMDB).
  - Dialog seleksi bahasa interaktif dengan bendera negara dan pergantian instan menggunakan `AppCompatDelegate.setApplicationLocales`.
- **Dukungan Penuh Dark / Light Mode & Rotasi Layar**:
  - Menggunakan palet **Material 3** (Cinematic Amber & Deep Obsidian) tanpa *hardcode* warna, sehingga tampilan Night & Not Night mode tampil sempurna.
  - Adaptabilitas tata letak saat rotasi layar (*portrait* & *landscape*) menggunakan `LazyColumn` dan `verticalScroll` sehingga bebas *clipping* dan tetap dapat digulirkan secara lancar.

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
