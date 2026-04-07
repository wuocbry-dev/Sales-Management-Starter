# MySQL database

## Files
- `migrations/V1__init_schema.sql`: full schema MySQL 8
- `seeds/sample_data.sql`: sample data

## Import manually
```sql
SOURCE database/migrations/V1__init_schema.sql;
SOURCE database/seeds/sample_data.sql;
```

## Run with Docker
```bash
cd infra/compose
docker compose -f docker-compose.dev.yml up -d
```

## Run Spring Boot with MySQL profile
```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## Default DB config
- Database: `sales_management`
- Host: `localhost`
- Port: `3306`
- Root password: `root123`
- App user: `app_user`
- App password: `app_password`
