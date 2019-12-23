#!/bin/sh

# This script downloads and installs Java 11 in `/opt/java/java-11`. Use this script if your OS distribution does not
# provide Java 11 through the package manager.

set -x

INSTALL_DIR_HOME=/opt
INSTALL_DIR=${INSTALL_DIR_HOME}/java
JAVA_DOWNLOAD_LINK="https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz"

sudo mkdir -p ${INSTALL_DIR} \
  && pushd . \
  && cd ${INSTALL_DIR} \
  && sudo wget ${JAVA_DOWNLOAD_LINK} \
  && sudo tar zxf openjdk* \
  && sudo rm -f openjdk* \
  && sudo ln -s $(find . -type d -name "jdk-11*") java-11\
  && popd
