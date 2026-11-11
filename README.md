# LocalSend Expressive

An **unofficial** Material 3 Expressive rewrite of [LocalSend](https://localsend.org), built with Kotlin and Compose Multiplatform for **Android and Linux** only.

It implements the [LocalSend Protocol v2.2](https://github.com/localsend/protocol) from scratch, so it is **fully interoperable with the official LocalSend app**: devices running this app and devices running official LocalSend discover each other and transfer files in both directions.

> This project is not affiliated with or endorsed by the LocalSend project. "LocalSend" is the name of the upstream project and protocol; this is an independent reimplementation of the published protocol, with credit to the original authors.

## Status

🚧 **v0.2.0 in development** (branch `v0.2.0`): M1–M5 are implemented — discovery, HTTPS send/receive, Download API, PIN, favorites, history and settings. Bug hunting against the official app is next. See [ROADMAP.md](ROADMAP.md) for architecture, the compatibility contract, and milestones.

## Planned stack

- **Kotlin Multiplatform + Compose Multiplatform** — one shared codebase
  - `shared` — Protocol v2.2 DTOs, multicast discovery, Ktor HTTPS server/client, transfer engine, most UI
  - `androidApp` — Jetpack Compose shell (package `dev.kurokawa.localsend.expressive`, installs alongside official LocalSend)
  - `desktopApp` — Compose Desktop (JVM) shell for Linux
- **Material 3 Expressive** — `MaterialExpressiveTheme`, spring motion, shape morphing, FAB Menu, Loading Indicator
- **Networking** — Ktor + kotlinx.serialization

## Compatibility highlights

- Multicast UDP `224.0.0.167:53317` + `POST /api/localsend/v2/register`
- `POST /prepare-upload` → `POST /upload?sessionId&fileId&token`, whole file in one request
- HTTPS with a self-signed certificate; fingerprint = SHA-256 of the certificate
- PIN, cancel, and the browser Download API

## License

To be added with the first code drop (planned Apache-2.0, matching upstream).
