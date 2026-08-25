#!/bin/sh
set -e
export DB_URL="jdbc:$(echo "$DATABASE_URL" | sed -E 's#^postgres(ql)?://[^@]*@#postgresql://#')"
exec java -jar /app/app.jar