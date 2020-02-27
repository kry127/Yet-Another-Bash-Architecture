# Simple Gradle CI

![CI](https://github.com/kry127/yacli/workflows/CI/badge.svg)

Stupidly simple :)
This is a template project that uses GitHub CI to run JUnit 4 tests on GitHub instance.


### File structure:
1. `.github/workflows/blank.yml` -- this one is shell script code running on instance
2. `build.gradle` -- gradle file of project
3. `src/main` -- source code, `src/test` -- test source code
4. et cetera ...


## How to use:
Running first time, uncomment lines in file `.github/workflows/blank.yml` that calling `gradle wrapper`. Then, you would probably like to comment them again to speed up test running on instance...
