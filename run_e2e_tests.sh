#!/bin/bash

set -e  # Stop on first error

AVD_NAME="Pixel_9_API_36"
EMULATOR_SERIAL="emulator-5554"

echo "==> Available AVDs:"
emulator -list-avds

echo "==> Starting emulator: $AVD_NAME"
emulator -avd "$AVD_NAME" -no-window &   # -no-window runs headless; remove flag if you want to see it
EMULATOR_PID=$!

echo "==> Waiting for emulator to boot..."
adb wait-for-device
adb -s "$EMULATOR_SERIAL" shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 2; done'
echo "==> Emulator ready."

echo "==> Running androidTests..."
./gradlew connectedAndroidTest

echo "==> Opening test report..."
open app/build/reports/androidTests/connected/debug/index.html

echo "==> Shutting down emulator..."
adb -s "$EMULATOR_SERIAL" emu kill
wait $EMULATOR_PID

echo "==> Done."