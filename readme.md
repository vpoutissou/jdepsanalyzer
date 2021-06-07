# jdepsanalyzer
Mainly for automatically indicating if there's a cycle in package dependencies by analyzing jdeps output

The purpose is to easily know if a package can be separated from a big jar into its own module

## Technical informations
jdepsanalyzer is using Java 16 and JUnit 5.7.2 for running the tests

## Usage
Run jdeps on the jar and store the resut in a file.
Then call Launcher by passing the path to the file as an argument.

```
jdeps myjar.jar > dependencies.txt
java -jar jdepsanalyzer-0.0.1-SNAPSHOT "dependencies.txt"
```