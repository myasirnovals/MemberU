# TODO - Implementasi Splash Sync Detail Card

- [ ] Tambahkan Activity baru `SyncDetailActivity` untuk menampilkan splash/loading saat tombol Sync ditekan
- [ ] Buat layout `activity_sync_detail.xml` berisi animasi loading + teks "Memuat detail kartu"
- [ ] Tambahkan string ke `app/src/main/res/values/strings.xml`
- [ ] Daftarkan `SyncDetailActivity` di `AndroidManifest.xml`
- [ ] Ubah `DetailCardActivity`: tombol Sync berpindah ke `SyncDetailActivity` dan **finish** DetailCardActivity lama
- [ ] Pastikan setelah loading, `SyncDetailActivity` membuka kembali `DetailCardActivity` dengan `memberCardId`
- [ ] Build & test: tap Sync → splash tampil → detail ter-load, Back tidak menampilkan layar duplikat

