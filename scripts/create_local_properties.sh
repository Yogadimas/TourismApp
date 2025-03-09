#!/bin/bash

echo "Creating local.properties file..."

cat <<EOL > local.properties
sdk.dir=$ANDROID_SDK_ROOT
apiKeyNvd=$API_KEY_NVD
baseUrlApi=$BASE_URL_API
baseUrlHost=$BASE_URL_HOST
certSha256_1=$CERT_SHA_256_1
certSha256_2=$CERT_SHA_256_2
certSha256_3=$CERT_SHA_256_3
EOL

echo "local.properties created successfully!"
