FROM bellsoft/liberica-openjdk-alpine:17
RUN addgroup -S docuser && adduser -S docuser -G docuser
USER docuser:docuser
VOLUME /tmp
ARG DEPENDENCY=schedule_construction_app/build/install
COPY ${DEPENDENCY}/schedule_construction_app-boot/lib /app/lib
ENTRYPOINT ["java","-cp","app:app/lib/*", "-jar", "/app/schedule_construction_app.jar"]
