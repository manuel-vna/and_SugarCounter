#!/usr/bin/env bash
set -euo pipefail

# This debugging script only works locally on its dedicated machine

# The release variables stored in 'Settings->CI/CD->Variables' are retrieved from the local 'Keychain Access' tool and
# stored as an environment variable of the current shell session
export SUGAR_COUNTER_KEY_ALIAS=$(security find-generic-password \
  -a "$USER" \
  -s "SUGAR_COUNTER_KEY_ALIAS" \
  -w)
export SUGAR_COUNTER_KEY=$(security find-generic-password \
  -a "$USER" \
  -s "SUGAR_COUNTER_KEY" \
  -w)
export RELEASE_PLAY_CONSOLE_MVA_KEYSTORE=$(security find-generic-password \
  -a "$USER" \
  -s "RELEASE_PLAY_CONSOLE_MVA_KEYSTORE" \
  -w)

# The secure files stored in 'Settings->CI/CD->Secure Files' are retrieved from a local directory
# which is added to the container as a mounted volume and the files are passed in as external-keys
docker run --rm -it --platform linux/amd64 \
  -v "$PWD":/workspace \
  -v "$HOME/MobileDev/FilesForLocalPipelineDebugging/":/external-keys \
  -w /workspace \
  -e "SUGAR_COUNTER_KEY_ALIAS=$SUGAR_COUNTER_KEY_ALIAS" \
  -e "SUGAR_COUNTER_KEY=$SUGAR_COUNTER_KEY" \
  -e "RELEASE_PLAY_CONSOLE_MVA_KEYSTORE=$RELEASE_PLAY_CONSOLE_MVA_KEYSTORE" \
  -e "GOOGLE_PLAY_SERVICE_ACCOUNT_JSON=/external-keys/GOOGLE_PLAY_SERVICE_ACCOUNT_JSON.json" \
  -e "SUGAR_COUNTER_KEY_FILE=/external-keys/releasePlayConsoleMva-keystore" \
  eclipse-temurin:21-jdk-jammy \
  bash gitlab-ci-debug_pipelineMirror.sh