package com.jdepsanalyzer;

import java.util.List;
import java.util.Set;

public interface PackageRepresentation {

  String getName();

  String getJarName();

  Set<PackageRepresentation> getDependencies();

  boolean isOkForIsolation();

  List<PackageRepresentation> getCycle();

}