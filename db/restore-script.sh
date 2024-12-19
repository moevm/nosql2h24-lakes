#!/bin/bash
set -e

# Указываем путь к дампу
DUMP_PATH="/backups/neo4j.dump"
DATABASE_NAME="neo4j"
DATABASE_PATH="/var/lib/neo4j/data/databases/$DATABASE_NAME"

# Проверка наличия дампа
if [ -f "$DUMP_PATH" ]; then
  echo "Дамп найден: $DUMP_PATH"

  # Останавливаем сервер Neo4j перед восстановлением
  echo "Останавливаем сервер Neo4j..."
  neo4j stop || echo "Сервер уже остановлен или не работает"

  # Если база данных существует, удаляем ее
  if [ -d "$DATABASE_PATH" ]; then
    echo "Удаляем старую базу данных..."
    rm -rf "$DATABASE_PATH"
  fi

  # Восстанавливаем базу данных из дампа
  echo "Восстанавливаем базу данных из дампа..."
  neo4j-admin database load --from-path="/backups" --overwrite-destination=true "$DATABASE_NAME"

  echo "База данных восстановлена успешно."
else
  echo "Дамп не найден. Пропуск восстановления."
fi

# Настройка пароля для пользователя neo4j (можно настроить через переменные окружения)
echo "Настроим пароль пользователя neo4j..."
neo4j-admin dbms set-initial-password "${NEO4J_PASSWORD:-neo4jneo4j}" || echo "Пароль уже настроен"

# Запуск Neo4j в консольном режиме
echo "Запускаем Neo4j..."
exec neo4j console
