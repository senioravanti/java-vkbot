# Служебный ВК бот

## Разное

`echo '' | sort | grep '\S' | copy-osc52` :: Сортировать импорты

## Сборка и запуск

### Локально

`kotlin package -f executable-jar` :: Сборка
`sh -c 'set -a; . ./.env; set +a; kotlin run --jvm-args="--enable-preview -Xmx384m -Dapp.artifact-id=$(yq -r ".settings.publishing.artifactId" module.yaml) -Dapp.group=$(yq -r ".settings.publishing.group" module.yaml) -Dapp.version=$(yq -r ".settings.publishing.version" module.yaml)"'` :: Запуск

### Docker

`docker rmi <tag>` :: Удалить тэг
`docker rm -f <container-name | container-id>` :: Удалить контейнер

```sh
version="$(yq -r '.settings.publishing.version' module.yaml)" \
artifact_id="$(yq -r ".settings.publishing.artifactId" module.yaml)" \
group="$(yq -r ".settings.publishing.group" module.yaml)" \
sh -c 'docker build --progress=plain \
    --build-arg "ARTIFACT_ID=${artifact_id}" \
    --build-arg "GROUP=${group}" \
    --build-arg "VERSION=${version}" \
    -t "test-vkbot:v${version}" \
    -t "test-vkbot:latest" .'

docker run -d --name 'test-vkbot' \
    --restart always \
    --env-file ".env" \
    'test-vkbot:latest'

docker logs -f 'test-vkbot'
```

## Переменные среды

| Название     | Описание                 | Пример    |
|--------------|--------------------------|-----------|
| VK_API_TOKEN | Ключ доступа сообщества  |           |
| VK_GROUP_ID  | Идентификатор сообщества | 228666999 |                     
