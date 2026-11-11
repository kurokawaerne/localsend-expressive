# LocalSend Expressive — Roadmap & Architecture

**What:** An unofficial Kotlin / Compose Multiplatform rewrite of LocalSend, for **Android and Linux only**, in **Material 3 Expressive**, that stays **fully compatible with the official LocalSend app**.
**Owner:** Ernest — GitHub `@kurokawaerne`
**Protocol:** LocalSend Protocol v2.2 (https://github.com/localsend/protocol)

> Not a fork of the Flutter code. A clean reimplementation of the published protocol, with credit to the LocalSend project. Devices running this app and devices running official LocalSend must discover each other and transfer files in both directions.

---

## 1. Module structure

```
localsend-expressive/
  shared/            # Kotlin Multiplatform — commonMain
    protocol/        # DTOs for v2.2 (Register, PrepareUpload, FileDto...), kotlinx.serialization
    discovery/       # Multicast UDP announcer/listener, register fallback, subnet scan
    transfer/        # Ktor server (receive) + Ktor client (send), session manager, sha256
    ui/              # Shared Compose UI — Send, Receive, History, Settings screens
    theme/           # MaterialExpressiveTheme, spring MotionScheme, MaterialShapes
  androidApp/        # Jetpack Compose shell: SAF picker, MulticastLock, foreground service
  desktopApp/        # Compose Desktop shell (JVM/Linux): window, tray, file dialogs, drag & drop
  packaging/
    arch/PKGBUILD
    appimage/
```

Rule: everything that *can* live in `shared` does. The two shells only provide platform services behind `expect/actual`.

## 2. Compatibility contract (Protocol v2.2)

| Piece | Spec |
|---|---|
| Multicast | UDP `224.0.0.167:53317`, announce JSON with `announce: true` |
| TCP port | `53317` (configurable) |
| Fingerprint | HTTPS: SHA-256 of TLS cert. HTTP: random string |
| Register | `POST /api/localsend/v2/register` — two-way device info exchange |
| Info | `GET /api/localsend/v2/info` |
| Prepare upload | `POST /api/localsend/v2/prepare-upload` (`?pin=`) → `{sessionId, files:{fileId: token}}` |
| Upload | `POST /api/localsend/v2/upload?sessionId&fileId&token` — raw binary, whole file in one request, parallel OK |
| Cancel | `POST /api/localsend/v2/cancel?sessionId` |
| Download API | `POST /prepare-download`, `GET /download?sessionId&fileId` (plain HTTP, for browsers) |
| Errors | prepare-upload: 204 / 400 / 401 PIN / 403 rejected / 409 busy / 429 / 500 · upload: 400 / 403 / 409 / 422 sha256 mismatch / 500 |

Hard rules learned from other reimplementations:
- **No invented chunking.** The upload body is the entire file in one request. Adding `Content-Range` breaks interop.
- **Tolerate unknown JSON fields** and missing nullable fields — the official app evolves the DTOs.
- **One active receive session** at a time; a second `prepare-upload` gets `409`.
- **Android needs a `WifiManager.MulticastLock`** or discovery silently receives nothing.
- Generate the self-signed TLS cert once, persist it, so the fingerprint (and trusted devices) stays stable.

### Interop test matrix — definition of done for v0.1
1. Expressive Android → Official Android · and back
2. Expressive Android → Official Linux · and back
3. Expressive Linux → Official Android · and back
4. Expressive ↔ Expressive (Android ↔ Linux)
Each: discovery appears < 5s, text message, 1 small file, 1 file > 500 MB, multi-file, cancel mid-transfer, PIN on/off.

## 3. Material 3 Expressive design

- `MaterialExpressiveTheme` with `MotionScheme.expressive()` — spring physics instead of easing curves
- `MaterialShapes` (35-shape library) with shape-morph for device avatars and the receive animation
- New components where they genuinely help: **FAB Menu** (File / Folder / Text / Clipboard), **Split Button** on Send, **Button Groups**, expressive **Loading Indicator**, wavy progress for transfers
- Emphasized typography scale, dynamic color on Android 12+, seeded color scheme on Linux
- Adaptive: single column + floating toolbar on phone; list-detail + wide navigation rail on desktop window

## 4. Milestones

- **M0 — Scaffold:** ✅ repo, Gradle KMP build, empty shells run on Android + Linux, GitHub Actions (APK + desktop)
- **M1 — Discovery:** ✅ multicast announce/listen + register + info, HTTPS with persisted self-signed cert (fingerprint = cert SHA-256), real Android MulticastLock, live device list in UI. *Success: official app sees us, we see it.*
- **M2 — Receive:** ✅ HTTPS server, accept dialog, upload endpoint streamed to disk, sha256 check (422), busy 409, cancel, duplicate-name protection
- **M3 — Send:** ✅ SAF/AWT picker, prepare-upload, per-file upload with progress, cancel, sha256 precomputed and sent (uploads sequential for now; official uses concurrency 2)
- **M4 — Expressive UI:** ✅ expressive shapes/theme, device cards with favorites, transfer progress states, offer dialog, history list (deeper expressive components — FAB Menu, Loading Indicator, dynamic color — still to come)
- **M5 — Extras:** ✅ Download API (prepare-download/download), PIN (401 + retry-with-PIN flow), favorites, history, settings (alias, port, PIN, HTTPS toggle, auto-accept favorites) persisted as JSON
- **M6 — Release:** signed APK, AppImage, Arch `.pkg.tar.zst`, GitHub Releases — release workflow produces the Android APK, the Arch package and a portable zip (APK still debug-signed; signing is the remaining M6 item)

**v0.2.0 (branch `v0.2.0`)** integrates M1–M5 in one pass. Next: bug hunting against the interop matrix in §2.

Each milestone is sized to fit one of Ernest's ~2-hour power sessions and ends in a buildable, testable state.

## 5. Naming & identity

- Repo (proposed): `kurokawaerne/localsend-expressive`
- App name: **LocalSend Expressive** (marked *unofficial* in README and About screen)
- Android applicationId: `dev.kurokawa.localsend.expressive` — different from official so both install side-by-side for interop testing
