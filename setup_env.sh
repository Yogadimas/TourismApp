#!/bin/bash

echo "Setting up environment variables for CircleCI..."

echo "export API_KEY_NVD=$API_KEY_NVD" >> $BASH_ENV
echo "export BASE_URL_API=$BASE_URL_API" >> $BASH_ENV
echo "export BASE_URL_HOST=$BASE_URL_HOST" >> $BASH_ENV
echo "export CERT_SHA_256_1=$CERT_SHA_256_1" >> $BASH_ENV
echo "export CERT_SHA_256_2=$CERT_SHA_256_2" >> $BASH_ENV
echo "export CERT_SHA_256_3=$CERT_SHA_256_3" >> $BASH_ENV

source $BASH_ENV
