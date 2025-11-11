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

Run locally (recommended steps)

1. Start Postgres (Docker Compose):

```bash
docker compose up -d
# wait for DB to come up
sleep 3
# load schema
cat backend/database/schema.sql | docker exec -i $(docker ps -q -f "ancestor=postgres:15") psql -U postgres -d dustbusters
```

2. Set environment variables (example):

```bash
export DB_URL='jdbc:postgresql://localhost:5432/dustbusters'
export DB_USER='postgres'
export DB_PASS='mysecretpassword'
export JWT_SECRET="$(openssl rand -base64 32)"
```

3. Build and run backend (with debugger):

```bash
cd backend
gradle build --no-daemon
gradle run -Dorg.gradle.jvmargs="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" --no-daemon
```

4. Start the web frontend:

```bash
cd web
npm install
npm run dev
# open http://localhost:3000
```

5. Mobile (Expo):

```bash
cd mobile
npm install
npm run start
```

Debugging

- Java: use the VS Code launch configuration `Attach to Java Backend` (port 5005).
- Frontend: use `Launch Chrome Frontend` or the compound `Debug Full Stack (Java + Frontend)`.


Notes

- The Java backend is intentionally minimal so you can iterate quickly. It listens on port 8080 and exposes `GET /jobs`.
- The web frontend (port 3000) fetches `/jobs` from the backend. When running both locally you can keep them on the same machine.

Next steps (suggested): add persistent storage, authentication, booking flow, and CI/deployment.
add persistance task
