#!/bin/sh

# This script runs the integration tests in Bitbucket pipelines (see `bitbucket-pipeline.yml`).

# The AWS variables must exist as environment variables in Bitbucket.
./gradlew --build-cache integrationTest -PAWS_ACCESS_KEY=$AWS_ACCESS_KEY -PAWS_SECRET_KEY=$AWS_SECRET_KEY
