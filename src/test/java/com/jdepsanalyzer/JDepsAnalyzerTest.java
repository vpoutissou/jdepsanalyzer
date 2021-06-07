package com.jdepsanalyzer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

public class JDepsAnalyzerTest {

  @Test
  public void shouldReturnComOrganizationTestWhenParsingTheRepresentationOfThePackage() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("com.organization.test -> com.organization.toto  toto.jar");
    assertEquals("com.organization.test", packages.stream().findAny().get().getName());
  }

  @Test
  public void shouldReturnIsOkForIsolationWhenParsingAPackageAThatDoesntDependenOnAPackageDependingsOnA() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("com.organization.test -> com.organization.toto  toto.jar");
    assertEquals(true, packages.stream().findAny().get().isOkForIsolation());
  }

  @Test
  public void shouldReturn2PackagesWhenParsingARepresentationOf2Packages() {
    String representation = "com.organization.test -> com.organization.toto  toto.jar\ncom.organization.toto -> com.organization.tata tata.jar";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(2, packages.size());
  }

  @Test
  public void shouldReturnFalseForIsolationWhenParsingACycleBetween2Packages() {
    String representation = "com.organization.test -> com.organization.toto  toto.jar\ntoto.jar -> not found\ncom.organization.toto -> com.organization.test default";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(2, packages.size());
    assertTrue(packages.stream().allMatch(packageRepresentation -> !packageRepresentation.isOkForIsolation()));
  }

  @Test
  public void shouldReturnFalseForIsolationWhenParsingACycleBetween3Packages() {
    String representation = "com.organization.test -> com.organization.toto  toto.jar\ntoto.jar -> not found\ncom.organization.toto -> com.organization.tata tata.jar\ntata.jar -> not found\ncom.organization.tata -> com.organization.test default";
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze(representation);
    assertEquals(3, packages.size());
    assertTrue(packages.stream().allMatch(packageRepresentation -> !packageRepresentation.isOkForIsolation()));
  }


  @Test
  public void shouldReturnComOrganizationTestWhenParsingWithDepth3TheRepresentationOfASubPackage() {
    Set<PackageRepresentation> packages = new JDepsAnalyzer().analyze("test.jar -> not found\ncom.organization.test.subpackage -> com.organization.toto  toto.jar", 3);
    assertEquals("com.organization.test", packages.stream().findAny().get().getName());
  }

}
