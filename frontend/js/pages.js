/* ============================================================
   FITVERSE AI — Page-specific logic (Phase 4)
   Every page now talks to the real Spring Boot API (see js/api.js)
   instead of the Phase 1 placeholder data.
   ============================================================ */

document.addEventListener("DOMContentLoaded", () => {
  const page = document.body.dataset.page;
  const initFns = {
    home: initHomePage,
    shop: initShopPage,
    product: initProductPage,
    cart: initCartPage,
    checkout: initCheckoutPage,
    wishlist: initWishlistPage,
    login: initLoginPage,
    profile: initProfilePage,
    tryon: initTryOnPage,
    stylist: initStylistPage
  };
  if (initFns[page]) initFns[page]();
});

/* ---------- shared helpers ---------- */

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str == null ? "" : String(str);
  return div.innerHTML;
}

function loginRedirect(target) {
  window.location.href = `login.html?redirect=${encodeURIComponent(target)}`;
}

/** Wishlisted product ids for the current user, or an empty set for guests. */
async function getWishlistIdSet() {
  if (!FV.auth.isLoggedIn()) return new Set();
  try {
    const items = await Api.wishlist.get();
    return new Set(items.map((i) => i.productId));
  } catch (e) {
    return new Set();
  }
}

/** Shared markup for a single product card — used on Home, Shop and Wishlist. */
function renderProductCard(product, index, wishlistIds) {
  wishlistIds = wishlistIds || new Set();
  const saved = wishlistIds.has(product.id);
  const image = (product.imageUrls && product.imageUrls[0]) || "";
  const priceHtml = product.salePrice
    ? `<span class="price"><span class="strike">${FV.money(product.price)}</span><span class="sale">${FV.money(product.salePrice)}</span></span>`
    : `<span class="price">${FV.money(product.price)}</span>`;
  return `
    <div class="product-card" style="animation-delay:${(index % 8) * 60}ms">
      <a href="product.html?id=${product.id}" data-no-transition>
        <div class="thumb">
          <img src="${image}" alt="${product.name}" loading="lazy">
          <button class="save-btn${saved ? " is-saved" : ""}" data-wish-toggle="${product.id}" aria-label="Save to wishlist">
            <span data-icon="heart"></span>
          </button>
        </div>
      </a>
      <a href="product.html?id=${product.id}" data-no-transition>
        <div class="meta-row"><span class="brand">${product.brand}</span>${priceHtml}</div>
        <h3 class="title">${product.name}</h3>
      </a>
    </div>`;
}

/** Toggles one product's wishlist membership; redirects guests to sign in first. */
async function toggleWishlist(productId, btn) {
  if (!FV.auth.isLoggedIn()) { loginRedirect(window.location.pathname.split("/").pop() || "index.html"); return; }
  const nowSaved = !btn.classList.contains("is-saved");
  try {
    if (nowSaved) await Api.wishlist.add(productId); else await Api.wishlist.remove(productId);
    btn.classList.toggle("is-saved", nowSaved);
    FV.toast(nowSaved ? "Saved to wishlist" : "Removed from wishlist", "heart");
    FV.syncHeaderBadges();
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
  }
}

/** Wires up every [data-wish-toggle] button within a container (event-delegated). */
function wireWishlistButtons(container) {
  container.addEventListener("click", (e) => {
    const btn = e.target.closest("[data-wish-toggle]");
    if (!btn) return;
    e.preventDefault();
    toggleWishlist(Number(btn.getAttribute("data-wish-toggle")), btn);
  });
}

function loadingRow(colspan) {
  return `<p style="color:var(--color-text-secondary);grid-column:1/-1;padding:var(--space-6) 0;">Loading…</p>`;
}

/* ---------- Home ---------- */
async function initHomePage() {
  const grid = document.getElementById("editGrid");
  if (!grid) return;
  grid.innerHTML = loadingRow();
  try {
    const [products, wishlistIds] = await Promise.all([Api.products.getAll(), getWishlistIdSet()]);
    const featuredIds = [5, 6, 7, 1];
    const featured = featuredIds.map((id) => products.find((p) => p.id === id)).filter(Boolean);
    grid.innerHTML = featured.map((p, i) => renderProductCard(p, i, wishlistIds)).join("");
    FV.renderIcons(grid);
    wireWishlistButtons(grid);
  } catch (e) {
    grid.innerHTML = `<p style="color:var(--color-text-secondary);grid-column:1/-1;">${FV.describeError(e)}</p>`;
  }
}

/* ---------- Shop ---------- */
async function initShopPage() {
  const grid = document.getElementById("shopGrid");
  const empty = document.getElementById("shopEmpty");
  if (!grid) return;
  grid.innerHTML = loadingRow();

  const categorySlug = new URLSearchParams(window.location.search).get("category");
  try {
    let categoryId = null;
    if (categorySlug) {
      const categories = await Api.categories.getAll();
      const match = categories.find((c) => c.slug === categorySlug);
      categoryId = match ? match.id : -1;
    }
    const [list, wishlistIds] = await Promise.all([Api.products.getAll(categoryId), getWishlistIdSet()]);

    if (list.length === 0) {
      grid.classList.add("hidden");
      empty.classList.remove("hidden");
      return;
    }
    grid.classList.remove("hidden");
    empty.classList.add("hidden");
    grid.innerHTML = list.map((p, i) => renderProductCard(p, i, wishlistIds)).join("");
    FV.renderIcons(grid);
    wireWishlistButtons(grid);
  } catch (e) {
    grid.innerHTML = `<p style="color:var(--color-text-secondary);grid-column:1/-1;">${FV.describeError(e)}</p>`;
  }
}

/* ---------- Product detail ---------- */
async function initProductPage() {
  const root = document.getElementById("pdpContent");
  if (!root) return;
  const id = Number(new URLSearchParams(window.location.search).get("id"));
  if (!id) { root.innerHTML = `<p>No product specified.</p>`; return; }

  let product;
  try {
    product = await Api.products.getById(id);
  } catch (e) {
    root.innerHTML = `<p style="color:var(--color-text-secondary);">${FV.describeError(e)}</p>`;
    return;
  }

  const wishlistIds = await getWishlistIdSet();
  const saved = wishlistIds.has(product.id);
  const image = (product.imageUrls && product.imageUrls[0]) || "";
  const priceHtml = product.salePrice
    ? `<span style="text-decoration:line-through;color:var(--color-text-muted);margin-right:8px;">${FV.money(product.price)}</span>${FV.money(product.salePrice)}`
    : FV.money(product.price);

  document.title = `${product.name} — FitVerse AI`;

  root.innerHTML = `
    <div class="pdp-image"><img src="${image}" alt="${product.name}"></div>
    <div>
      <p class="pdp-brand">${product.brand}</p>
      <h1 class="pdp-title serif">${product.name}</h1>
      <p class="pdp-price">${priceHtml}</p>
      <p class="pdp-desc">${product.description}</p>
      ${product.fitConfidence ? `<span class="match-badge"><span data-icon="sparkle"></span> ${product.fitConfidence}% Match for you</span>` : ""}
      <span class="size-label">Size</span>
      <div class="size-row" id="sizeRow">
        ${(product.sizes || []).map((s) => `<button type="button" class="size-btn" data-size="${s}">${s}</button>`).join("")}
      </div>
      <div class="pdp-actions">
        <button class="btn btn-primary" id="addToBagBtn"><span data-icon="bag"></span> Add to Bag</button>
        <button class="btn btn-outline wish-toggle-btn${saved ? " is-saved" : ""}" id="wishToggleBtn" aria-label="Save to wishlist"><span data-icon="heart"></span></button>
      </div>
      <a href="try-on.html?id=${product.id}" class="btn btn-ai btn-block"><span data-icon="camera"></span> Try it on with AI</a>
      <dl class="info-grid">
        <div><dt>Material</dt><dd>${product.material || "—"}</dd></div>
        <div><dt>Shipping</dt><dd>${product.shippingInfo || "—"}</dd></div>
        <div><dt>Returns</dt><dd>${product.returnsInfo || "—"}</dd></div>
        <div><dt>Care</dt><dd>${product.careInfo || "—"}</dd></div>
      </dl>
    </div>`;

  FV.renderIcons(root);

  let selectedSize = null;
  const sizeRow = document.getElementById("sizeRow");
  sizeRow.addEventListener("click", (e) => {
    const btn = e.target.closest(".size-btn");
    if (!btn) return;
    sizeRow.querySelectorAll(".size-btn").forEach((b) => b.classList.remove("is-selected"));
    btn.classList.add("is-selected");
    selectedSize = btn.dataset.size;
    sizeRow.classList.remove("has-error");
  });

  document.getElementById("addToBagBtn").addEventListener("click", async () => {
    if (!FV.auth.isLoggedIn()) { loginRedirect(`product.html?id=${product.id}`); return; }
    if (!selectedSize) {
      sizeRow.classList.add("has-error");
      FV.toast("Select a size first", "close");
      return;
    }
    try {
      await Api.cart.addItem(product.id, selectedSize, 1);
      FV.toast(`${product.name} (${selectedSize}) added to bag`, "bag");
      FV.syncHeaderBadges();
    } catch (e) {
      FV.toast(FV.describeError(e), "close");
    }
  });

  const wishBtn = document.getElementById("wishToggleBtn");
  wishBtn.addEventListener("click", () => toggleWishlist(product.id, wishBtn));

  initReviews(product.id);
}

async function initReviews(productId) {
  const form = document.getElementById("reviewForm");
  const prompt = document.getElementById("reviewSignInPrompt");
  if (!form) return;

  await loadReviews(productId);

  if (FV.auth.isLoggedIn()) {
    form.classList.remove("hidden");
    form.addEventListener("submit", async (e) => {
      e.preventDefault();
      FV.validate.clearAll(form);
      const commentField = form.querySelector('[data-field="comment"]');
      const commentInput = document.getElementById("reviewComment");
      if (!FV.validate.required(commentInput.value)) {
        FV.validate.setError(commentField, "Enter a comment");
        return;
      }
      const rating = Number(document.getElementById("reviewRating").value);
      const submitBtn = form.querySelector('button[type="submit"]');
      submitBtn.disabled = true;
      try {
        await Api.reviews.add(productId, rating, commentInput.value.trim());
        FV.toast("Review posted", "check");
        commentInput.value = "";
        await loadReviews(productId);
      } catch (e2) {
        FV.toast(FV.describeError(e2), "close");
      } finally {
        submitBtn.disabled = false;
      }
    });
  } else {
    prompt.classList.remove("hidden");
  }
}

async function loadReviews(productId) {
  const list = document.getElementById("reviewsList");
  const empty = document.getElementById("noReviews");
  empty.classList.add("hidden");
  try {
    const reviews = await Api.reviews.getForProduct(productId);
    if (reviews.length === 0) {
      list.innerHTML = "";
      empty.classList.remove("hidden");
    } else {
      list.innerHTML = reviews.map((r) => `
        <div class="review-item">
          <div class="stars">${"★".repeat(r.rating)}${"☆".repeat(5 - r.rating)}</div>
          <div class="review-meta"><span>${escapeHtml(r.userName)}</span><span>${new Date(r.createdAt).toLocaleDateString()}</span></div>
          <p class="review-comment">${escapeHtml(r.comment)}</p>
        </div>`).join("");
    }
  } catch (e) {
    list.innerHTML = `<p style="color:var(--color-text-secondary);font-size:14px;">${FV.describeError(e)}</p>`;
  }
}

/* ---------- Cart ---------- */
async function initCartPage() {
  const filled = document.getElementById("cartFilled");
  if (!filled) return;
  if (!FV.auth.isLoggedIn()) { loginRedirect("cart.html"); return; }
  await loadCart();
}

async function loadCart() {
  const filled = document.getElementById("cartFilled");
  const empty = document.getElementById("cartEmpty");
  const itemsRoot = document.getElementById("cartItems");

  let cart;
  try {
    cart = await Api.cart.get();
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
    return;
  }

  if (cart.items.length === 0) {
    filled.classList.add("hidden");
    empty.classList.remove("hidden");
    return;
  }
  filled.classList.remove("hidden");
  empty.classList.add("hidden");

  itemsRoot.innerHTML = cart.items.map((item) => `
    <div class="cart-item" data-item-id="${item.id}" data-qty="${item.quantity}">
      <div class="thumb"><img src="${item.imageUrl || ""}" alt="${item.name}"></div>
      <div>
        <p class="brand">${item.brand}</p>
        <h3 class="title">${item.name}</h3>
        <p class="variant">Size ${item.size}</p>
        <div class="row-bottom">
          <div class="qty-stepper">
            <button type="button" data-qty-minus aria-label="Decrease quantity">−</button>
            <span>${item.quantity}</span>
            <button type="button" data-qty-plus aria-label="Increase quantity">+</button>
          </div>
          <span class="price">${FV.money(item.lineTotal)}</span>
        </div>
        <button type="button" class="remove-btn" data-remove>Remove</button>
      </div>
    </div>`).join("");

  document.getElementById("sumSubtotal").textContent = FV.money(cart.subtotal);
  document.getElementById("sumShipping").textContent = Number(cart.shipping) === 0 ? "Free" : FV.money(cart.shipping);
  document.getElementById("sumTotal").textContent = FV.money(cart.total);

  itemsRoot.querySelectorAll(".cart-item").forEach((el) => {
    const itemId = Number(el.dataset.itemId);
    const qty = Number(el.dataset.qty);
    el.querySelector("[data-qty-plus]").addEventListener("click", () => updateCartQty(itemId, qty + 1));
    el.querySelector("[data-qty-minus]").addEventListener("click", () => updateCartQty(itemId, qty - 1));
    el.querySelector("[data-remove]").addEventListener("click", () => removeCartItem(itemId));
  });
}

async function updateCartQty(itemId, qty) {
  try {
    if (qty <= 0) await Api.cart.removeItem(itemId);
    else await Api.cart.updateItem(itemId, qty);
    await loadCart();
    FV.syncHeaderBadges();
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
  }
}

async function removeCartItem(itemId) {
  try {
    await Api.cart.removeItem(itemId);
    FV.toast("Removed from bag", "check");
    await loadCart();
    FV.syncHeaderBadges();
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
  }
}

/* ---------- Checkout ---------- */
async function initCheckoutPage() {
  const form = document.getElementById("checkoutForm");
  if (!form) return;
  if (!FV.auth.isLoggedIn()) { loginRedirect("checkout.html"); return; }

  let cart;
  try {
    cart = await Api.cart.get();
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
    return;
  }

  if (cart.items.length === 0) {
    form.closest(".cart-layout").classList.add("hidden");
    document.getElementById("checkoutEmpty").classList.remove("hidden");
    return;
  }

  document.getElementById("checkoutSummary").innerHTML = cart.items.map((i) => `
    <div class="summary-row"><span>${i.name} (${i.size}) × ${i.quantity}</span><span>${FV.money(i.lineTotal)}</span></div>
  `).join("");
  document.getElementById("sumSubtotal").textContent = FV.money(cart.subtotal);
  document.getElementById("sumShipping").textContent = Number(cart.shipping) === 0 ? "Free" : FV.money(cart.shipping);
  document.getElementById("sumTotal").textContent = FV.money(cart.total);

  form.addEventListener("submit", async (e) => {
    e.preventDefault();
    FV.validate.clearAll(form);
    let valid = true;
    form.querySelectorAll("[data-required]").forEach((field) => {
      const input = field.querySelector("input, select");
      if (!FV.validate.required(input.value)) { FV.validate.setError(field, "This field is required"); valid = false; }
    });
    const cardField = form.querySelector("[data-card]");
    const cardInput = cardField.querySelector("input");
    if (valid && !/^[0-9\s]{12,19}$/.test(cardInput.value.trim())) {
      FV.validate.setError(cardField, "Enter a valid card number");
      valid = false;
    }
    if (!valid) return;

    const shippingAddress = {
      fullName: form.fullName.value.trim(),
      line1: form.address.value.trim(),
      city: form.city.value.trim(),
      zip: form.zip.value.trim(),
      country: form.country.value
    };

    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    try {
      const order = await Api.orders.place(shippingAddress);
      form.closest(".cart-layout").classList.add("hidden");
      const successPanel = document.getElementById("checkoutSuccess");
      successPanel.classList.remove("hidden");
      successPanel.querySelector("#orderNumber").textContent = order.orderNumber;
      FV.syncHeaderBadges();
    } catch (e2) {
      FV.toast(FV.describeError(e2), "close");
      submitBtn.disabled = false;
    }
  });
}

/* ---------- Wishlist ---------- */
async function initWishlistPage() {
  const grid = document.getElementById("wishlistGrid");
  if (!grid) return;
  if (!FV.auth.isLoggedIn()) { loginRedirect("wishlist.html"); return; }

  grid.addEventListener("click", async (e) => {
    const btn = e.target.closest("[data-wish-toggle]");
    if (!btn) return;
    e.preventDefault();
    const productId = Number(btn.getAttribute("data-wish-toggle"));
    try {
      await Api.wishlist.remove(productId);
      FV.toast("Removed from wishlist", "heart");
      FV.syncHeaderBadges();
      await loadWishlist();
    } catch (e2) {
      FV.toast(FV.describeError(e2), "close");
    }
  });

  await loadWishlist();
}

async function loadWishlist() {
  const grid = document.getElementById("wishlistGrid");
  const empty = document.getElementById("wishlistEmpty");
  try {
    const items = await Api.wishlist.get();
    if (items.length === 0) {
      grid.classList.add("hidden");
      empty.classList.remove("hidden");
      return;
    }
    grid.classList.remove("hidden");
    empty.classList.add("hidden");
    const ids = new Set(items.map((i) => i.productId));
    grid.innerHTML = items.map((p, i) => renderProductCard(
      { id: p.productId, name: p.name, brand: p.brand, imageUrls: [p.imageUrl], price: p.price, salePrice: p.salePrice },
      i, ids
    )).join("");
    FV.renderIcons(grid);
  } catch (e) {
    FV.toast(FV.describeError(e), "close");
  }
}

/* ---------- Login / Register ---------- */
async function initLoginPage() {
  const redirect = new URLSearchParams(window.location.search).get("redirect") || "profile.html";
  if (FV.auth.isLoggedIn()) { window.location.href = redirect; return; }

  const intro = document.getElementById("authIntro");
  const panel = document.getElementById("authPanel");
  document.getElementById("beginBtn").addEventListener("click", () => {
    intro.classList.add("hidden");
    panel.classList.remove("hidden");
  });

  const tabs = document.querySelectorAll(".auth-tab");
  const forms = document.querySelectorAll(".auth-form");
  function showTab(name) {
    tabs.forEach((t) => t.classList.toggle("is-active", t.dataset.tab === name));
    forms.forEach((f) => f.classList.toggle("is-active", f.dataset.form === name));
  }
  tabs.forEach((tab) => tab.addEventListener("click", () => showTab(tab.dataset.tab)));
  document.querySelectorAll("[data-switch]").forEach((btn) => {
    btn.addEventListener("click", () => {
      intro.classList.add("hidden");
      panel.classList.remove("hidden");
      showTab(btn.dataset.switch);
    });
  });

  document.getElementById("signinForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    FV.validate.clearAll(form);
    const emailField = form.querySelector('[data-field="email"]');
    const passField = form.querySelector('[data-field="password"]');
    let valid = true;
    if (!FV.validate.email(emailField.querySelector("input").value)) { FV.validate.setError(emailField, "Enter a valid email address"); valid = false; }
    if (!FV.validate.minLength(passField.querySelector("input").value, 6)) { FV.validate.setError(passField, "Password must be at least 6 characters"); valid = false; }
    if (!valid) return;

    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    try {
      await FV.auth.login(emailField.querySelector("input").value.trim(), passField.querySelector("input").value);
      FV.toast("Welcome back!", "check");
      setTimeout(() => { window.location.href = redirect; }, 400);
    } catch (err) {
      submitBtn.disabled = false;
      if (err.status === 401) FV.validate.setError(passField, "Incorrect email or password");
      else FV.toast(FV.describeError(err), "close");
    }
  });

  document.getElementById("signupForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    FV.validate.clearAll(form);
    const nameField = form.querySelector('[data-field="name"]');
    const emailField = form.querySelector('[data-field="email"]');
    const passField = form.querySelector('[data-field="password"]');
    const confirmField = form.querySelector('[data-field="confirm"]');
    let valid = true;
    if (!FV.validate.required(nameField.querySelector("input").value)) { FV.validate.setError(nameField, "Enter your name"); valid = false; }
    if (!FV.validate.email(emailField.querySelector("input").value)) { FV.validate.setError(emailField, "Enter a valid email address"); valid = false; }
    if (!FV.validate.minLength(passField.querySelector("input").value, 6)) { FV.validate.setError(passField, "Password must be at least 6 characters"); valid = false; }
    if (passField.querySelector("input").value !== confirmField.querySelector("input").value) { FV.validate.setError(confirmField, "Passwords don't match"); valid = false; }
    if (!valid) return;

    const submitBtn = form.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    try {
      await FV.auth.register(
        nameField.querySelector("input").value.trim(),
        emailField.querySelector("input").value.trim(),
        passField.querySelector("input").value
      );
      FV.toast("Account created!", "check");
      setTimeout(() => { window.location.href = redirect; }, 400);
    } catch (err) {
      submitBtn.disabled = false;
      if (err.status === 409) FV.validate.setError(emailField, "An account with this email already exists");
      else FV.toast(FV.describeError(err), "close");
    }
  });
}

/* ---------- Profile ---------- */
async function initProfilePage() {
  if (!FV.auth.isLoggedIn()) { loginRedirect("profile.html"); return; }

  let user;
  try {
    user = await Api.auth.me();
  } catch (e) {
    if (e.status === 401) { FV.auth.logout(); loginRedirect("profile.html"); return; }
    user = FV.auth.getUser() || { name: "", email: "" };
  }

  document.getElementById("profileAvatarInitial").textContent = FV.initial(user.name);
  document.getElementById("profileName").textContent = user.name;
  document.getElementById("profileEmail").textContent = user.email;
  document.getElementById("accountName").value = user.name;
  document.getElementById("accountEmail").value = user.email;
  if (user.avatarUrl) {
    document.getElementById("avatarPreview").innerHTML = `<img src="${user.avatarUrl}" alt="Your AI avatar photo">`;
  }

  const orderRoot = document.getElementById("orderHistory");
  try {
    const orders = await Api.orders.getMine();
    orderRoot.innerHTML = orders.length === 0
      ? `<p style="color:var(--color-text-secondary);font-size:14px;">No orders yet — once you check out, they'll show up here.</p>`
      : orders.map((o) => `
        <div class="order-row">
          <span>${o.orderNumber} · ${new Date(o.createdAt).toLocaleDateString()}</span>
          <span class="order-status">${o.status}</span>
          <span>${FV.money(o.total)}</span>
        </div>`).join("");
  } catch (e) {
    orderRoot.innerHTML = `<p style="color:var(--color-text-secondary);font-size:14px;">${FV.describeError(e)}</p>`;
  }

  const avatarInput = document.getElementById("avatarUploadInput");
  const avatarPreview = document.getElementById("avatarPreview");
  avatarInput.addEventListener("change", () => {
    const file = avatarInput.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = async () => {
      avatarPreview.innerHTML = `<img src="${reader.result}" alt="Your AI avatar photo">`;
      const base64 = reader.result.includes(",") ? reader.result.split(",")[1] : reader.result;
      try {
        const res = await Api.ai.avatar(base64);
        FV.toast(res.message, "sparkle");
      } catch (e) {
        FV.toast(FV.describeError(e), "close");
      }
    };
    reader.readAsDataURL(file);
  });

  document.getElementById("accountForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const form = e.target;
    FV.validate.clearAll(form);
    const nameField = form.querySelector('[data-field="name"]');
    const emailField = form.querySelector('[data-field="email"]');
    let valid = true;
    if (!FV.validate.required(nameField.querySelector("input").value)) { FV.validate.setError(nameField, "Enter your name"); valid = false; }
    if (!FV.validate.email(emailField.querySelector("input").value)) { FV.validate.setError(emailField, "Enter a valid email address"); valid = false; }
    if (!valid) return;
    try {
      const updated = await FV.auth.updateUser(
        nameField.querySelector("input").value.trim(),
        emailField.querySelector("input").value.trim(),
        user.avatarUrl
      );
      document.getElementById("profileName").textContent = updated.name;
      document.getElementById("profileEmail").textContent = updated.email;
      FV.syncHeaderBadges();
      FV.toast("Account details saved", "check");
    } catch (err) {
      if (err.status === 409) FV.validate.setError(emailField, "An account with this email already exists");
      else FV.toast(FV.describeError(err), "close");
    }
  });

  document.getElementById("logoutBtn").addEventListener("click", () => {
    FV.auth.logout();
    window.location.href = "index.html";
  });
}

/* ---------- Virtual Try-On ---------- */
async function initTryOnPage() {
  const select = document.getElementById("tryonProductSelect");

  try {
    const products = await Api.products.getAll();
    select.innerHTML = products.map((p) => `<option value="${p.id}">${p.brand} — ${p.name}</option>`).join("");
    const presetId = Number(new URLSearchParams(window.location.search).get("id"));
    if (presetId) select.value = presetId;
  } catch (e) {
    select.innerHTML = `<option>Unable to load products</option>`;
  }

  const dropzone = document.getElementById("dropzone");
  const photoInput = document.getElementById("photoInput");
  const seeFitBtn = document.getElementById("seeFitBtn");
  let uploadedPhoto = null;

  function handleFile(file) {
    if (!file || !file.type.startsWith("image/")) return;
    const reader = new FileReader();
    reader.onload = () => {
      uploadedPhoto = reader.result;
      dropzone.innerHTML = `<img class="preview-img" src="${uploadedPhoto}" alt="Your uploaded photo">`;
      seeFitBtn.disabled = false;
    };
    reader.readAsDataURL(file);
  }

  dropzone.addEventListener("click", () => photoInput.click());
  photoInput.addEventListener("change", () => handleFile(photoInput.files[0]));
  ["dragover", "dragleave", "drop"].forEach((evt) => {
    dropzone.addEventListener(evt, (e) => {
      e.preventDefault();
      dropzone.classList.toggle("is-dragover", evt === "dragover");
      if (evt === "drop") handleFile(e.dataTransfer.files[0]);
    });
  });

  seeFitBtn.addEventListener("click", async () => {
    if (!FV.auth.isLoggedIn()) { loginRedirect("try-on.html"); return; }
    document.getElementById("tryonUploadStep").classList.add("hidden");
    document.getElementById("tryonLoading").classList.remove("hidden");
    try {
      const productId = Number(select.value);
      const base64 = uploadedPhoto.includes(",") ? uploadedPhoto.split(",")[1] : uploadedPhoto;
      const res = await Api.ai.tryOn(productId, base64);
      document.getElementById("tryonLoading").classList.add("hidden");
      const result = document.getElementById("tryonResult");
      result.classList.remove("hidden");
      document.getElementById("resultImg").src = uploadedPhoto;
      document.getElementById("resultBadge").innerHTML = `${FV.ICONS.sparkle}<span>${res.message}</span>`;
    } catch (e) {
      document.getElementById("tryonLoading").classList.add("hidden");
      document.getElementById("tryonUploadStep").classList.remove("hidden");
      FV.toast(FV.describeError(e), "close");
    }
  });

  document.getElementById("tryAgainBtn").addEventListener("click", () => {
    document.getElementById("tryonResult").classList.add("hidden");
    document.getElementById("tryonUploadStep").classList.remove("hidden");
    dropzone.innerHTML = `<div class="icon-wrap" data-icon="upload"></div><p><strong>Click to upload</strong> or drag a full-length photo here</p><input type="file" id="photoInput" accept="image/*">`;
    FV.renderIcons(dropzone);
    document.getElementById("photoInput").addEventListener("change", () => handleFile(document.getElementById("photoInput").files[0]));
    seeFitBtn.disabled = true;
    uploadedPhoto = null;
  });
}

/* ---------- AI Stylist ---------- */
async function initStylistPage() {
  document.querySelectorAll("#styleChips .chip").forEach((chip) => {
    chip.addEventListener("click", () => chip.classList.toggle("is-selected"));
  });

  document.getElementById("getRecsBtn").addEventListener("click", async () => {
    if (!FV.auth.isLoggedIn()) { loginRedirect("stylist.html"); return; }

    document.getElementById("stylistForm").classList.add("hidden");
    document.getElementById("stylistLoading").classList.remove("hidden");

    const occasion = document.getElementById("occasionSelect").value;
    const styles = Array.from(document.querySelectorAll("#styleChips .chip.is-selected")).map((c) => c.dataset.chip);
    const budget = document.getElementById("budgetSelect").value;

    try {
      const res = await Api.ai.stylist(occasion, styles, budget);
      document.getElementById("stylistLoading").classList.add("hidden");
      document.getElementById("stylistResults").classList.remove("hidden");
      document.getElementById("recsGrid").innerHTML = `<div class="stub-notice">${FV.ICONS.sparkle}<span>${res.message}</span></div>`;
    } catch (e) {
      document.getElementById("stylistLoading").classList.add("hidden");
      document.getElementById("stylistForm").classList.remove("hidden");
      FV.toast(FV.describeError(e), "close");
    }
  });

  document.getElementById("restyleBtn").addEventListener("click", () => {
    document.getElementById("stylistResults").classList.add("hidden");
    document.getElementById("stylistForm").classList.remove("hidden");
  });
}
