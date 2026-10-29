#!/usr/bin/env bash
set -euo pipefail

# This debugging script only works locally on its dedicated machine

# pipeline variables
export ANDROID_COMPILE_SDK="37"
export ANDROID_SDK_TOOLS="15859902"
export ANDROID_BUILD_TOOLS="37.0.0"


#before script
apt-get --quiet update --yes
apt-get --quiet install --yes wget unzip
apt-get install -y git

export ANDROID_HOME="${PWD}/android-sdk-root"

install -d $ANDROID_HOME

#Code added for debugging purposes: Directory Handling, Start
rm -rf "$ANDROID_HOME/cmdline-tools/latest"
rm -rf "$ANDROID_HOME/cmdline-tools-temp"
mkdir -p "$ANDROID_HOME/cmdline-tools"
#Code added for debugging purposes: Directory Handling, End

wget --no-verbose --output-document=$ANDROID_HOME/cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_TOOLS}_latest.zip
unzip -q -d "$ANDROID_HOME/cmdline-tools-temp" "$ANDROID_HOME/cmdline-tools.zip"
mkdir -p "$ANDROID_HOME/cmdline-tools/latest"
mv "$ANDROID_HOME/cmdline-tools-temp/cmdline-tools"/* "$ANDROID_HOME/cmdline-tools/latest/"
rm -rf "$ANDROID_HOME/cmdline-tools-temp"
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

echo "android --version"
android --version
echo "sdkmanager --version"
sdkmanager --version

yes | sdkmanager --licenses --channel=3 > /dev/null || true

android sdk install --canary "platforms;android-${ANDROID_COMPILE_SDK}" "platform-tools" "build-tools;${ANDROID_BUILD_TOOLS}"

chmod +x ./gradlew


#debug tests:
#./gradlew -Pci --console=plain :app:testDebug
echo "Debug tests done"

#Release variables: Start
# The release variables are retrieved from the local 'Keychain Access' tool and
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
export GOOGLE_PLAY_SERVICE_ACCOUNT_JSON=$(security find-generic-password \
  -a "$USER" \
  -s "GOOGLE_PLAY_SERVICE_ACCOUNT_JSON" \
  -w)
#Release variables: End

#Release Build and Fastlane: Start
./gradlew assemble bundleRelease
apt-get --quiet install --yes ruby-full build-essential libssl-dev zlib1g-dev
gem install fastlane --no-document
fastlane internal
#Release Build and Fastlane: End
mv app/build/outputs/apk/release/app-release.apk app/build/outputs/apk/release/sugarCounter-${CI_COMMIT_SHORT_SHA}-release.apk
mv app/build/outputs/bundle/release/app-release.aab app/build/outputs/bundle/release/sugarCounter-${CI_COMMIT_SHORT_SHA}-release.aab

#interactive shell
echo "Setup complete. Entering interactive shell..."
exec bash