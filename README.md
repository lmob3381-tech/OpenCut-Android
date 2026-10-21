# OpenCut Android

OpenCut Android adalah aplikasi video editor native untuk platform Android dengan UI/UX yang dirancang presisi menyerupai **CapCut Mobile** (OLED Dark Mode, Fixed Center Playhead, Trimming Handles, dan Contextual Action Bar).

Aplikasi ini dibangun menggunakan **Kotlin** dan **Jetpack Compose**, serta dikonfigurasi untuk menghasilkan APK terpisah untuk arsitektur **32-bit (`armeabi-v7a`)** dan **64-bit (`arm64-v8a`)**, serta **Universal APK**.

---

## Fitur Utama

- **OLED Pure Black Interface (`#000000`)**: Desain kontras tinggi tanpa *bloatware* grafis, responsif dan ringan di baterai.
- **Top Navigation Bar**:
  - Tombol Tutup / Simpan Draf
  - Pemilih Cepat Resolusi & Frame Rate (`1080P • 30`)
  - Tombol Ekspor Cepat
- **Interactive Video Preview**:
  - Rasio kanvas adaptif (9:16 TikTok/Reels, 16:9 YouTube, 1:1 Instagram, 4:5, 2.35:1)
  - Safe-area grid visual & Bounding Box sentuh saat klip/teks aktif
- **Playback & Timecode Bar**:
  - Timecode presisi milidetik (`00:02.14 / 00:15.00`)
  - Tombol Putar/Jeda (Play/Pause)
  - Tombol Undo & Redo fungsional
  - Tombol Pratinjau Layar Penuh
- **Multi-Track Timeline (Signature CapCut Experience)**:
  - **Fixed Center Playhead**: Garis jarum waktu tetap di tengah dengan indikator panah atas; track video bergeser mulus di bawahnya saat di-*scrub*.
  - **Main Video Track**: Visual strip thumbnail, white border highlight saat dipilih, drag handles kiri & kanan untuk *trimming*.
  - **Secondary Tracks**: Audio waveform track & Text overlay track.
  - **Gestur Sentuh**: Geser horizontal (*scrubbing*) & cubit untuk zoom rentang waktu (*pinch-to-zoom*).
- **Contextual Action Toolbar**:
  - **Mode Utama**: Edit, Audio, Teks, Overlay, Efek, Filter, Rasio, Kanvas, Sesuaikan.
  - **Mode Klip Aktif**: Bagi (Split di posisi playhead), Kecepatan (0.1x - 10.0x), Animasi, Hapus, Volume (0% - 200%), Filter, Salin, Balik.
- **Interactive Sheets & Modals**:
  - Panel Resolusi (720P, 1080P, 2K/4K, fps selector, bitrate slider)
  - Panel Kecepatan (slider presisi + preset 0.5x, 1x, 2x, 5x, 10x + switch pitch)
  - Panel Volume (slider 0-200% + fade in/fade out)
  - Panel Editor Teks (gaya font, warna palette, ukuran slider)
  - Layar Proses Ekspor (indikator persentase sirkular + status selesai)

---

## Target Arsitektur (32-bit & 64-bit)

Proyek ini dikonfigurasi dengan Gradle ABI Splits:
- `OpenCut-32bit-armeabi-v7a.apk` (Smartphone 32-bit)
- `OpenCut-64bit-arm64-v8a.apk` (Smartphone modern 64-bit)
- `OpenCut-Universal.apk` (Dapat dipasang di semua arsitektur)

---

## Build Otomatis via GitHub Actions

Repositori ini sudah dilengkapi alur kerja CI otomatis di `.github/workflows/build-apk.yml`.

1. Buat repositori baru di akun GitHub Anda (misal `OpenCut-Android`).
2. Masukkan semua isi folder ini (`git push` ke repositori tersebut):
   ```bash
   git init
   git add .
   git commit -m "feat: initial OpenCut Android native app with CapCut UI"
   git branch -M main
   git remote add origin https://github.com/<username>/<repo>.git
   git push -u origin main
   ```
3. Buka tab **Actions** di GitHub. Alur kerja **Build Android APK (32-bit & 64-bit)** akan otomatis berjalan dan mengompilasi APK.
4. Setelah selesai, unduh file APK dari bagian **Artifacts** (`OpenCut-Mobile-APKs`).

---

## Build Manual / Lokal

Pastikan Anda memiliki JDK 17 atau 21 dan Android SDK (API 34).

```bash
# Debug Build
./gradlew assembleDebug

# Release Build (Signed)
./gradlew assembleRelease
```

File APK hasil build akan berada di direktori:
- `app/build/outputs/apk/debug/`
- `app/build/outputs/apk/release/`
