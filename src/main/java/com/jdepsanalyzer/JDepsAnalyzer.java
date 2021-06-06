package com.jdepsanalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JDepsAnalyzer {

  public Set<PackageRepresentation> analyze(String string) {
    return analyze(string, 0);
  }

  public Set<PackageRepresentation> analyze(String string, int packageDepth) {
    return analyze(Arrays.asList(string.split("\n")), packageDepth);
  }

  public Set<PackageRepresentation> analyze(List<String> lines, int packageDepth) {
    String jarName = "default";
    List<String> jarLines = new ArrayList<>();
    Set<PackageRepresentation> packages = new HashSet<>();
    Map<String, Set<PackageRepresentationImplem>> packagesByJar = new HashMap<>();
    for(String line : lines) {
      if (line.matches(".*jar +->.*")) {
        String newJar = line.split("->")[0].strip();
        if (!newJar.equals(jarName)) {
          if (!jarLines.isEmpty()) {
            packages.addAll(analyzeLines(packagesByJar, jarName, jarLines.stream(), packageDepth));
            jarLines.clear();
          }
          jarName = newJar;
        }
      }
      else {
        jarLines.add(line);
      }
    }
    packages.addAll(analyzeLines(packagesByJar, jarName, jarLines.stream(), packageDepth));
    packages.stream()
            .map(PackageRepresentationImplem.class::cast)
            .forEach(PackageRepresentationImplem::checkIsolation);
    return packages;
  }

  private Set<PackageRepresentation> analyzeLines(Map<String, Set<PackageRepresentationImplem>> packages, String jarName, Stream<String> lines, int packageDepth) {
    Map<String, Set<String>> representations = lines
        .map(line -> line.split("->"))
        .collect(Collectors.groupingBy(tokens -> tokens[0].strip(), 
            Collectors.mapping(tokens -> tokens[1], Collectors.toSet())));
    return representations.entrySet().stream().map(entry -> {
      PackageRepresentationImplem implem = getPackage(packages, jarName, entry.getKey(), packageDepth);
      entry.getValue().forEach(dependency -> {
        String[] tokens = dependency.strip().split(" +");
        implem.addToDependency(getPackage(packages, tokens[1].strip(), tokens[0].strip(), packageDepth));
      });
      return implem;
    }).collect(Collectors.toSet());
  }

  private PackageRepresentationImplem getPackage(Map<String, Set<PackageRepresentationImplem>> packages, String jar, String name, int packageDepth) {
    String reviewedName = packageDepth == 0 ? name : filterPackageName(name, packageDepth);
    return packages.computeIfAbsent(jar, n -> new HashSet<>()).stream()
        .filter(p -> p.getName().equals(reviewedName))
        .findFirst()
        .orElseGet(() -> {
          PackageRepresentationImplem implem = new PackageRepresentationImplem(reviewedName, jar);
          packages.get(jar).add(implem);
          return implem;
        });
  }

  private String filterPackageName(String name, int packageDepth) {
    int count = 1;
    int currentIndex = name.indexOf('.');
    while(count < packageDepth && currentIndex != -1) {
      count++;
      currentIndex = name.indexOf('.', currentIndex + 1);
    }
    return currentIndex > 0 ? name.substring(0, currentIndex) : name;
  }

  private static class PackageRepresentationImplem implements PackageRepresentation {

    private boolean okForIsolation = false;
    private final String name;
    private final String jarName;
    private final Set<PackageRepresentation> dependencies;
    private final List<PackageRepresentation> cycle = new ArrayList<>();

    public PackageRepresentationImplem(String name, String jarName) {
      this(name, jarName, Set.of());
    }

    public PackageRepresentationImplem(String name, String jarName, Collection<PackageRepresentation> dependencies) {
      this.name = name;
      this.jarName = jarName;
      this.dependencies = new HashSet<>(dependencies);
      cycle.add(this);
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String getJarName() {
      return jarName;
    }

    public void addToDependency(PackageRepresentation dependency) {
      dependencies.add(dependency);
    }

    @Override
    public Set<PackageRepresentation> getDependencies() {
      return Collections.unmodifiableSet(dependencies);
    }

    @Override
    public List<PackageRepresentation> getCycle() {
      return cycle;
    }

    @Override
    public boolean isOkForIsolation() {
      return okForIsolation;
    }

    public void checkIsolation() {
      okForIsolation = checkIsolation(this, dependencies, new HashSet<>());
    }

    private boolean checkIsolation(PackageRepresentation searchedFor, Set<PackageRepresentation> dependencies, Set<PackageRepresentation> seenPackages) {
      if (dependencies.isEmpty()) {
        return true;
      }
      for (PackageRepresentation dependency : dependencies) {
        if (dependency.getDependencies().contains(searchedFor)) {
          cycle.add(dependency);
          cycle.add(searchedFor);
          return false;
        }
        Set<PackageRepresentation> nextDependencies = dependency.getDependencies().stream()
            .filter(dep -> !seenPackages.contains(dep))
            .collect(Collectors.toSet());
        seenPackages.addAll(nextDependencies);
        cycle.add(dependency);
        if (!checkIsolation(searchedFor, nextDependencies, seenPackages)) {
          return false;
        }
        cycle.remove(cycle.size() - 1);
      }
      return true;
    }

    @Override
    public String toString() {
      return name + "[" + jarName + "]";
    }
  }

}
