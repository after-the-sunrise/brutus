# brutus ![Travis-CI Build Status](https://travis-ci.org/after-the-sunrise/brutus.svg?branch=master)

## Overview

*'Brutus, so you forgot your password too?'*

Brutus is a [brute-force attack](https://en.wikipedia.org/wiki/Brute-force_attack) password cracker tool, 
which simply generates a sequence of words following the predefined character combination, 
and attempts to decrypt the file with each password until the correct one is found.


## Feature Set
* Passwords
  * Any combination of standard ASCII characters.
  * Arbitrary length with configurable minimum/maximum characters.
  * Include/exclude certain types (lower-case, upper-case, numbers, etc) of characters.
* Supported formats
  * Text : Simply use the first line of file as password. Use for debugging and performance testing.
  * Zip : Uses either [Zip4j](http://www.lingala.net/zip4j/) or local system's `unzip` command.
* Etc
  * Configurable number of threads to use.
  * Configurable give-up time.


## Usage

### Prerequisites
  * JDK 1.8 or later
  * Maven 3 or later
  * Internet connection for downloading dependencies.

### Preparation

Download the sources, and build the project to sanity check the environment. 
Make sure that the build and test passes.

```
cd /path/to/brutus

mvn clean package
```

### Execution

Execute the application with [Exec Maven Plugin](http://www.mojohaus.org/exec-maven-plugin/), 
specifying the file to decrypt. Note that the file name's extension (cf: `.txt`, `.zip`) 
is used to automatically switch the decryption logic per file type.

```
mvn exec:java -Dexec.args="-in /path/to/the/file.zip"
```

### Configuration

For the list of configurable parameters, refer to the '--help' option.

```
mvn exec:java -Dexec.args="--help"
```
