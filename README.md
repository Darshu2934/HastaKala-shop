# Hasta Kala

Artisan-focused app: **native Android** (Kotlin, Jetpack Compose, Room) plus an optional **Next.js** web tool for AI-generated product copy and SEO.

## Android app

- Open the repository root in **Android Studio** (not the `web/` folder).
- Gradle sync, then run the `app` configuration on a device or emulator.
- Minimum SDK 26; see `app/build.gradle.kts` for versions.

## Android — AI tab

The bottom navigation **AI** tab calls the same `/api/suggest` API as the web app. Start the Next server (`npm run dev` in `web/`) with `OPENAI_API_KEY` set.

- **Emulator:** default base URL is `http://10.0.2.2:3000` (maps to your computer’s localhost).
- **Physical device:** add to `local.properties` (project root):  
  `suggest.api.base.url=http://YOUR_PC_LAN_IP:3000`  
  then sync Gradle so `BuildConfig.SUGGEST_API_BASE_URL` updates.

Debug builds allow cleartext HTTP (`app/src/debug/AndroidManifest.xml`). Use HTTPS for production APIs.

## Troubleshooting: AI not connecting or “not responding”

Work through these in order:

1. **Start the Next.js server** from a terminal on your PC: `cd web` then `npm run dev`. Wait until it prints `Ready` on port **3000**. The Android app does **not** call OpenAI directly; it only talks to this server.

2. **Set `OPENAI_API_KEY`** in `web/.env.local` (copy from `web/.env.example`). If the key is missing, the API returns HTTP 500 with `Missing OPENAI_API_KEY on the server.` — the app will show that message in the red error box.

3. **Quick test in a browser on the PC** (while `npm run dev` is running): open `http://localhost:3000` and submit the form. If that fails, fix the web/API first; the Android app will not work until this succeeds.

4. **Emulator vs real phone**
   - **Android Emulator:** keep the default `http://10.0.2.2:3000` (no `local.properties` line needed). `10.0.2.2` is the emulator’s alias for your PC’s localhost.
   - **Physical phone:** the phone cannot use `10.0.2.2` or `localhost` to mean your PC. In **`local.properties`** (project root, same folder as `settings.gradle.kts`) add a line like  
     `suggest.api.base.url=http://192.168.1.42:3000`  
     using your PC’s **Wi‑Fi IPv4** address (`ipconfig` on Windows). Phone and PC must be on the **same Wi‑Fi**. Then **File → Sync Project with Gradle Files** in Android Studio.

5. **Windows firewall:** the first time Node listens on port 3000, allow **private network** access if Windows asks. If the phone still cannot connect, temporarily allow inbound TCP **3000** for `node.exe` in Windows Defender Firewall (advanced settings).

6. **Run a Debug build** of the app when using `http://…`. Release builds do not merge `app/src/debug/AndroidManifest.xml`, so **plain HTTP may be blocked** on release unless you use HTTPS or a network security config.

7. **Base URL format:** use the **origin only** — e.g. `http://10.0.2.2:3000` — not `.../api/suggest` (the app appends `/api/suggest` itself).

8. **If the error mentions JSON or HTTP 404:** the URL is probably pointing at the wrong host/port (not the Next app), or an old build is still using an empty/wrong `BuildConfig.SUGGEST_API_BASE_URL` — sync Gradle and reinstall the app.

## Web — AI product suggestions

The Next.js app lives in **`web/`** so it does not conflict with Gradle’s **`app/`** module at the repo root.

1. `cd web`
2. `npm install`
3. Copy `web/.env.example` to `web/.env.local` and set `OPENAI_API_KEY`.
4. `npm run dev` — or double-click / run **`web/start-ai.ps1`** in PowerShell (it checks your key and starts the server).

API: `POST /api/suggest` with JSON `{ "title", "material" }` (used by the home page form).

## Repository layout (high level)

| Path | Purpose |
|------|---------|
| `app/` | Android application module |
| `web/` | Next.js + Tailwind + OpenAI suggester |
