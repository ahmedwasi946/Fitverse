# FitVerse AI

A fashion-intelligence e-commerce platform, built in phases — now fully
wired end to end.

- **`/frontend`** — HTML5/CSS3/vanilla JS, pixel-matched to the original
  screenshots, now calling the real backend API for everything (Phase 4).
  Auto-deploys to GitHub Pages (see below).
- **`/backend`** — Java 21 + Spring Boot 4.1 REST API, MySQL via Spring
  Data JPA, JWT auth. See `backend/README.md` to run it.

Run the backend first, then the frontend (see each folder's README) —
the frontend won't have anything to show without it.

## Live frontend

Pushing to `main` automatically deploys `/frontend` to GitHub Pages via
`.github/workflows/deploy-pages.yml`. One-time setup after your first push:

1. Repo → **Settings → Pages**
2. Under **Build and deployment → Source**, choose **GitHub Actions**

That's it — no branch or folder picking needed, the workflow handles it.
Your site will be live at `https://<your-username>.github.io/<repo-name>/`.
Note: the deployed frontend can only reach a backend that's actually
reachable from wherever you're viewing the page — see `frontend/README.md`.

## Backend — entirely within GitHub (Codespaces)

GitHub itself doesn't offer persistent server hosting, so there's no
"GitHub-only" way to keep the backend running 24/7 — but **Codespaces**
gets you a live, publicly-reachable URL using nothing but a GitHub account,
for as long as you keep that codespace running:

1. Repo → **Code → Codespaces → Create codespace on main**
2. Wait for the one-time setup to finish (installs MySQL automatically —
   see `.devcontainer/devcontainer.json`)
3. In the terminal: `cd backend && mvn spring-boot:run`
4. Open the **Ports** tab, find port `8080`, right-click → **Port Visibility → Public**, copy the URL
5. Paste that URL (+ `/api`) into `API_BASE` in `frontend/js/api.js`, commit, push

Worth knowing before you rely on this: it's a free tier of **60 hours/month**,
the codespace **pauses after ~30 minutes idle** (so the URL goes dead until
you reopen and restart it), and the forwarded URL can change if you ever
recreate the codespace. It's genuinely useful for demos and sharing with
someone right now — it isn't a substitute for real hosting (Railway,
Render, Cloud Run, etc.) if you want this reliably live all the time.

## Backend — a real always-on host

See the note in the chat that generated this repo for current free-tier
options (Railway, Render, Google Cloud Run) if 24/7 uptime matters more
than "GitHub only."

