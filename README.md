# Spring Boot caching-rung starter

A [Spring Boot](https://spring.io/projects/spring-boot) + JPA API that is **slow on purpose**.
It is the starting point for the Java briefs on the DayZer0 caching rung: measure how slow it
is first, then make it fast — a real WHERE clause, an index, pagination, a pool setting,
whatever your brief asks for — and prove the difference with the same load test.

What ships here, deliberately:

- An embedded H2 database seeded with **150,000 products** on startup, deterministically, so
  every clone measures the same data.
- A store (`SlowStore.java`) where **every query pays a simulated ~30ms database round-trip
  and then pulls the whole table and filters in Java**. No WHERE clause, no index, no cache.
  Do not delete the delay — your job is to stop paying it on every request, not to pretend it
  never existed.
- A **JMeter baseline** (`loadtest/baseline.jmx`) whose p95 is terrible on purpose. Run it
  before you change anything and keep the numbers.
- **No caching anywhere.** If your brief asks you to add a cache, you are adding the first one.

## Run it

```bash
./mvnw spring-boot:run
```

Requires Java 17+. The server listens on `:8080` (override with `PORT`); startup seeds the
in-memory database (a few seconds, every start). Confirm it is up:

```bash
curl http://localhost:8080/health
# {"status":"ok"}
```

Feel the problem: `curl http://localhost:8080/products/42` — every call pulls the whole table.
`SEED_ROWS` and `DB_DELAY_MS` tune the dataset size and the simulated round-trip.

## Test it

```bash
./mvnw test
```

## Measure it

```bash
./mvnw spring-boot:run                                          # terminal 1
jmeter -n -t loadtest/baseline.jmx -l loadtest/baseline.jtl     # terminal 2
```

## Infrastructure

`docker-compose.yml` starts Postgres. The scaffold runs on embedded H2 and does not use it —
moving to it is the work on the briefs that ask for it. `docker compose up -d postgres`.

## Layout

- `CatalogApplication.java` — the entry point.
- `CatalogController.java` — the routes. Add yours beside `/health` and `/products`.
- `SlowStore.java` — the deliberately slow data layer. Read its class comment before touching it.
- `Seeder.java` — the deterministic startup seeder.
- `src/test/.../CatalogApplicationTests.java` — the tests, a pattern to copy for your own code.
- `loadtest/baseline.jmx` — the JMeter baseline your pull requests quote.

## Working the tickets

Each ticket is a GitHub issue on this repo. For a work ticket: branch `issue-<N>-<slug>` off
`main`, commit your work, open a pull request with `Closes #<N>` in its body, and Raj reviews it.
An approved pull request merges and marks the ticket submitted.
