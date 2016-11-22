package org.sonarsource.xml.custom;

import org.sonar.api.Plugin;
import org.sonarsource.xml.custom.rules.CreateIssuesOnXmlFilesSensor;
import org.sonarsource.xml.custom.rules.XmlRulesDefinition;

/**
 * This class is the entry point for all extensions. It is referenced in pom.xml.
 */
public class XmlCustomPlugin implements Plugin {

  @Override
  public void define(Context context) {
    // tutorial on rules
    context.addExtensions(XmlRulesDefinition.class, CreateIssuesOnXmlFilesSensor.class);
  }
}
