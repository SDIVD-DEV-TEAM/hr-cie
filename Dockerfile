FROM maven:3.9.6-eclipse-temurin-17-focal AS builder
ARG VERSION
WORKDIR /workspace
COPY . /workspace

RUN mvn clean package -Dmaven.test.skip -Drevision=${VERSION}

FROM openjdk:22-jdk-slim
ARG VERSION
LABEL author="Guy Alexis TAMBIE"

# Install libfreetype6
USER root
RUN apt-get update && apt-get install -y --no-install-recommends curl=7.88.1-10+deb12u8 libfreetype6=2.12.1+dfsg-5+deb12u3 fontconfig=2.14.1-4 fonts-dejavu-core=2.37-6 \
    && apt-get clean &&  rm -rf /var/lib/apt/lists/*

# Create the debian user
RUN useradd -ms /bin/bash everest

USER everest
WORKDIR /usr/app/hr-cie-api
RUN chown -R everest:everest /usr/app/hr-cie-api

EXPOSE 8080
COPY --from=builder --chown=everest:everest --chmod=755 /workspace/target/hr-${VERSION}.jar ./hr-cie-api.jar

ENTRYPOINT ["java", "-server", "-jar", "./hr-cie-api.jar"]

# Healthcheck configuration
HEALTHCHECK --interval=60s --timeout=20s --retries=3 CMD curl --fail http://localhost:8080/actuator/health || exit 1
