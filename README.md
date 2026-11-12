# LSEx — LocalSend Expressive

An **unofficial** Material 3 Expressive rewrite of [LocalSend](https://localsend.org), built with Kotlin and Compose Multiplatform for **Android and Linux** only.

It implements the [LocalSend Protocol v2.2](https://github.com/localsend/protocol) from scratch, so it is **fully interoperable with the official LocalSend app**: devices running LSEx and devices running official LocalSend discover each other and transfer files in both directions.

> This project is not affiliated with or endorsed by the LocalSend project. "LocalSend" is the name of the upstream project and protocol; this is an independent reimplementation of the published protocol, with credit to the original authors.

**Current release: v0.4.0** — full feature parity with the official app (see the checklist below), an Expressive redesign in the spirit of [Minus](https://github.com/isaacsa51/Minus) and PixelPlayer, and a from-the-ground-up fix for the mutual-TLS send failure of earlier versions.

## Features (parity with the official app)

**Send**
- Selection grid in Material 3 Expressive style: **File · Media · Paste · Text · Folder · App**
  - *Folder* sends keep their folder structure on the receiving side
  - *App* (Android) sends an installed app's APK
  - *Paste* sends the clipboard as a text message
- Android share sheet integration, Quick Settings quick-share tile
- Device list with favorites, device info (fingerprint verification), manual IP (incl. `host:port`)
- PIN entry when the receiver requires one, per-file progress, cancel, partial acceptance

**Receive**
- Accept dialog with **per-file selection**
- Quick Save and Quick Save from Favorites
- Received files land where they belong: public **Downloads**, or **Pictures/Movies/Music** with *Save to Gallery* — never in an app-private corner
- Share via link (Download API): browser page, QR code, chosen files, optional auto-accept of web uploads
- History with one-line previews

**Every official setting is present**
- *General:* theme, color mode (wallpaper dynamic color or LSEx teal), language (System / English / Español), animations; desktop: always on top, save window placement, minimize to tray
- *Receive:* Quick Save, Quick Save from Favorites, require PIN, destination, Save to Gallery, Auto Finish, Save to History, verify checksums (advanced)
- *Send (advanced):* share-via-link auto-accept, create checksums
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

## Architecture

- `shared` — one Kotlin codebase:
  - `commonMain` — Protocol v2.2 DTOs, UI (Compose Multiplatform, Material 3 Expressive, Google Sans Flex), settings model
  - `jvmMain` — multicast discovery, Ktor (Netty) HTTPS receive server, OkHttp/JSSE send client, TLS identity, transfer engine, MediaStore/file receive sinks
  - `androidApp` — Jetpack Compose shell (package `dev.kurokawa.localsend.expressive`, installs alongside official LocalSend), foreground service, QS tile
  - `desktopApp` — Compose Desktop shell for Linux (Arch package + portable zip)

### Roadmap: a Rust core (v0.5.0)

The official app's engine is a Rust core; LSEx's Kotlin/JVM engine is the part that has historically been most fragile (TLS edge cases). The next milestone moves the critical transfer path — the mutual-TLS client first, then the server — into a Rust crate (rustls) shared by Android and desktop, keeping the Compose UI and protocol DTOs as they are.

## Building

CI (GitHub Actions) is the build of record: it runs the unit tests, assembles the Android APK, and packages the Linux artifacts (Arch `.pkg.tar.zst` + portable zip).

Locally (JDK 21, Gradle 8.14):

```
./gradlew :androidApp:assembleRelease
./gradlew :desktopApp:packageDistributionForCurrentOS
```

## License

Apache-2.0 (matching upstream). Google Sans Flex is bundled under the SIL Open Font License 1.1 (see `shared/src/commonMain/composeResources/font/`).
