FROM openjdk:11-slim
MAINTAINER Tide <dev-java@tide.co>

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# When building the image, set these arguments.
#
# The build tag of the image. See bin/docker-build.sh
ARG build_tag=?
# The commit that was used to build the image. See bin/docker-build.sh
ARG build_commit=?

ENV BUILD_TAG=$build_tag
ENV BUILD_COMMIT=$build_commit

WORKDIR /app
RUN pwd
RUN ls -la /

ADD bin/run.sh /app
ADD build/libs/*.jar /app

EXPOSE 8080
CMD ["/app/run.sh"]