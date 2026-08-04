#!/usr/bin/env sh
set -eu

GRADLE_VERSION=9.3.1
CACHE_DIR="${GRADLE_USER_HOME:-$HOME/.gradle}/vehylo-distributions/gradle-$GRADLE_VERSION"
GRADLE_BIN="$CACHE_DIR/gradle-$GRADLE_VERSION/bin/gradle"

if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$CACHE_DIR"
  ARCHIVE="$CACHE_DIR/gradle-$GRADLE_VERSION-bin.zip"
  URL="https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip"
  echo "Scaricamento Gradle $GRADLE_VERSION..." >&2
  if command -v curl >/dev/null 2>&1; then
    curl -fL "$URL" -o "$ARCHIVE"
  elif command -v wget >/dev/null 2>&1; then
    wget -O "$ARCHIVE" "$URL"
  else
    echo "Serve curl o wget per scaricare Gradle." >&2
    exit 1
  fi
  if command -v unzip >/dev/null 2>&1; then
    unzip -q "$ARCHIVE" -d "$CACHE_DIR"
  else
    echo "Serve unzip per estrarre Gradle." >&2
    exit 1
  fi
fi

exec "$GRADLE_BIN" "$@"
