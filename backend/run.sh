#!/usr/bin/env bash
set -euo pipefail
mkdir -p out
javac -d out src/Main.java
echo "Starting Java backend..."
java -cp out Main
