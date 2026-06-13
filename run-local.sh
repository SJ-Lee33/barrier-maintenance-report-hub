#!/bin/bash

set -a
source .env
set +a

./gradlew bootRun

# 앞으로 실행할 땐 ./run-local.sh