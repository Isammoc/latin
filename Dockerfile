FROM openjdk:11-slim
COPY ./stage /app
WORKDIR /app
ENTRYPOINT ["/bin/bash", "/app/bin/latin"]
