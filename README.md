# brutus 
[![Build Status][travis-icon]][travis-page] [![Coverage Status][coverall-icon]][coverall-page]

## Overview

*'Brutus, so you forgot your password too?'*

Brutus is a [brute-force attack](https://en.wikipedia.org/wiki/Brute-force_attack) password cracker tool, 
which simply generates a sequence of words following the predefined character combination, 
and attempts to decrypt the file with each password until the correct one is found.

Needless to say, brute-force is *NOT* one of the most efficient algorithm out there, so 
keep in mind that this tool is limited to be functional with simple passwords.


## Feature

Brutus currently supports the following file formats:
  * `*.zip`
  * `*.pdf`
  * `*.txt`

Note that `*.txt` is to be used for testing purpose, which tries to match the clear-text value 
written in the text file. This is intended to be used for measuring the speed of password generation.

Complexity of the password can be optionally specified, such as min/max number of characters and 
types of characters (lower-case, upper-case, numbers, etc).

Execution parameters can also be specified, which includes the number of concurrent threads to use, 
and time-out in case if the password is not found within the specified time interval.

See the [Configuration](#configuration) section of this documentation for the list of configurable parameters.


## Usage

### Prerequisites
  * JDK 1.8 or later
  * Maven 3 or later
  * Internet connection for downloading dependencies.

### Preparation

Download the sources, and build the project to sanity check the environment. 
Make sure that the build and test passes.

```bash
git clone "https://github.com/after-the-sunrise/brutus.git" "brutus"

cd brutus && mvn clean test
```

### Execution

Execute the application with [Exec Maven Plugin](http://www.mojohaus.org/exec-maven-plugin/), 
specifying the file to decrypt. The file name's extension (cf: `.txt`, `.zip`) 
is used to automatically determine and switch the decryption logic.

```bash
mvn exec:java -Dexec.args="-in /path/to/the/file.zip"
```

To configure the maximum length of password to attempt, specify the `-max` parameter. 

```bash
mvn exec:java -Dexec.args="-in /path/to/the/file.zip -max 8"
```

### Configuration

For the list of configurable parameters, refer to the '--help' option.

```bash
mvn exec:java -Dexec.args="--help"
```

[travis-page]:https://travis-ci.org/after-the-sunrise/brutus
[travis-icon]:https://travis-ci.org/after-the-sunrise/brutus.svg?branch=master
[coverall-page]:https://coveralls.io/github/after-the-sunrise/brutus?branch=master
[coverall-icon]:https://coveralls.io/repos/github/after-the-sunrise/brutus/badge.svg?branch=master
