# LSEx — LocalSend Expressive

An **unofficial** Material 3 Expressive rewrite of [LocalSend](https://localsend.org), built with Kotlin and Compose Multiplatform for **Android and Linux** only.

It implements the [LocalSend Protocol v2.2](https://github.com/localsend/protocol) from scratch, so it is **fully interoperable with the official LocalSend app**: devices running LSEx and devices running official LocalSend discover each other and transfer files in both directions.

> This project is not affiliated with or endorsed by the LocalSend project. "LocalSend" is the name of the upstream project and protocol; this is an independent reimplementation of the published protocol, with credit to the original authors.

**Current release: v0.4.0** — full feature parity with the official app (see the checklist below), an Expressive redesign in the spirit of [Minus](https://github.com/isaacsa51/Minus) and PixelPlayer, a from-the-ground-up fix for the mutual-TLS send failure of earlier versions, a Rust core for the transfer-critical math, chunked resumable sends with a persistent retry outbox, and main-page connectivity shortcuts.

## Features (parity with the official app)

**Send**
- Selection grid in Material 3 Expressive style: **File · Media · Paste · Text · Folder · App**
  - *Folder* sends keep their folder structure on the receiving side
  - *App* (Android) sends an installed app's APK
  - *Paste* sends the clipboard as a text message
- Android share sheet integration, Quick Settings quick-share tile
- Device list with favorites, device info (fingerprint verification), manual IP (incl. `host:port`)
- PIN entry when the receiver requires one, per-file progress, cancel, partial acceptance
- **Resumable chunked sends (LSEx ↔ LSEx):** files move in 1 MiB chunks staged on the receiver, so a dropped connection resumes from the receiver's confirmed byte count instead of zero (official peers get the classic whole-file upload — see *Protocol* below)
- **Persistent outbox:** every send is saved before it starts and survives app restarts; failed sends retry automatically with backoff, and anything left can be retried **as a whole or per single file** from the Outbox section

**Receive**
- Accept dialog with **per-file selection**
- Quick Save and Quick Save from Favorites
- Received files land where they belong: public **Downloads**, or **Pictures/Movies/Music** with *Save to Gallery* — never in an app-private corner
- Share via link (Download API): browser page, QR code, chosen files, optional auto-accept of web uploads
- History with one-line previews

**Connectivity shortcuts (Receive screen)**
- **WiFi / Hotspot / WiFi Direct** toggles: on Android the hotspot is a local-only hotspot (no internet needed) and WiFi Direct starts a P2P group; on desktop the toggles drive NetworkManager via `nmcli`. (Android 10+ forbids apps from flipping the WiFi radio, so that toggle opens the system internet panel.)
- **Connect QR code:** shows the hotspot/P2P credentials as a scannable `WIFI:` code — or an LSEx connect code for this device when no hotspot is up
- **QR scanner:** scan a `WIFI:` code to join a network, an `http(s)` link to open a web share, or an LSEx connect code to add that device

**Every official setting is present**
- *General:* theme, color mode (wallpaper dynamic color or LSEx teal), language (System / English / Español), animations; desktop: always on top, save window placement, minimize to tray
- *Receive:* Quick Save, Quick Save from Favorites, require PIN, destination, Save to Gallery, Auto Finish, Save to History, verify checksums (advanced)
- *Send (advanced):* share-via-link auto-accept, create checksums, plus LSEx additions: auto-retry sends, resumable chunked sends
- *Network:* server on/off, alias, port, encryption (HTTPS), Download API; advanced: device model/type, discovery timeout, multicast group, network whitelist/blacklist

**Platform behavior**
- Android foreground service with a persistent notification, WakeLock and Wi-Fi lock, so transfers survive the screen going off
- Animated splash (system splash → in-app Compose splash), adaptive + monochrome themed icon
- Desktop: tray support, window placement restore

## Protocol v2.2 compatibility

- Multicast UDP `224.0.0.167:53317` announcements + `POST /api/localsend/v2/register`
- `POST /prepare-upload` → `POST /upload?sessionId&fileId&token`, whole file in one request; 204 for text messages; 401/409/422/429 semantics matched
- HTTPS with a self-signed certificate; fingerprint = SHA-256 of the certificate
- **Mutual TLS is required by official peers** — LSEx presents a client certificate with proper key-usage/extended-key-usage (serverAuth + clientAuth) through a deterministic key manager
- Receive sessions expire after 10 idle minutes, and the *same* peer (same fingerprint + IP) may replace its own stale session — so a retry after a crash never eats a spurious 409

### LSEx extension: chunked resumable uploads (`/api/lsex/v1/*`)

Not part of Protocol v2.2, and never required: LSEx senders probe `GET /api/lsex/v1/capabilities` first. Official peers 404 the probe and receive the classic whole-file upload, unchanged. Between LSEx peers, an accepted session uploads each file in 1 MiB chunks (`PUT /api/lsex/v1/upload-chunk`, each chunk SHA-256-verified), the receiver stages chunks in a random-access temp file and reports its contiguous confirmed byte count (`GET /api/lsex/v1/upload-status`), and `POST /api/lsex/v1/finish-file` publishes the completed file through the normal receive sink (MediaStore/files, checksum verification, history). Only fully confirmed chunks count toward resume — a partially received chunk is always resent whole.

## Architecture

- `shared` — one Kotlin codebase:
  - `commonMain` — Protocol v2.2 DTOs, UI (Compose Multiplatform, Material 3 Expressive, Google Sans Flex), settings model
  - `jvmMain` — multicast discovery, Ktor (Netty) HTTPS receive server, OkHttp/JSSE send client, TLS identity, transfer engine, MediaStore/file receive sinks
  - `androidApp` — Jetpack Compose shell (package `dev.kurokawa.localsend.expressive`, installs alongside official LocalSend), foreground service, QS tile
  - `desktopApp` — Compose Desktop shell for Linux (Arch package + portable zip)

### Rust core (`core-rust/`)

The transfer-critical math — chunk planning, resume offsets and per-chunk SHA-256 — lives in a small std-only Rust crate, `lsex-core`, shared by Android (per-ABI `liblsex_core.so` in jniLibs) and desktop (bundled resource), reached through JNI (`dev.kurokawa.localsend.expressive.RustCore`). The native library is an accelerator, never a single point of failure: if it is missing or fails to load, an identical Kotlin implementation takes over transparently — both are locked to the same test vectors (`cargo test` and `TransferMathTest`), and the Settings footer shows which engine is active. A possible future step is moving the TLS transfer path itself into Rust (rustls); the chunk/resume layer was the part that had to be exactly right first.

## Building

CI (GitHub Actions) is the build of record: it runs the unit tests, assembles the Android APK, and packages the Linux artifacts (Arch `.pkg.tar.zst` + portable zip).

Locally (JDK 21, Gradle 8.14):

```
./gradlew :androidApp:assembleRelease
./gradlew :desktopApp:packageDistributionForCurrentOS
```

## License

Apache-2.0 (matching upstream). Google Sans Flex is bundled under the SIL Open Font License 1.1 (see `shared/src/commonMain/composeResources/font/`).
