#!/bin/sh

# This script builds the final artefact that will be packaged in the Docker image. It is run automatically run by
# Bitbucket (see `bitbucket-pipeline.yml`).
# The AWS variables must exist as environment variables in Bitbucket.

./gradlew --build-cache bootJar -PAWS_ACCESS_KEY=$AWS_ACCESS_KEY -PAWS_SECRET_KEY=$AWS_SECRET_KEY
