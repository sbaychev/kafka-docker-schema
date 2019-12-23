#!/bin/sh

# This script runs the SonarCloud analysis in Bitbucket pipelines (see `bitbucket-pipeline.yml`).
# The AWS variables must exist as environment variables in Bitbucket, as well as a SONAR_TOKEN variable.

# This script will fail if your project doesn't already exist in Sonarcloud

./gradlew --build-cache sonarqube -x test \
              -Dsonar.projectKey=$BITBUCKET_WORKSPACE_$BITBUCKET_REPO_SLUG \
              -Dsonar.organization=$SONAR_ORGANIZATION \
              -Dsonar.host.url=$SONAR_HOST \
              -Dsonar.login=$SONAR_TOKEN \
              -PAWS_ACCESS_KEY=$AWS_ACCESS_KEY -PAWS_SECRET_KEY=$AWS_SECRET_KEY