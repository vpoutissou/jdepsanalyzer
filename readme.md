# jdepsanalyzer
Mainly for automatically indicating if there's a cycle in package dependencies by analyzing jdeps output

The purpose is to easily know if a package can be separated from a big jar into its own module

##Usage
Run jdeps on the jar and storethe resut in a file.
Then call call Launcher by passing the path to the file as an argument.

```
jdeps myjar.jar > dependencies.txt
java -cp jdepsanalyzer-0.0.1-SNAPSHOT com.jdepsanalyzer.Launcher "dependencies.txt"
```