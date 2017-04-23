FROM java:8-jdk

LABEL name="tg2vk-sever" \
      maintainer="aliaksandr.babai@gmail.com"

ARG ROOT_DIR=/usr/workspace
ARG WORK_DIR=$ROOT_DIR/tg2vk-server
ENV SERVER_PORT=8080

COPY . $WORK_DIR
WORKDIR $WORK_DIR

EXPOSE $SERVER_PORT
CMD ["./gradlew", "bootRun"]
