# LOCKSS 2.0 Build Environment

This project brings together Git repositories, Maven infrastructure and scripts to build the LOCKSS 2.x system.

## Prerequisites

*   Java Development Kit (JDK) 17
*   [Apache Maven](https://maven.apache.org/) 3.9
*   [yq](https://github.com/mikefarah/yq) version 4
    *   Fedora Linux 42/41/Rawhide, OpenSUSE Leap 15.6/Tumbleweed: the `yq` OS package
    *   Arch Linux: the `go-yq` OS package
    *   AlmaLinux OS/CentOS Stream/RHEL/Rocky Linux 9 or 10: the `yq` [EPEL](https://docs.fedoraproject.org/en-US/epel/) package
    *   Other Linux distributions: with a [binary download](https://github.com/mikefarah/yq/?tab=readme-ov-file#download-the-latest-binary), with [Wget](https://github.com/mikefarah/yq/?tab=readme-ov-file#wget), with [Homebrew](https://github.com/mikefarah/yq/?tab=readme-ov-file#macos--linux-via-homebrew), with [Snap](https://github.com/mikefarah/yq/?tab=readme-ov-file#linux-via-snap)
    *   MacOS: `yq` with [Homebrew](https://github.com/mikefarah/yq/?tab=readme-ov-file#macos--linux-via-homebrew) or [MacPorts](https://github.com/mikefarah/yq/?tab=readme-ov-file#macports)
    *   [All installation instructions](https://github.com/mikefarah/yq/?tab=readme-ov-file#install)
*   Optional prerequisites
    *   `etags` from Emacs
    *   `dot` from Graphviz
    *   `ps2pdf` from Ghostscript

In LOCKSS-related Git repositories, ongoing development occurs in the `develop` branch (the `master` branch is for stable releases only).

## Usage

### One-Time Bootstrapping

1.  `bin/clone develop`
2.  `( cd lockss-parent-pom && mvn install )`

### Running the Build

1. `bin/foreach git pull`
2. `mvn clean`
3. `mvn install -DskipTests=true`
4.  `mvn test`
