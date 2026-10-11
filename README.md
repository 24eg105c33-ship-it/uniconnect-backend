# UniConnect Backend — ANURAG UNIVERSITY

Java 17 + Spring Boot 3 REST API.

- Register / login restricted to `@anurag.edu.in` (configurable: `app.allowed-domain`)
- BCrypt password hashing, JWT sessions (7 days)
- Posts, likes (toggle), comments, shares, student search and suggestions
- H2 in-memory DB by default (demo); PostgreSQL in production via env vars

## Run locally
```bash
mvn spring-boot:run        # http://localhost:8080/api/health
```

## Environment variables
| Variable | Purpose |
|---|---|
| `JWT_SECRET` | **Required in production**, 32+ random characters |
| `CORS_ORIGINS` | Frontend origin(s), comma-separated, e.g. `https://user.github.io` |
| `DB_URL`, `DB_USER`, `DB_PASSWORD` | PostgreSQL, e.g. `jdbc:postgresql://host:5432/uniconnect` |
| `PORT` | HTTP port (default 8080) |

## API
`POST /api/auth/register` · `POST /api/auth/login` · `GET /api/me` · `GET|POST /api/posts` ·
`POST /api/posts/{id}/like` · `POST /api/posts/{id}/share` · `GET|POST /api/posts/{id}/comments` ·
`GET /api/users/search?q=` · `GET /api/users/suggestions`

All endpoints except `/api/auth/**` and `/api/health` need `Authorization: Bearer <token>`.

## Note on email verification
The domain check proves the address *looks* like a college address, not that the person owns it.
For real use, add an emailed one-time code (or university-approved verification) before activating accounts.
