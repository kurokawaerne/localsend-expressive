# LSEx — LocalSend Expressive

An **unofficial** Material 3 Expressive rewrite of [LocalSend](https://localsend.org), built with Kotlin and Compose Multiplatform for **Android and Linux** only.

It implements the [LocalSend Protocol v2.2](https://github.com/localsend/protocol) from scratch, so it is **fully interoperable with the official LocalSend app**: devices running LSEx and devices running official LocalSend discover each other and transfer files in both directions.

> This project is not affiliated with or endorsed by the LocalSend project. "LocalSend" is the name of the upstream project and protocol; this is an independent reimplementation of the published protocol, with credit to the original authors.

**Current release: v0.6.0** — the expressive-browsers release: Home's content tabs now show the actual content (inline app list, photo/video thumbnail grids, audio list, and a Zapya-style file tree with breadcrumbs), a dedicated Connect view with a device radar, swipe-to-reveal actions on staged files, a single system splash, signed release builds, and the official Material Symbols icon set throughout (outlined by default, filled in Settings, nav icons animate outlined → filled when selected).

## Features (parity with the official app, and then some)

**One Home view** — compose what to send, then tap as many target devices as you want (tap again to unselect)
- Content tabs show the content itself, Zapya/Files-style, with in-place selection that persists across tabs: **Apps · Photos · Videos · Audio · Files · Text · Web**
  - *Apps* (Android) is an inline list with real app icons, search, and a hide-system-apps toggle; sends an app's APK — `.apk`, `.apks` and `.aab` files can be picked as files too
  - *Photos / Videos* are MediaStore thumbnail grids; *Audio* is a media list
  - *Files* is a built-in file browser with a folder tree and breadcrumb path bar; folders can be staged whole (structure kept on the receiving side) or expanded file by file
  - *Text* is written inline (or pasted from the clipboard) and goes to every selected device
  - *Web* is share-via-link (Download API): browser page, QR code, chosen files, optional auto-accept of web uploads
- Staged items are individual cards; on Android their **rename / discard** actions hide behind a swipe-left (long-press works too), on desktop the buttons stay visible; the Ready-to-send section expands and collapses
- Android share sheet integration, Quick Settings quick-share tile
- Device list with favorites (sorted first), device info (fingerprint verification), manual IP (incl. `host:port`)
- PIN entry when the receiver requires one, per-file progress cards with quick cancel, partial acceptance
- **Resumable chunked sends (LSEx ↔ LSEx):** files move in 1 MiB chunks staged on the receiver, so a dropped connection resumes from the receiver's confirmed byte count instead of zero (official peers get the classic whole-file upload — see *Protocol* below)
- **Persistent outbox:** every send is saved before it starts and survives app restarts; transfers are grouped per recipient device with dividers, failed sends retry automatically with backoff, and anything left can be retried **as a whole or per single file**. Finished sends can be re-staged from History in one tap

**Receive**
- Accept dialog with **per-file selection**
- Quick Save as one connected button group: **Off / Favorites / Everyone**
- Received files land where they belong: pick any folder with the system folder picker (**SAF**), or let them go to public **Downloads** / **Pictures/Movies/Music** with *Save to Gallery* — never an app-private corner. History keeps a link to every received file (**Open** button)
- **Built-in installer:** received `.apk` files and split sets (`.apks` / `.xapk` / `.apkm`) install straight from History through PackageInstaller sessions — no second app needed
- Discovery announces and listens on **every LAN interface** (plus subnet broadcast copies), so machines with VPNs or virtual NICs stay visible

**Connect view**
- A dedicated Connect screen (second nav destination, plus a floating action **fan** on Home for connection setup): **Create Group / Join Group** cards and a radar with nearby devices as tappable bubbles
- **WiFi / Hotspot / WiFi Direct** toggles: on Android the hotspot is a local-only hotspot (no internet needed) and WiFi Direct starts a P2P group; on desktop the toggles drive NetworkManager via `nmcli`. (Android 10+ forbids apps from flipping the WiFi radio, so that toggle opens the system internet panel.)
- **QR codes only when they carry something useful:** your QR appears while your hotspot/P2P group is up (it carries the credentials), or behind an explicit *Show my QR* tap — never as permanent decoration
- **QR scanner:** scan a `WIFI:` code to join a network, an `http(s)` link to open a web share, or an LSEx connect code to add that device

**Every official setting is present** — grouped Android-style cards with a colored-circle icon per setting, background-colored dividers, and dropdown menus for theme / colors / language
- *General:* theme, color mode (wallpaper dynamic color or LSEx teal), language (System / English / Español), animations; desktop: always on top, save window placement, minimize to tray
- *Receive:* Quick Save, Quick Save from Favorites, require PIN, destination, Save to Gallery, Auto Finish, Save to History, verify checksums (advanced)
- *Send (advanced):* share-via-link auto-accept, create checksums, plus LSEx additions: auto-retry sends, resumable chunked sends
- *Network:* server on/off, alias, port, encryption (HTTPS), Download API; advanced: device model/type, discovery timeout, multicast group, network whitelist/blacklist

**Platform behavior**
- Android foreground service with a persistent notification (**Stop** action included), WakeLock and Wi-Fi lock, so transfers survive the screen going off
- **Split APKs** per CPU architecture (arm64-v8a, armeabi-v7a, x86_64) plus a universal build; the Rust core is 16 KB-page aligned
- Floating navigation bar on phones (edge-to-edge, no strip behind it); ChromeOS-style rail + floating rounded content on desktop/wide windows. Icons are the official **Material Symbols** set: outlined by default, filled on the Settings page, and the nav icons animate outlined → filled when selected
- One splash only — the system splash, held until the first frame is ready; adaptive + monochrome themed icon drawn inside the safe zone
- Release builds are properly **signed** (no more debug-signature compatibility dialog)
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
