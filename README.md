# Служебный ВК бот

## Разное

`echo '' | sort | grep '\S' | copy-osc52` :: Сортировать импорты

## Сборка и запуск

`kotlin package` :: Сборка
`sh -c 'set -a; . ./.env; set +a; kotlin run --jvm-args="-Xmx384m -Dapp.artifact-id=$(yq -r ".settings.publishing.artifactId" module.yaml) -Dapp.group=$(yq -r ".settings.publishing.group" module.yaml) -Dapp.version=$(yq -r ".settings.publishing.version" module.yaml)"'` :: Запуск

## Переменные среды

| Название     | Описание                 | Пример    |
|--------------|--------------------------|-----------|
| VK_API_TOKEN | Ключ доступа сообщества  |           |
| VK_GROUP_ID  | Идентификатор сообщества | 228666999 |                     
