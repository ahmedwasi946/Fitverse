/* ============================================================
   FITVERSE AI — API client (Phase 4)
   Talks to the real Spring Boot backend from Phases 2–3.
   Change API_BASE if your backend runs somewhere other than
   localhost:8080, e.g. once you deploy it for real.
   ============================================================ */

const API_BASE = "http://localhost:8080/api";

const Api = (() => {

  function getToken() {
    try { return localStorage.getItem("fv_token"); } catch (e) { return null; }
  }

  /** Every request funnels through here: adds auth/JSON headers, throws a rich error on failure. */
  async function request(path, options = {}) {
    const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
    const token = getToken();
    if (token) headers["Authorization"] = `Bearer ${token}`;

    let response;
    try {
      response = await fetch(`${API_BASE}${path}`, { ...options, headers });
    } catch (networkErr) {
      const err = new Error("Can't reach the FitVerse AI server. Is the backend running on localhost:8080?");
      err.isNetworkError = true;
      throw err;
    }

    if (response.status === 204) return null;

    const isJson = (response.headers.get("content-type") || "").includes("application/json");
    const body = isJson ? await response.json().catch(() => null) : null;

    if (!response.ok) {
      const err = new Error((body && body.message) || `Request failed (${response.status})`);
      err.status = response.status;
      err.fieldErrors = (body && body.fieldErrors) || {};
      throw err;
    }
    return body;
  }

  const get = (path) => request(path, { method: "GET" });
  const post = (path, body) => request(path, { method: "POST", body: body !== undefined ? JSON.stringify(body) : undefined });
  const put = (path, body) => request(path, { method: "PUT", body: body !== undefined ? JSON.stringify(body) : undefined });
  const del = (path) => request(path, { method: "DELETE" });

  return {
    auth: {
      register: (name, email, password) => post("/auth/register", { name, email, password }),
      login: (email, password) => post("/auth/login", { email, password }),
      me: () => get("/auth/me")
    },
    users: {
      updateMe: (name, email, avatarUrl) => put("/users/me", { name, email, avatarUrl })
    },
    categories: {
      getAll: () => get("/categories")
    },
    products: {
      getAll: (categoryId) => get(categoryId ? `/products?categoryId=${categoryId}` : "/products"),
      getById: (id) => get(`/products/${id}`)
    },
    reviews: {
      getForProduct: (productId) => get(`/products/${productId}/reviews`),
      add: (productId, rating, comment) => post(`/products/${productId}/reviews`, { rating, comment })
    },
    cart: {
      get: () => get("/cart"),
      addItem: (productId, size, quantity) => post("/cart/items", { productId, size, quantity }),
      updateItem: (itemId, quantity) => put(`/cart/items/${itemId}`, { quantity }),
      removeItem: (itemId) => del(`/cart/items/${itemId}`),
      clear: () => del("/cart")
    },
    wishlist: {
      get: () => get("/wishlist"),
      add: (productId) => post(`/wishlist/${productId}`),
      remove: (productId) => del(`/wishlist/${productId}`)
    },
    orders: {
      place: (shippingAddress) => post("/orders", { shippingAddress }),
      getMine: () => get("/orders")
    },
    ai: {
      avatar: (photoBase64) => post("/ai/avatar", { photoBase64 }),
      tryOn: (productId, photoBase64) => post("/ai/try-on", { productId, photoBase64 }),
      stylist: (occasion, styles, budget) => post("/ai/stylist", { occasion, styles, budget })
    }
  };
})();
