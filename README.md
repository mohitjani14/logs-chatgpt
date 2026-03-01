# Centralized Log Retrieval System (Spring Boot + Thymeleaf)

## Features
- Multi-project hierarchy: Project -> Environment -> Module -> Server -> Logs
- Modern colorful UI for end users and a dedicated admin control center
- Async queue (`BlockingQueue`) + worker thread pool (`ExecutorService`)
- Download progress, cancel support, and status polling
- SFTP download client abstraction (current demo implementation supports localhost file copy)
- Retry (3 attempts), timeout, cleanup for partial downloads
- Audit logging in `logs/audit/audit.log` with searchable admin view
- File-based config in `/config` (no database)
- Admin/User role security with Spring Security + BCrypt/Delegating encoder
- CLI for project/environment/module/server/user configuration
- Health endpoint via Spring Boot Actuator

## Architecture
- `controller/` web + REST API
- `service/` orchestration layer
- `queue/` internal async queue + dispatcher
- `worker/` threaded download execution
- `sftp/` log fetch implementation
- `audit/` append-only audit trail
- `config/` YAML config load/reload/backup store
- `security/` authz/authn
- `cli/` command entrypoint
- `util/` shared helpers (crypto, zip)

## Run locally
```bash
mvn spring-boot:run
```

Login users are in `config/users.yaml`. Temporary admin credentials for bootstrap: `admin / TempAdmin@123` (change immediately).

## Build jar
```bash
mvn clean package
java -jar target/logapp-1.0.0.jar
```

## CLI examples
```bash
java -jar target/logapp-1.0.0.jar add-project ABC
java -jar target/logapp-1.0.0.jar add-environment ABC UAT
java -jar target/logapp-1.0.0.jar add-module ABC UAT PAYMENT
java -jar target/logapp-1.0.0.jar add-server ABC UAT PAYMENT s1 localhost 22 demo sample-logs "*.log"
java -jar target/logapp-1.0.0.jar add-user alice Str0ngPass USER
java -jar target/logapp-1.0.0.jar set-temp-admin-password TempAdmin@123
java -jar target/logapp-1.0.0.jar reload-config
```

## Deployment (Linux)
1. Install Java 17.
2. Copy jar + `config/` directory.
3. Ensure `logs/` and `downloads/` writable.
4. Run as systemd service.

## Scaling guidance (toward 1000 users)
- Externalize queue to Redis/RabbitMQ + distributed workers.
- Move downloads to object storage (S3/MinIO) and serve pre-signed URLs.
- Replace session auth with JWT + API gateway.
- Add horizontal pod autoscaling and sharded audit pipeline.
