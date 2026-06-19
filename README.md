# analytics-service

Агрегация и экспорт данных опросов АСНИ. Вызывает `survey-service` для получения данных.

## Запуск

```bash
docker compose up -d
```

Swagger: http://localhost:8084/swagger-ui.html

Требует запущенный `survey-service` (`http://localhost:8083`).

## Тесты

```bash
./gradlew test
```

Docker не нужен (нет базы данных).
