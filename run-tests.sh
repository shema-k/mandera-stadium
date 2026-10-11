#!/usr/bin/env bash
# Compiles and runs the test suite. Needs only a JDK and the bundled H2 driver.
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_DIR="$ROOT_DIR/build/test-classes"
LIB_DIR="$ROOT_DIR/lib"
cd "$ROOT_DIR"

rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"

# --release 17 and -encoding UTF-8 match the settings in pom.xml, so the tests
# check the same code NetBeans builds. Without UTF-8 the Luganda and Kiswahili
# words could be mangled rather than fail loudly.
find "$ROOT_DIR/src/main/java" -name '*.java' -print0 \
  | xargs -0 javac --release 17 -encoding UTF-8 -cp "$LIB_DIR/*" -d "$OUT_DIR"
find "$ROOT_DIR/src/test/java" -name '*.java' -print0 \
  | xargs -0 javac --release 17 -encoding UTF-8 -cp "$OUT_DIR:$LIB_DIR/*" -d "$OUT_DIR"

java -cp "$OUT_DIR:$LIB_DIR/*" com.stadium.booking.TestRunner
