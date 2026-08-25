#!/bin/sh
set -e
export DB_URL="jdbc:$(echo "$DATABASE_URL" | sed 's#^postgres://#postgresql://#')"
exec java -jar /app/app.jar