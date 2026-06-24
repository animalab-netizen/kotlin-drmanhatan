# Publication Guide

This document describes the publication strategy for `kotlin-drmanhatan`.

## Current Coordinates

- group: `io.github.animalab-netizen`
- artifact: `kotlin-drmanhatan`
- version: `0.1.1`
- repository: `github.com/animalab-netizen/kotlin-drmanhatan`

## Current Configuration

The library is configured for:

- local publication with `publishToMavenLocal`
- remote publication to a Maven repository provided through Gradle properties or CI secrets
- generation of JAR, sources JAR, javadoc JAR, POM and Gradle module metadata
- maintainer, license, organization and SCM metadata in the published POM

## Source Repository

- repository: [github.com/animalab-netizen/kotlin-drmanhatan](https://github.com/animalab-netizen/kotlin-drmanhatan)
- scm url: `https://github.com/animalab-netizen/kotlin-drmanhatan`
- scm connection: `scm:git:https://github.com/animalab-netizen/kotlin-drmanhatan.git`
- scm developer connection: `scm:git:git@github.com:animalab-netizen/kotlin-drmanhatan.git`

## Local Validation

Run the library tests:

```bash
./gradlew :lib:test
```

Validate local Maven publication:

```bash
./gradlew :lib:publishToMavenLocal
```

Validate external consumption from the separate consumer:

```bash
cd ../kotlin-drmanhatan-consumer
./scripts/run-consumer.sh
```

## Remote Publication

The publication target is configured through:

- `publicationRepositoryUrl`
- `publicationRepositoryUsername`
- `publicationRepositoryPassword`

Those values can be provided through `~/.gradle/gradle.properties`, project-local untracked overrides, or CI secrets exported as:

- `ORG_GRADLE_PROJECT_publicationRepositoryUrl`
- `ORG_GRADLE_PROJECT_publicationRepositoryUsername`
- `ORG_GRADLE_PROJECT_publicationRepositoryPassword`

Publish to the configured remote repository with:

```bash
./gradlew :lib:publishMavenJavaPublicationToMavenRepository
```

## GitHub Repository Secrets

The release workflow expects these repository secrets in `animalab-netizen/kotlin-drmanhatan`:

- `PUBLICATION_REPOSITORY_URL`
- `PUBLICATION_REPOSITORY_USERNAME`
- `PUBLICATION_REPOSITORY_PASSWORD`

If `PUBLICATION_REPOSITORY_URL` is missing, `.github/workflows/release.yml` now fails instead of silently skipping publication. This avoids creating a successful release pipeline that did not actually publish the artifact.

## Release Checklist

1. Run `./gradlew :lib:test`
2. Run `./gradlew :lib:publishToMavenLocal`
3. Validate external consumption from `kotlin-drmanhatan-consumer`
4. Confirm CI is green in `.github/workflows/ci.yml`
5. Confirm version in `gradle.properties`
6. Confirm remote repository credentials are available when needed
7. Create and push the release tag
8. Let `.github/workflows/release.yml` publish and create the GitHub release

## Operational Next Step

To make real publication work, define the target Maven repository owned by AnimaLab and set the three GitHub secrets above. Without that repository URL and credentials, the library is release-ready but not remotely publishable.

## Workflow Notes

- CI runs from `.github/workflows/ci.yml`
- tag releases run from `.github/workflows/release.yml`
- remote publication uses `PUBLICATION_REPOSITORY_URL`, `PUBLICATION_REPOSITORY_USERNAME` and `PUBLICATION_REPOSITORY_PASSWORD`
- GitHub Releases are created automatically for version tags
