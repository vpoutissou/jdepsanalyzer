package com.jdepsanalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class Launcher {

  public static void main(String[] args) {
    try {
      Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(Files.readAllLines(Path.of(args[0])), 4);
      packages.stream().collect(Collectors.groupingBy(pack -> pack.getJarName())).
      forEach((jarName, packs) -> {
        System.out.println(jarName);
        for (var pack : packs) {
          if(pack.isOkForIsolation()) {
            System.out.println(pack.getName() + " OK");
          } else {
            String cycle = pack.getCycle().stream().map(PackageRepresentation::getName)
                .collect(Collectors.joining("\t\n-> "));
            System.out.println(pack.getName() + " KO");
            System.out.println(cycle);
          }
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
