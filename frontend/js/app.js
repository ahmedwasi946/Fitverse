/* ============================================================
   FITVERSE AI — Core App Shell (Phase 4)
   Shared across every page: real auth/session state (backed by
   the Spring Boot API + a JWT in localStorage), icons, header
   wiring, toasts, transitions, validation helpers.
   ============================================================ */

const FV = (() => {

  /* ---------- storage helpers ---------- */
  function readJSON(key, fallback) {
    try {
      const raw = localStorage.getItem(key);
      return raw ? JSON.parse(raw) : fallback;
    } catch (e) {
      return fallback;
    }
  }
  function writeJSON(key, value) {
    try { localStorage.setItem(key, JSON.stringify(value)); } catch (e) { /* storage unavailable */ }
  }

  const KEYS = { TOKEN: "fv_token", USER: "fv_user" };

  /* ---------- icon set (inline SVG, stroke-based) ---------- */
  const ICONS = {
    heart: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 1 0-7.8 7.8l1 1L12 21l7.8-7.6 1-1a5.5 5.5 0 0 0 0-7.8Z"/></svg>`,
    bag: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><path d="M6 8h12l1 13H5L6 8Z"/><path d="M9 8V6a3 3 0 0 1 6 0v2"/></svg>`,
    user: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="8" r="3.6"/><path d="M4.5 20c1.4-3.8 4.4-5.8 7.5-5.8s6.1 2 7.5 5.8"/></svg>`,
    camera: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M4 8h3l1.6-2.4h6.8L17 8h3a1 1 0 0 1 1 1v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V9a1 1 0 0 1 1-1Z"/><circle cx="12" cy="13" r="3.4"/></svg>`,
    wand: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="m6 18 12-12"/><path d="M15 3h1.2M20 6.8V8M20 3l-.6.6M9 21l.6-2M4 15h1.6M3 4l1.6 1.6"/></svg>`,
    sparkle: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M12 3v4M12 17v4M3 12h4M17 12h4M6 6l2 2M16 16l2 2M18 6l-2 2M8 16l-2 2"/><path d="M12 8a4 4 0 0 0 4 4 4 4 0 0 0-4 4 4 4 0 0 0-4-4 4 4 0 0 0 4-4Z"/></svg>`,
    arrowRight: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14M13 6l6 6-6 6"/></svg>`,
    chevronLeft: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M15 18l-6-6 6-6"/></svg>`,
    close: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="M6 6l12 12M18 6 6 18"/></svg>`,
    plus: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M12 5v14M5 12h14"/></svg>`,
    minus: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><path d="M5 12h14"/></svg>`,
    check: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"><path d="m5 13 4 4L19 7"/></svg>`,
    upload: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M12 16V4M7 9l5-5 5 5"/><path d="M4 16v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3"/></svg>`,
    box: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"><path d="M21 8 12 3 3 8l9 5 9-5Z"/><path d="M3 8v8l9 5 9-5V8M12 13v8"/></svg>`
  };

  function renderIcons(scope) {
    (scope || document).querySelectorAll("[data-icon]").forEach((el) => {
      const name = el.getAttribute("data-icon");
      if (ICONS[name]) el.innerHTML = ICONS[name];
    });
  }

  function initial(name) { return (name || "U").trim()[0].toUpperCase(); }

  /* ---------- auth — real JWT session against the Spring Boot API ---------- */
  const auth = {
    isLoggedIn() { return !!readJSON(KEYS.TOKEN, null); },
    getUser() { return readJSON(KEYS.USER, null); },
    getToken() { return readJSON(KEYS.TOKEN, null); },
    async login(email, password) {
      const res = await Api.auth.login(email, password);
      writeJSON(KEYS.TOKEN, res.token);
      writeJSON(KEYS.USER, res.user);
      return res.user;
    },
    async register(name, email, password) {
      const res = await Api.auth.register(name, email, password);
      writeJSON(KEYS.TOKEN, res.token);
      writeJSON(KEYS.USER, res.user);
      return res.user;
    },
    async updateUser(name, email, avatarUrl) {
      const user = await Api.users.updateMe(name, email, avatarUrl);
      writeJSON(KEYS.USER, user);
      return user;
    },
    logout() { localStorage.removeItem(KEYS.TOKEN); localStorage.removeItem(KEYS.USER); }
  };

  /* ---------- header badges + avatar (fetches live counts when signed in) ---------- */
  async function syncHeaderBadges() {
    const cartBadge = document.getElementById("cartBadge");
    const wishBadge = document.getElementById("wishlistBadge");
    const avatarBtn = document.getElementById("avatarBtn");
    const user = auth.getUser();

    if (avatarBtn) {
      if (user) {
        avatarBtn.href = "profile.html";
        avatarBtn.classList.remove("is-guest");
        avatarBtn.innerHTML = `<span>${initial(user.name)}</span>`;
      } else {
        avatarBtn.href = "login.html";
        avatarBtn.classList.add("is-guest");
        avatarBtn.innerHTML = ICONS.user;
      }
    }

    if (!auth.isLoggedIn()) {
      if (cartBadge) cartBadge.hidden = true;
      if (wishBadge) wishBadge.hidden = true;
      return;
    }

    try {
      const [cart, wishlist] = await Promise.all([Api.cart.get(), Api.wishlist.get()]);
      const c = cart.items.reduce((n, i) => n + i.quantity, 0);
      if (cartBadge) { cartBadge.textContent = c; cartBadge.hidden = c === 0; }
      if (wishBadge) { wishBadge.textContent = wishlist.length; wishBadge.hidden = wishlist.length === 0; }
    } catch (e) {
      // Session likely expired server-side — fall back to a signed-out header rather than break the page.
      if (e.status === 401) auth.logout();
    }
  }

  /* ---------- toast notifications ---------- */
  function toast(message, icon = "check") {
    let root = document.getElementById("toast-root");
    if (!root) {
      root = document.createElement("div");
      root.id = "toast-root";
      document.body.appendChild(root);
    }
    const el = document.createElement("div");
    el.className = "toast";
    el.innerHTML = `${ICONS[icon] || ICONS.check}<span>${message}</span>`;
    root.appendChild(el);
    setTimeout(() => {
      el.classList.add("leaving");
      setTimeout(() => el.remove(), 280);
    }, 2400);
  }

  /* ---------- validation helpers ---------- */
  const validate = {
    email(v) { return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v.trim()); },
    required(v) { return v !== undefined && v !== null && String(v).trim().length > 0; },
    minLength(v, n) { return String(v || "").trim().length >= n; },
    setError(fieldEl, message) {
      fieldEl.classList.add("has-error");
      const msg = fieldEl.querySelector(".error-msg");
      if (msg) msg.textContent = message;
    },
    clearError(fieldEl) { fieldEl.classList.remove("has-error"); },
    clearAll(formEl) { formEl.querySelectorAll(".field.has-error").forEach((f) => f.classList.remove("has-error")); }
  };

  /* ---------- reveal-on-scroll ---------- */
  function initReveal() {
    const els = document.querySelectorAll(".reveal");
    if (!("IntersectionObserver" in window) || els.length === 0) {
      els.forEach((el) => el.classList.add("is-visible"));
      return;
    }
    const io = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("is-visible");
          io.unobserve(entry.target);
        }
      });
    }, { threshold: 0.12 });
    els.forEach((el) => io.observe(el));
  }

  /* ---------- smooth page-to-page transitions ---------- */
  function initPageTransitions() {
    requestAnimationFrame(() => document.body.classList.add("is-ready"));
    document.addEventListener("click", (e) => {
      const link = e.target.closest("a[href]");
      if (!link) return;
      const href = link.getAttribute("href");
      if (!href || href.startsWith("#") || href.startsWith("http") || href.startsWith("mailto:") || link.target === "_blank" || link.hasAttribute("data-no-transition")) return;
      e.preventDefault();
      document.body.classList.add("is-leaving");
      setTimeout(() => { window.location.href = href; }, 170);
    });
  }

  /* ---------- simple modal system (Help / Returns / Contact) ---------- */
  const MODAL_CONTENT = {
    help: { title: "Help Center", body: "Questions about sizing, materials, or an order in progress? Our team replies within one business day at support@fitverse.ai." },
    returns: { title: "Returns", body: "Every full-price piece can be returned within 30 days, unworn and with tags attached, for a full refund to your original payment method." },
    contact: { title: "Contact", body: "Reach the FitVerse AI team at support@fitverse.ai, or write to 148 Mercer Street, New York, NY 10012." }
  };
  function initModals() {
    let overlay = document.querySelector(".modal-overlay");
    if (!overlay) {
      overlay = document.createElement("div");
      overlay.className = "modal-overlay";
      overlay.innerHTML = `<div class="modal-box"><button class="icon-btn modal-close" data-close><span data-icon="close"></span></button><h2 id="modalTitle"></h2><p id="modalBody"></p></div>`;
      document.body.appendChild(overlay);
      renderIcons(overlay);
      overlay.addEventListener("click", (e) => { if (e.target === overlay || e.target.closest("[data-close]")) closeModal(); });
    }
    document.querySelectorAll("[data-modal]").forEach((trigger) => {
      trigger.addEventListener("click", (e) => {
        e.preventDefault();
        const key = trigger.getAttribute("data-modal");
        const content = MODAL_CONTENT[key];
        if (!content) return;
        overlay.querySelector("#modalTitle").textContent = content.title;
        overlay.querySelector("#modalBody").textContent = content.body;
        overlay.classList.add("is-open");
      });
    });
  }
  function closeModal() { document.querySelector(".modal-overlay")?.classList.remove("is-open"); }

  /* ---------- format helpers ---------- */
  function money(n) { return "$" + Number(n).toFixed(0); }

  /** Renders a friendly message for a thrown Api error, including the "backend not running" case. */
  function describeError(err) {
    return err && err.message ? err.message : "Something went wrong. Please try again.";
  }

  /* ---------- boot ---------- */
  function init() {
    renderIcons();
    syncHeaderBadges();
    initReveal();
    initPageTransitions();
    initModals();
  }
  document.addEventListener("DOMContentLoaded", init);

  return { auth, toast, validate, money, renderIcons, syncHeaderBadges, describeError, initial, ICONS };
})();
