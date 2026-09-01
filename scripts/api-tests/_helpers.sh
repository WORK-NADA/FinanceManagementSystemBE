#!/bin/bash
# =============================================================================
# _helpers.sh - Shared helper functions for the API test suite
# Uses Python3 for JSON parsing (jq not required)
# =============================================================================

API_URL="http://localhost:8080"
ADMIN_EMAIL="urvip249@gmail.com"
ADMIN_PASS="urviAK2005!"
CLIENT1_EMAIL="testclient1@example.com"
CLIENT1_PASS="TestPass@123"
CLIENT2_EMAIL="testclient2@example.com"
CLIENT2_PASS="TestPass@123"

# Global counters (per module)
declare -A MODULE_PASS
declare -A MODULE_FAIL

LOG_FILE="${LOG_FILE:-/dev/stderr}"

# Logging
log() {
    echo "$1" | tee -a "$LOG_FILE"
}

log_header() {
    log ""
    log "======================================================================"
    log "  $1"
    log "======================================================================"
}

log_sub() {
    log "  --- $1 ---"
}

# Python-based JSON extractor
# Usage: py_json_get <json_string> <dotted.key>
py_json_get() {
    local json="$1"
    local key="$2"
    echo "$json" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    keys = '$key'.split('.')
    for k in keys:
        data = data[k] if isinstance(data, dict) else None
        if data is None:
            break
    print(data if data is not None else '')
except:
    print('')
" 2>/dev/null
}

py_json_compact() {
    local json="$1"
    echo "$json" | python3 -c "
import sys, json
try:
    print(json.dumps(json.load(sys.stdin), separators=(',',':')))
except:
    sys.stdout.write(sys.stdin.read()[:300])
" 2>/dev/null
}

# Usage: TOKEN=$(login "email@x.com" "pass")
login() {
    local email="$1"
    local password="$2"
    local payload_file
    payload_file=$(mktemp)
    cat <<EOF > "$payload_file"
{"email":"$email","password":"$password"}
EOF
    local resp
    resp=$(curl -s -X POST "$API_URL/user/login" \
        -H "Content-Type: application/json" \
        -d "@$payload_file")
    rm -f "$payload_file"
    py_json_get "$resp" "data.accessToken"
}

login_get_refresh() {
    local email="$1"
    local password="$2"
    local payload_file
    payload_file=$(mktemp)
    cat <<EOF > "$payload_file"
{"email":"$email","password":"$password"}
EOF
    local resp
    resp=$(curl -s -X POST "$API_URL/user/login" \
        -H "Content-Type: application/json" \
        -d "@$payload_file")
    rm -f "$payload_file"
    py_json_get "$resp" "data.refreshToken"
}

# Core call_api function
# Usage: call_api METHOD PATH TOKEN|NONE PAYLOAD|NONE EXPECTED_STATUS TEST_NAME MODULE
# Sets LAST_BODY and LAST_STATUS after each call.
LAST_BODY=""
LAST_STATUS=""

call_api() {
    local method="$1"
    local path="$2"
    local token="$3"
    local payload="$4"
    local expected="$5"
    local test_name="$6"
    local module="$7"

    local out_file
    out_file=$(mktemp)

    local curl_args=(-s -o "$out_file" -w '%{http_code}' -X "$method" "$API_URL$path" -H "Content-Type: application/json")

    if [ -n "$token" ] && [ "$token" != "NONE" ]; then
        curl_args+=(-H "Authorization: Bearer $token")
    fi

    local payload_file=""
    if [ -n "$payload" ] && [ "$payload" != "NONE" ]; then
        payload_file=$(mktemp)
        cat <<EOF > "$payload_file"
$payload
EOF
        curl_args+=(-d "@$payload_file")
    fi

    LAST_STATUS=$(curl "${curl_args[@]}")
    LAST_BODY=$(cat "$out_file")
    rm -f "$out_file"
    if [ -n "$payload_file" ]; then rm -f "$payload_file"; fi

    if [ "$LAST_STATUS" = "$expected" ]; then
        log "[PASS] [$module] $test_name -> $LAST_STATUS"
        MODULE_PASS[$module]=$(( ${MODULE_PASS[$module]:-0} + 1 ))
    else
        if [ "$LAST_STATUS" = "500" ]; then
            log "[FAIL] [$module] $test_name -> Expected $expected, got $LAST_STATUS *** RAW 500 - POTENTIAL BUG ***"
        else
            log "[FAIL] [$module] $test_name -> Expected $expected, got $LAST_STATUS"
        fi
        local compact_body
        compact_body=$(py_json_compact "$LAST_BODY")
        log "       Body: $compact_body"
        MODULE_FAIL[$module]=$(( ${MODULE_FAIL[$module]:-0} + 1 ))
    fi
}

extract_public_id() {
    py_json_get "$LAST_BODY" "data.publicId"
}

extract_field() {
    local field="$1"
    py_json_get "$LAST_BODY" "data.$field"
}

print_summary() {
    local modules=("$@")
    log ""
    log "======================================================================"
    log "  FINAL TEST SUMMARY"
    log "======================================================================"
    local total_pass=0
    local total_fail=0
    for mod in "${modules[@]}"; do
        local p=${MODULE_PASS[$mod]:-0}
        local f=${MODULE_FAIL[$mod]:-0}
        total_pass=$(( total_pass + p ))
        total_fail=$(( total_fail + f ))
        log "  $(printf '%-22s' "$mod") PASS=$p | FAIL=$f"
    done
    log "----------------------------------------------------------------------"
    log "  $(printf '%-22s' "TOTAL") PASS=$total_pass | FAIL=$total_fail"
    log "======================================================================"
}
