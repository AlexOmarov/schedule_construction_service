FROM bellsoft/liberica-openjdk-alpine:17
RUN addgroup -S docuser && adduser -S docuser -G docuser
USER docuser:docuser
VOLUME /tmp
ARG DEPENDENCY=schedule_construction_app/build/deps
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","ru/schedlab/scheduleconstruction/ScheduleConstructionApplicationKt"]
