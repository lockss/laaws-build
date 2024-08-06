# LOCKSS 2.0 BUILD SCRIPTS

This project offers a collection of scripts and maven pom to allow for the build and release of LOCKSS 2.0

## Note on branches
The `develop` branch should be used for current build and is for ongoing development.

# Usage

## One-time bootstrapping:
1. `bin/clone`
2. `bin/foreach git checkout develop`
3. `cd lockss-parent-pom && mvn install`

## Running the build
4. `bin/foreach git pull`
5. `mvn test`