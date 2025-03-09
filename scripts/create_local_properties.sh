#!/bin/bash
echo "Writing local.properties..."

cat <<EOL > local.properties
API_KEY_NVD=${API_KEY_NVD}
BASE_URL_API=${BASE_URL_API}
BASE_URL_HOST=${BASE_URL_HOST}
CERT_SHA_256_1=${CERT_SHA_256_1}
CERT_SHA_256_2=${CERT_SHA_256_2}
CERT_SHA_256_3=${CERT_SHA_256_3}
EOL

echo "local.properties created:"
cat local.properties
