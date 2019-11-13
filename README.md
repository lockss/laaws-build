# Introduction

Thib's little POM-based LAAWS build tool.

**This project is not intended for public consumption.**

# Usage

## One-time bootstrapping:
1. `bin/clone`
2. `bin/foreach git checkout develop`
3. `cd lockss-parent-pom && mvn install`

## Running the build
4. `bin/foreach git pull`
5. `mvn test`