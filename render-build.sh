#!/usr/bin/env bash
# Exit on error
set -o errexit

# Ensure the maven wrapper has execution rights on Linux containers before running
chmod +x ./mvnw
./mvnw clean package -DskipTests
