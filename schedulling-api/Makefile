.PHONY: setup start stop logs test build

# Targets delegated by the root Makefile (make setup / make start-api / make stop / make logs).
# Kept minimal and safe: wraps the Maven Wrapper / docker compose commands already documented
# in .claude/CLAUDE.md — does not introduce a new build/run strategy.

setup:
	@echo "[schedulling-api] Installing dependencies (mvnw)..."
	./mvnw -q -DskipTests dependency:go-offline

start:
	@echo "[schedulling-api] Starting API locally (mvnw spring-boot:run)..."
	./mvnw spring-boot:run

stop:
	@echo "[schedulling-api] Stopping containers (docker compose)..."
	cd .. && docker compose stop api

logs:
	@echo "[schedulling-api] Tailing API container logs..."
	cd .. && docker compose logs -f api

test:
	./mvnw test

build:
	./mvnw clean package -DskipTests
