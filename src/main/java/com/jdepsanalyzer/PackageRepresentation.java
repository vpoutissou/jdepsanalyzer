package com.jdepsanalyzer;

import java.util.List;
import java.util.Set;

public interface PackageRepresentation {

  /**
   * @return the name of the package
   */
  String getName();

  /**
   * @return the jar name
   */
  String getJarName();

  Set<PackageRepresentation> getDependencies();

  boolean isOkForIsolation();

  /**
   * @return the first cycle found when checking for isolation
   */
  List<PackageRepresentation> getCycle();

}