package com.efluid.jdepsanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class JDepsAnalyzerTest {

  @Test
  public void shouldReturnComOrganisationTestWhenParsingTheRepresentationOfThePackage() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("com.organisation.test -> com.organisation.toto  toto.jar");
    assertEquals("com.organisation.test", packages.stream().findAny().get().getName());
  }

  @Test
  public void shouldReturnIsOkPourIsolationWhenParsingAPackageAThatDoesntDependenOnAPackageDependingsOnA() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("com.organisation.test -> com.organisation.toto  toto.jar");
    assertEquals(true, packages.stream().findAny().get().isOkForIsolation());
  }

  @Test
  public void shouldReturn2PackagesWhenParsingARepresentationOf2Packages() {
    String representation = "com.organisation.test -> com.organisation.toto  toto.jar\ncom.organisation.toto -> com.organisation.tata tata.jar";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(2, packages.size());
  }

  @Test
  public void shouldReturnFalseForIsolationWhenParsingACycleBetween2Packages() {
    String representation = "com.organisation.test -> com.organisation.toto  toto.jar\ntoto.jar -> not found\ncom.organisation.toto -> com.organisation.test default";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(2, packages.size());
    assertTrue(packages.stream().allMatch(packageRepresentation -> !packageRepresentation.isOkForIsolation()));
  }

  @Test
  public void shouldReturnFalseForIsolationWhenParsingACycleBetween3Packages() {
    String representation = "com.organisation.test -> com.organisation.toto  toto.jar\ntoto.jar -> not found\ncom.organisation.toto -> com.organisation.tata tata.jar\ntata.jar -> not found\ncom.organisation.tata -> com.organisation.test default";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(3, packages.size());
    assertTrue(packages.stream().allMatch(packageRepresentation -> !packageRepresentation.isOkForIsolation()));
  }


  @Test
  public void shouldReturnComOrganisationTestWhenParsingWithDepth3TheRepresentationOfASubPackage() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("test.jar -> not found\ncom.organisation.test.subpackage -> com.organisation.toto  toto.jar", 3);
    assertEquals("com.organisation.test", packages.stream().findAny().get().getName());
  }

}
