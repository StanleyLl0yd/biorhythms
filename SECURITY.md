# Security Policy

## Supported versions

Biorhythms follows a latest-release support model.

| Version | Supported |
| --- | --- |
| Latest published release | Yes |
| Older releases | No |

Security fixes are normally shipped in a new release rather than backported to older versions.

## Reporting a vulnerability

Use GitHub private vulnerability reporting for this repository when it is available. Do not disclose a suspected vulnerability, credential, signing material, token, private key, or other sensitive detail in a public issue, discussion, pull request, commit message, or CI log.

Include the affected version or commit, reproduction steps, expected impact, and relevant logs or screenshots with secrets removed. If the issue may expose credentials or signing material, stop testing after establishing the minimum evidence needed to report it safely.

If GitHub private vulnerability reporting is unavailable, contact the maintainer through the GitHub profile without publishing exploit details.

## Triage process

- Initial acknowledgement target: within 3 business days.
- Initial severity and scope assessment target: within 7 business days.
- Valid reports are reproduced when practical, assigned a severity, and tracked privately until a fix or mitigation is available.
- Critical and high-impact issues are prioritized over feature work.
- Disclosure timing is coordinated after affected users have a reasonable opportunity to update.

These are response targets, not a guarantee of a specific remediation date.

## Security scope

In scope:

- Android application code and packaged resources;
- birth-date preference handling, local notifications, and home-screen widget behavior;
- dependency and build-toolchain risks introduced by this repository;
- GitHub Actions, CI/CD, release signing, checksums, provenance, and release integrity;
- Android manifest, exported components, permissions, local data handling, and unintended network exposure;
- repository-secret exposure or supply-chain weaknesses caused by repository configuration.

Generally out of scope unless the repository directly causes or amplifies the issue:

- vulnerabilities in GitHub, Android, device firmware, app stores, or other third-party infrastructure;
- social engineering and phishing;
- denial-of-service requiring unrealistic local resource exhaustion;
- findings that require a rooted or already-compromised device and do not cross an additional Biorhythms trust boundary.

## Security model

Biorhythms intentionally keeps a small attack surface:

- the application does not declare the Android INTERNET permission;
- cleartext network traffic is disabled;
- there are no accounts, backend services, advertising SDKs, or analytics SDKs in the application;
- the stored birth date and other DataStore preferences are excluded from Android cloud backup;
- notification and widget PendingIntent instances are immutable;
- the exported widget configuration activity validates that the requested widget ID belongs to this app's provider;
- release signing material is supplied outside the repository;
- release APK/AAB signatures and the expected production signing-certificate fingerprint are verified before publication;
- release binary artifacts are checksumed and receive GitHub artifact attestations;
- GitHub Actions are SHA-pinned and workflow containers are digest-pinned;
- CodeQL, Android Lint, Semgrep, Gitleaks, Dependency Review, SonarCloud, and Dependabot provide layered automated checks.

Never commit a keystore, key.properties, private key, token, .env file, service-account credential, or other credential material.
