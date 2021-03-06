#!/bin/bash

dir=$(cd -P -- "$(dirname -- "$0")" && pwd -P)

SK=$dir/../build/sk
PK=$dir/../build/pk

if [ ! -e "$SK" ]; then
    # Generate a private key in PEM format
    openssl genrsa -out ${SK}.pem 2048
    # Convert it to DER for Java
    openssl pkcs8 -topk8 -inform PEM -outform DER -in ${SK}.pem -out ${SK}.der -nocrypt
    # Calculate corresponding public key, saved in PEM format
    openssl rsa -in ${SK}.pem -pubout -outform PEM -out ${PK}.pem
    # Calculate corresponding public key, saved in DER format
    openssl rsa -in ${SK}.pem -pubout -outform DER -out ${PK}.der
    # Remove leftover files
fi
rm -f ${SK}.pem
rm -f ${PK}.pem
