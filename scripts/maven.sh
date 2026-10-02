#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
# На этом Mac Java 17 установлена отдельно от системной Java 25.
if [[ -z "${JAVA_HOME:-}" && -d /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home ]]; then
  export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
fi
if [[ -f .env ]]; then
  set -a
  source .env
  set +a
fi
: "${DB_PASSWORD:?Создайте .env по образцу .env.example и задайте DB_PASSWORD}"
exec ./mvnw "$@"
