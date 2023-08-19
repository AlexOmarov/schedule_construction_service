FROM bellsoft/liberica-openjdk-alpine:17
RUN addgroup -S docuser && adduser -S docuser -G docuser
RUN mkdir -p /var/dumps
USER docuser:docuser
VOLUME /tmp
ARG DEPENDENCY=schedule_construction_app/build/install
COPY ${DEPENDENCY}/schedule_construction_app-boot/lib /app/lib
ENTRYPOINT ["java","-cp","app:app/lib/*", "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=/var/dumps", "-jar", "/app/lib/schedule_construction_app.jar"]
EXPOSE 8080 9090