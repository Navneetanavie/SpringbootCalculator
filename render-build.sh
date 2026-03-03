#!/usr/bin/env bash
# Exit on error
set -o errexit

./mvnw clean package -DskipTests
