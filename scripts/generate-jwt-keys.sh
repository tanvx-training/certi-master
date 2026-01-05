#!/bin/bash

# Script to generate RSA key pair for JWT signing
# Run this script once and use the same keys for all services

echo "Generating RSA key pair for JWT..."

# Generate private key
openssl genrsa -out jwt-private.pem 2048

# Generate public key from private key
openssl rsa -in jwt-private.pem -pubout -out jwt-public.pem

# Convert to PKCS8 format (required by Java)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in jwt-private.pem -out jwt-private-pkcs8.pem

echo ""
echo "Keys generated successfully!"
echo ""
echo "=== PRIVATE KEY (for auth-service only) ==="
echo "Set this as JWT_PRIVATE_KEY environment variable:"
echo ""
cat jwt-private-pkcs8.pem | grep -v "BEGIN\|END" | tr -d '\n'
echo ""
echo ""
echo "=== PUBLIC KEY (for all services) ==="
echo "Set this as JWT_PUBLIC_KEY environment variable:"
echo ""
cat jwt-public.pem | grep -v "BEGIN\|END" | tr -d '\n'
echo ""
echo ""
echo "=== Usage ==="
echo "1. Copy the private key (single line) and set as JWT_PRIVATE_KEY"
echo "2. Copy the public key (single line) and set as JWT_PUBLIC_KEY"
echo "3. Configure in application.yml or as environment variables"
echo ""
echo "Example .env file:"
echo "JWT_PRIVATE_KEY=<private-key-base64>"
echo "JWT_PUBLIC_KEY=<public-key-base64>"

# Cleanup
rm -f jwt-private.pem jwt-public.pem jwt-private-pkcs8.pem
