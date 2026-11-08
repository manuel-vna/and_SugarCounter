#!/usr/bin/env bash
set -euo pipefail

# This debugging script only works locally on its dedicated machine

# The release variables stored in 'Settings->CI/CD->Variables' are retrieved from the local 'Keychain Access' tool and
# stored as an environment variable of the current shell session
export KEY_ALIAS=$(security find-generic-password \
  -a "$USER" \
  -s "SUGAR_COUNTER_KEY_ALIAS" \
  -w)
export KEY_PASSWORD=$(security find-generic-password \
  -a "$USER" \
  -s "SUGAR_COUNTER_KEY" \
  -w)
export KEYSTORE_PASSWORD=$(security find-generic-password \
  -a "$USER" \
  -s "RELEASE_PLAY_CONSOLE_MVA_KEYSTORE" \
  -w)

# The secure files stored in 'Settings->CI/CD->Secure Files' are retrieved from a local directory
# which is added to the container as a mounted volume and the files are passed in as external-keys
docker run --rm -it --platform linux/amd64 \
  -v "$PWD":/workspace \
  -v "$HOME/MobileDev/FilesForLocalPipelineDebugging/":/external-keys \
  -w /workspace \
  -e "KEY_ALIAS=$KEY_ALIAS" \
  -e "KEY_PASSWORD=$KEY_PASSWORD" \
  -e "KEYSTORE_PASSWORD=$KEYSTORE_PASSWORD" \
  -e "FASTLANE_JSON_KEY=/external-keys/FASTLANE_JSON_KEY.json" \
  -e "KEYSTORE_FILE=/external-keys/KEYSTORE_FILE" \
  eclipse-temurin:21-jdk-jammy \
  bash gitlab-ci-debug_pipelineMirror.sh