# Dustbusters-of-Destin

Cleaning app

This repository is a minimal scaffold forDustbusters web + mobile app.

Structure:

- `backend/`  — simple Java HTTP server exposing GET /jobs
- `web/`      — React + Vite + TypeScript web frontend
- `mobile/`   — Expo React Native TypeScript app (starter)

Quick start

1) Java backend

```bash
cd backend
chmod +x run.sh
./run.sh
```

2) Web frontend

```bash
cd web
npm install
npm run dev
```

3) Mobile (optional)

```bash
cd mobile
npm install
npx expo start
```

Notes

- The Java backend is intentionally minimal so you can iterate quickly. It listens on port 8080 and exposes `GET /jobs`.
- The web frontend (port 3000) fetches `/jobs` from the backend. When running both locally you can keep them on the same machine.

Next steps (suggested): add persistent storage, authentication, booking flow, and CI/deployment.
add persistance task
