# Attendance terminal integration

FaithOS accepts terminal check-ins at `POST /api/device/attendance/check-in`.

An administrator must first register the terminal with `POST /api/attendance-devices`. FaithOS returns a unique `deviceId` and secret once. Store them in the connector and send them as `X-Device-Id` and `X-Device-Secret`. Secrets are stored as BCrypt hashes and can be rotated or revoked independently.

```json
{
  "memberCode": "M-001"
}
```

The church and timestamp cannot be supplied by the connector. FaithOS derives the church from the registered device and always uses its server time. A scan at the configured cutoff is `ON_TIME`; a later scan is `LATE`. Repeated scans return the first check-in instead of creating duplicates. Failed authentication is rate-limited, audited, and locks a registered device for 15 minutes after five failures.

Most fingerprint terminals require a small vendor-specific connector to read their logs and call this endpoint. To configure that connector, obtain the exact brand, model number, and communication mode (cloud API, ADMS, TCP/IP, USB export, or SDK) from the label or manual. FaithOS does not receive or store fingerprint templates; the terminal identifies the person and sends only their member ID and timestamp.
