#!/bin/sh

# This script runs the unit tests in Bitbucket pipelines (see `bitbucket-pipeline.yml`).
# The AWS variables must exist as environment variables in Bitbucket.

./gradlew --build-cache unitTest -PAWS_ACCESS_KEY=$AWS_ACCESS_KEY -PAWS_SECRET_KEY=$AWS_SECRET_KEY
