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
- bundle generation for Sonatype Central Publisher API
- optional remote publication to a Maven repository provided through Gradle properties or CI secrets
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
- `signingKeyId`
- `signingKey`
- `signingPassword`

Those values can be provided through `~/.gradle/gradle.properties`, project-local untracked overrides, or CI secrets exported as:

- `ORG_GRADLE_PROJECT_publicationRepositoryUrl`
- `ORG_GRADLE_PROJECT_publicationRepositoryUsername`
- `ORG_GRADLE_PROJECT_publicationRepositoryPassword`
- `ORG_GRADLE_PROJECT_signingKeyId`
- `ORG_GRADLE_PROJECT_signingKey`
- `ORG_GRADLE_PROJECT_signingPassword`

For the current AnimaLab setup, `kotlin-drmanhatan` should eventually use the same organization-owned Sonatype Central and signing material already used for `kotlin-ode`, but those values should stay outside source control for now.

Build a Sonatype Central bundle locally with:

```bash
./gradlew :lib:packageCentralBundle
```

## GitHub Repository Secrets

The release workflow expects these repository secrets in `animalab-netizen/kotlin-drmanhatan`:

- `PUBLICATION_REPOSITORY_URL`
- `PUBLICATION_REPOSITORY_USERNAME`
- `PUBLICATION_REPOSITORY_PASSWORD`
- `SIGNING_KEY_ID`
- `SIGNING_KEY`
- `SIGNING_PASSWORD`

If token credentials or signing material are missing, `.github/workflows/release.yml` now fails instead of silently skipping publication. This avoids creating a successful release pipeline that did not actually publish the artifact.

## Sonatype Central Flow

This repository now uses Gradle's built-in `maven-publish` plugin to create a signed Maven bundle, then uploads that bundle to Sonatype Central Publisher API.

1. publish `mavenJava` to the local `CentralBundle` repository under `lib/build/central-bundle/repository`
2. sign the publication with PGP
3. package the repository tree into `lib/build/central-bundle/dist/kotlin-drmanhatan-<version>-central-bundle.zip`
4. upload the bundle to `https://central.sonatype.com/api/v1/publisher/upload`
5. poll the deployment until Sonatype reports `PUBLISHED`

This avoids the older OSSRH compatibility `manual/*` endpoints, which were returning `Endpoint ... not supported` for this repository setup.

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

To make real publication work, keep `kotlin-drmanhatan` on the same Sonatype Central organization and token model as `kotlin-ode`, and set the six GitHub secrets above. Without token credentials and signing material, the library is release-ready but not publishable to Central.

## Workflow Notes

- CI runs from `.github/workflows/ci.yml`
- tag releases run from `.github/workflows/release.yml`
- Central publication uses `PUBLICATION_REPOSITORY_USERNAME`, `PUBLICATION_REPOSITORY_PASSWORD`, `SIGNING_KEY_ID`, `SIGNING_KEY` and `SIGNING_PASSWORD`
- `PUBLICATION_REPOSITORY_URL` remains declared for parity with `kotlin-ode` and optional repository-based publication
- GitHub Releases are created automatically for version tags
