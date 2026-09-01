#!/bin/bash

# Configuration
API_URL="http://localhost:8080"
ADMIN_EMAIL="urvip249@gmail.com"
ADMIN_PASS="urviAK2005!"

# 1. Login as ADMIN to get the JWT token
echo "Logging in as ADMIN to acquire token..."
PAYLOAD_FILE=$(mktemp)
cat <<EOF > "$PAYLOAD_FILE"
{"email":"$ADMIN_EMAIL","password":"$ADMIN_PASS"}
EOF
LOGIN_RESPONSE=$(curl -s -X POST "$API_URL/user/login" \
  -H "Content-Type: application/json" \
  -d "@$PAYLOAD_FILE")
rm -f "$PAYLOAD_FILE"

TOKEN=$(echo "$LOGIN_RESPONSE" | python3 -c "import sys, json; print(json.load(sys.stdin).get('data', {}).get('accessToken', ''))" 2>/dev/null)

if [ -z "$TOKEN" ]; then
    echo "Failed to retrieve access token. Response was:"
    echo "$LOGIN_RESPONSE"
    exit 1
fi
echo "Admin token acquired successfully."

# 2. Register the new test client
echo "Registering CLIENT 1 (testclient1@example.com)..."
PAYLOAD_FILE=$(mktemp)
cat <<EOF > "$PAYLOAD_FILE"
{
  "ownerName": "Test Client One",
  "username": "testclient1",
  "email": "testclient1@example.com",
  "password": "TestPass@123",
  "mobileNumber": "9876543210",
  "role": "CLIENT",
  "userAddress": { "houseNo": "12", "societyName": "Test Society", "area": "Test Area", "city": "Ahmedabad", "pincode": "380001", "state": "Gujarat", "country": "India" }
}
EOF
curl -s -X POST "$API_URL/admin/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "@$PAYLOAD_FILE" 
rm -f "$PAYLOAD_FILE"
echo "Registering CLIENT 2 (testclient2@example.com)..."
PAYLOAD_FILE=$(mktemp)
cat <<EOF > "$PAYLOAD_FILE"
{
  "ownerName": "Test Client Two",
  "username": "testclient2",
  "email": "testclient2@example.com",
  "password": "TestPass@123",
  "mobileNumber": "9876543211",
  "role": "CLIENT",
  "userAddress": { "houseNo": "12", "societyName": "Test Society", "area": "Test Area", "city": "Ahmedabad", "pincode": "380001", "state": "Gujarat", "country": "India" }
}
EOF
curl -s -X POST "$API_URL/admin/register" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "@$PAYLOAD_FILE" 
rm -f "$PAYLOAD_FILE"

echo "Done! You can now log into the frontend with:"
echo "Email: testclient1@example.com | testclient2@example.com"
echo "Password: TestPass@123"
