# FitVerse AI — Frontend (Phase 4)

HTML5 · CSS3 · Vanilla JS — now wired to the real Spring Boot + MySQL API
from Phases 2–3 instead of placeholder data.

## Run it

1. **Start the backend first** (see `../backend/README.md`) — it needs to
   be running on `http://localhost:8080` before this frontend can load
   anything.

2. **Serve this folder over HTTP** — don't just double-click `index.html`.
   Browsers restrict `fetch()` from `file://` pages in ways that make API
   calls unreliable. Pick one:
   ```
   npx http-server .        # or
   python3 -m http.server 5500
   ```
   Then open `http://localhost:5500`.

That's it — every page now calls the real API. Sign in with one of the
demo accounts from the backend README, or register a new one.

## What changed from Phase 1

- `js/data.js` (placeholder products) is gone — `js/api.js` is new and talks
  to `http://localhost:8080/api` for everything.
- Cart, wishlist, orders, and profile are all real now and require signing
  in — the backend has no concept of an anonymous cart, so `cart.html`,
  `wishlist.html`, `checkout.html` and `profile.html` redirect to
  `login.html` (with a `?redirect=` back to where you were) if you're
  signed out.
- Product pages now show real reviews and let signed-in users post one.
- Virtual Try-On and AI Stylist call the real (placeholder) `/api/ai/*`
  endpoints and honestly show the backend's "not implemented yet" message
  instead of Phase 1's simulated result — the AI logic itself is still
  intentionally unbuilt, per the brief.

## Pointing at a different backend

Change the one constant at the top of `js/api.js`:
```js
const API_BASE = "http://localhost:8080/api";
```

## CORS

The backend's `SecurityConfig` already allows any origin, so this works
whether you serve the frontend from `localhost`, GitHub Pages, or anywhere
else — as long as that backend URL above is actually reachable from wherever
this page is loaded.
