# Publication Guide

## Current State

`kotlin-drmanhatan` is structured as a standalone JVM library artifact.

Current coordinates:

- group: `io.github.animalab-netizen`
- artifact: `kotlin-drmanhatan`
- version: `0.1.1`
- repository: `github.com/animalab-netizen/kotlin-drmanhatan`

## Distribution Model

The artifact is intended for:

- direct Maven consumption as the public Kotlin DrManhatan runtime
- consumption by validation projects such as `kotlin-drmanhatan-consumer`
- publication to GitHub Packages and optionally Sonatype

## Installation

```kotlin
implementation("io.github.animalab-netizen:kotlin-drmanhatan:0.1.1")
```

## Release Checklist

1. Run `./gradlew :lib:test`
2. Run `./gradlew :lib:publishToMavenLocal`
3. Validate external consumption from `kotlin-drmanhatan-consumer`
4. Update `CHANGELOG.md`
5. Confirm version in Gradle properties
6. Commit release metadata
7. Create and push tag `v0.1.1`
8. Confirm GitHub Packages publication credentials
9. Confirm Sonatype and signing credentials when Maven Central publication is intended

## Workflow Notes

- CI runs from `.github/workflows/ci.yml`
- tag releases run from `.github/workflows/release.yml`
- GitHub Packages publication uses `GITHUB_TOKEN`
- Sonatype publication requires `OSSRH_USERNAME`, `OSSRH_PASSWORD`, `SIGNING_KEY` and `SIGNING_PASSWORD`
- GitHub Releases are created automatically for version tags
