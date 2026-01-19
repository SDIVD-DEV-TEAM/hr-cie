FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
ARG VERSION
WORKDIR /workspace
COPY . /workspace

RUN mvn clean package -Dmaven.test.skip -Drevision=${VERSION}

FROM openjdk:22-jdk-slim
ARG VERSION
LABEL author="Sminth"

# Install libfreetype6
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl libfreetype6 fontconfig fonts-dejavu-core \
    && apt-get clean &&  rm -rf /var/lib/apt/lists/*

# Create the debian user
RUN useradd -ms /bin/bash dctd

USER dctd
WORKDIR /usr/app/hr-cie-api
RUN chown -R dctd:dctd /usr/app/hr-cie-api

EXPOSE 8090
COPY --from=builder --chown=dctd:dctd --chmod=755 /workspace/target/hr-${VERSION}.jar ./hr-cie-api.jar

ENTRYPOINT ["java", "-server", "-jar", "./hr-cie-api.jar"]

# Healthcheck configuration
HEALTHCHECK --interval=60s --timeout=20s --retries=3 CMD curl --fail http://localhost:8090/actuator/health || exit 1
