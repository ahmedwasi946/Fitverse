# FitVerse AI — Backend (Phase 3)

Java 21 · Spring Boot 4.1.0 · Spring Security 7 (JWT) · Spring Data JPA · MySQL · Maven

REST APIs, now backed by a real MySQL database via Spring Data JPA. Data
persists across restarts. `DataSeeder` still seeds the same product catalog
the Phase 1 frontend uses — but now only on the very first run, since the
database keeps what it's given.

## Run it

1. **Get a MySQL instance running.** Quickest way, via Docker:
   ```
   docker run --name fitverse-mysql -e MYSQL_ROOT_PASSWORD=root \
     -e MYSQL_DATABASE=fitverse_db -p 3306:3306 -d mysql:8
   ```
   Already have MySQL installed locally instead? Just create an empty
   `fitverse_db` database — `createDatabaseIfNotExist=true` in the JDBC URL
   handles the rest.

2. **Start the app:**
   ```
   cd backend
   mvn spring-boot:run
   ```
   Starts on **http://localhost:8080**. On first run, Hibernate creates every
   table from the `@Entity` classes and `DataSeeder` fills them.

Connection details default to `localhost:3306` / `root` / `root` — override
with the `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` environment variables for
anything else.

## Demo accounts (seeded on first run)

| Role     | Email                     | Password       |
|----------|---------------------------|----------------|
| Admin    | admin@fitverse.ai         | Admin@123      |
| Customer | sarah.chen@example.com    | Customer@123   |

## Schema

Two ways to get the schema, pick one:

- **Hibernate auto-DDL (default)** — `spring.jpa.hibernate.ddl-auto=update`
  in `application.properties`. Zero setup; Hibernate creates/updates tables
  to match the `@Entity` classes on every startup. Good for local dev.
- **Manual (`schema.sql`)** — run `src/main/resources/schema.sql` against
  your database yourself, then switch `ddl-auto` to `validate` (Hibernate
  will check the entities match and refuse to start if they don't, instead
  of trying to change anything). Better for anything closer to production.

Either way you get the same 11 tables: `users`, `categories`, `products`,
`product_images`, `product_sizes`, `addresses`, `cart_items`,
`wishlist_items`, `orders`, `order_items`, `reviews` — with foreign keys and
indexes either way (Hibernate reads the same `@Index`/`@JoinColumn`
annotations `schema.sql` was written from).

## Try it

```
# Log in (returns a JWT)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sarah.chen@example.com","password":"Customer@123"}'

# Browse products (public) — now persisted in MySQL
curl http://localhost:8080/api/products

# Call an authenticated endpoint
curl http://localhost:8080/api/cart \
  -H "Authorization: Bearer <token from login>"
```

## Endpoint map

| Domain | Base path | Notes |
|---|---|---|
| Auth | `/api/auth/*` | register, login, me — all public |
| Users | `/api/users/*` | `/me` for self; list/get/delete are admin-only |
| Products | `/api/products/*` | GET is public; write ops are admin-only |
| Categories | `/api/categories/*` | GET is public; write ops are admin-only |
| Cart | `/api/cart/*` | requires auth |
| Wishlist | `/api/wishlist/*` | requires auth |
| Orders | `/api/orders/*`, `/api/admin/orders/*` | checkout requires auth; admin routes are admin-only |
| Reviews | `/api/products/{id}/reviews`, `/api/reviews/{id}` | GET is public; write requires auth |
| Admin | `/api/admin/dashboard` | admin-only |
| AI (placeholder) | `/api/ai/*` | requires auth; every response is a stub — see `AiService` for TODOs |

Every error response shares one JSON shape (`timestamp`, `status`, `error`,
`message`, `path`, `fieldErrors`).

## What changed from Phase 2

- Every model class is now a real `@Entity`, with proper `@ManyToOne`/`@OneToMany`
  relationships instead of raw `Long` id fields.
- Every `InMemory*Repository` is gone — the repository interfaces now
  extend `JpaRepository` and Spring Data generates the implementation.
- `Product.imageUrl` (one string) became `Product.images` (a real gallery,
  via the new `ProductImage` entity/table).
- `Address` split in two: a standalone `Address` entity (a user's saved
  address book) and `ShippingAddress` (an `@Embeddable` snapshot on each
  `Order`, so editing a saved address never rewrites past order history).

## What's intentionally not here yet

- No AI logic and no AI provider API keys — see `AiService`.
- No connection between this and the Phase 1 frontend yet (Phase 4).

## Next: Phase 4

Replaces the Phase 1 frontend's placeholder `js/data.js` with real `fetch()`
calls into this API, wires the JWT into `localStorage` + an `Authorization`
header on every request, and makes every button actually do something.

