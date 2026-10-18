FROM eclipse-temurin:25-jdk-resolute AS build
WORKDIR /usr/local/src/app/

COPY ./src/ ./src/
COPY ./module.yaml github.creds.properties ./

RUN apt update && apt install -y curl
RUN curl -fsSL https://kotl.in/install.sh | sh
RUN /root/.local/bin/kotlin package -f executable-jar

FROM gcr.io/distroless/java25-debian13

COPY --from=build /usr/local/src/app/build/tasks/*executableJarJvm/*.jar /app.jar

ARG ARTIFACT_ID=vkbot
ARG GROUP=ru.senioravanti
ARG VERSION
# https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html#using-the-jdk_java_options-launcher-environment-variable
# JDK_JAVA_OPTIONS :: способ задать аргументы jvm через переменную среды
ENV JDK_JAVA_OPTIONS="-Dapp.artifact-id=${ARTIFACT_ID} -Dapp.group=${GROUP} -Dapp.version=${VERSION}"

ENTRYPOINT ["java","--enable-preview","-Xmx384m","-jar","/app.jar"]