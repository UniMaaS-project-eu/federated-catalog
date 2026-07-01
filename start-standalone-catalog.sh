#!/bin/bash
# Start Provider Catalog Server

JAVA_PROPS=$(grep -v '^\s*$' deployment/standalone-catalog.env | grep -v '^#' | sed 's/^/-D/')
echo $JAVA_PROPS

java $JAVA_PROPS -Dweb.http.default.enabled=true -jar build/libs/standalone-catalog.jar --log-level=debug
